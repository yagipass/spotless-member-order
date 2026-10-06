package io.github.yagipass.memberorder;

import com.google.errorprone.annotations.Var;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.core.compiler.IProblem;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.AnnotationTypeDeclaration;
import org.eclipse.jdt.core.dom.AnnotationTypeMemberDeclaration;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.Initializer;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.RecordDeclaration;
import org.eclipse.jdt.core.dom.StructuralPropertyDescriptor;
import org.eclipse.jdt.core.dom.TypeDeclaration;

final class MemberSorter {

  private static final Map<String, String> COMPILER_OPTIONS = compilerOptions();

  private static final List<String> SPOTLESS_TOGGLE_MARKERS =
      List.of("spotless:off", "spotless:on");

  private final MemberOrder order;

  MemberSorter(MemberOrder order) {
    this.order = order;
  }

  String sort(String source) {
    if (SPOTLESS_TOGGLE_MARKERS.stream().anyMatch(source::contains)) {
      return source;
    }
    Optional<CompilationUnit> unit = parse(source);
    if (unit.isEmpty()) {
      return source;
    }
    String sorted = render(source, unit.get(), 0, source.length());
    return sorted.equals(source) || parse(sorted).isPresent() ? sorted : source;
  }

  private String render(String source, ASTNode node, int from, int to) {
    StringBuilder out = new StringBuilder(to - from);
    @Var int position = from;
    for (List<Member> slots : memberLists(node)) {
      if (!areDisjoint(slots, position, to)) {
        continue;
      }
      List<Member> arranged = isSortable(source, slots) ? sorted(slots) : slots;
      for (int i = 0; i < slots.size(); i++) {
        Member member = arranged.get(i);
        out.append(source, position, slots.get(i).start());
        out.append(render(source, member.declaration(), member.start(), member.end()));
        position = slots.get(i).end();
      }
    }
    return out.append(source, position, to).toString();
  }

  private static List<Member> sorted(List<Member> members) {
    List<Member> sorted = new ArrayList<>(members);
    sorted.sort(Comparator.comparingInt(Member::rank));
    return sorted;
  }

  private static boolean isSortable(String source, List<Member> members) {
    ASTNode body = members.getFirst().declaration().getParent();
    if (!isKnownBracedBody(body)) {
      return false;
    }
    int closingBrace = body.getStartPosition() + body.getLength() - 1;
    for (int i = 0; i < members.size(); i++) {
      Member member = members.get(i);
      int next = i + 1 < members.size() ? members.get(i + 1).start() : closingBrace;
      if (extendsPastItsLastLine(source, member)
          || !isWhitespace(source, member.end(), next)
          || !containsLineBreak(source, member.end(), next)) {
        return false;
      }
    }
    return true;
  }

  private static boolean extendsPastItsLastLine(String source, Member member) {
    BodyDeclaration declaration = member.declaration();
    return containsLineBreak(
        source, declaration.getStartPosition() + declaration.getLength(), member.end());
  }

  private static boolean isKnownBracedBody(ASTNode body) {
    return body instanceof TypeDeclaration
        || body instanceof EnumDeclaration
        || body instanceof AnnotationTypeDeclaration
        || body instanceof RecordDeclaration
        || body instanceof AnonymousClassDeclaration;
  }

  private static boolean areDisjoint(List<Member> members, int from, int to) {
    @Var int previousEnd = from;
    for (Member member : members) {
      if (member.start() < previousEnd || member.end() <= member.start()) {
        return false;
      }
      previousEnd = member.end();
    }
    return previousEnd <= to;
  }

  private static Category categoryOf(BodyDeclaration declaration) {
    boolean isStatic = Modifier.isStatic(declaration.getModifiers());
    if (declaration instanceof AbstractTypeDeclaration) {
      return Category.TYPE;
    }
    if (declaration instanceof FieldDeclaration) {
      return isStatic || isInterfaceOrAnnotation(declaration.getParent())
          ? Category.STATIC_FIELD
          : Category.FIELD;
    }
    if (declaration instanceof Initializer) {
      return isStatic ? Category.STATIC_INITIALIZER : Category.INITIALIZER;
    }
    if (declaration instanceof MethodDeclaration method && method.isConstructor()) {
      return Category.CONSTRUCTOR;
    }
    if (declaration instanceof MethodDeclaration) {
      return isStatic ? Category.STATIC_METHOD : Category.METHOD;
    }
    if (declaration instanceof AnnotationTypeMemberDeclaration) {
      return Category.METHOD;
    }
    throw new IllegalStateException(
        "Unexpected member declaration " + declaration.getClass().getName());
  }

  private static Visibility visibilityOf(BodyDeclaration declaration) {
    int modifiers = declaration.getModifiers();
    ASTNode body = declaration.getParent();
    if (Modifier.isPrivate(modifiers)) {
      return Visibility.PRIVATE;
    }
    if (isInterfaceOrAnnotation(body)) {
      return Visibility.PUBLIC;
    }
    if (body instanceof EnumDeclaration
        && declaration instanceof MethodDeclaration method
        && method.isConstructor()) {
      return Visibility.PRIVATE;
    }
    if (Modifier.isPublic(modifiers)) {
      return Visibility.PUBLIC;
    }
    if (Modifier.isProtected(modifiers)) {
      return Visibility.PROTECTED;
    }
    return Visibility.PACKAGE;
  }

  private static boolean isInterfaceOrAnnotation(ASTNode body) {
    return (body instanceof TypeDeclaration type && type.isInterface())
        || body instanceof AnnotationTypeDeclaration;
  }

  private List<List<Member>> memberLists(ASTNode root) {
    List<List<Member>> lists = new ArrayList<>();
    root.accept(
        new ASTVisitor() {
          @Override
          public boolean preVisit2(ASTNode node) {
            if (!node.equals(root) && isMember(node)) {
              return false;
            }
            List<?> declarations = bodyDeclarations(node);
            if (!declarations.isEmpty()) {
              lists.add(
                  declarations.stream()
                      .map(declaration -> member((BodyDeclaration) declaration))
                      .toList());
            }
            return true;
          }
        });
    lists.sort(Comparator.comparingInt(members -> members.getFirst().start()));
    return lists;
  }

  private static List<?> bodyDeclarations(ASTNode node) {
    if (node instanceof AbstractTypeDeclaration type) {
      return type.bodyDeclarations();
    }
    if (node instanceof AnonymousClassDeclaration anonymous) {
      return anonymous.bodyDeclarations();
    }
    return List.of();
  }

  private static boolean isMember(ASTNode node) {
    StructuralPropertyDescriptor location = node.getLocationInParent();
    return location == AnonymousClassDeclaration.BODY_DECLARATIONS_PROPERTY
        || (node.getParent() instanceof AbstractTypeDeclaration type
            && location == type.getBodyDeclarationsProperty());
  }

  private Member member(BodyDeclaration declaration) {
    CompilationUnit unit = (CompilationUnit) declaration.getRoot();
    int start = unit.getExtendedStartPosition(declaration);
    int rank = order.rank(categoryOf(declaration), visibilityOf(declaration));
    return new Member(declaration, start, start + unit.getExtendedLength(declaration), rank);
  }

  private static boolean isWhitespace(String source, int from, int to) {
    for (int i = from; i < to; i++) {
      if (!CharOperation.isWhitespace(source.charAt(i))) {
        return false;
      }
    }
    return true;
  }

  private static boolean containsLineBreak(String source, int from, int to) {
    for (int i = from; i < to; i++) {
      if (source.charAt(i) == '\n' || source.charAt(i) == '\r') {
        return true;
      }
    }
    return false;
  }

  private static Optional<CompilationUnit> parse(String source) {
    ASTParser parser = ASTParser.newParser(AST.getJLSLatest());
    parser.setKind(ASTParser.K_COMPILATION_UNIT);
    parser.setCompilerOptions(COMPILER_OPTIONS);
    parser.setResolveBindings(false);
    parser.setSource(source.toCharArray());
    CompilationUnit unit;
    try {
      unit = (CompilationUnit) parser.createAST(null);
    } catch (RuntimeException e) {
      return Optional.empty();
    }
    return hasSyntaxErrors(unit) ? Optional.empty() : Optional.of(unit);
  }

  private static boolean hasSyntaxErrors(CompilationUnit unit) {
    if (Arrays.stream(unit.getProblems()).anyMatch(IProblem::isError)) {
      return true;
    }
    List<ASTNode> recovered = new ArrayList<>();
    unit.accept(
        new ASTVisitor(true) {
          @Override
          public boolean preVisit2(ASTNode node) {
            if ((node.getFlags() & (ASTNode.MALFORMED | ASTNode.RECOVERED)) != 0) {
              recovered.add(node);
            }
            return recovered.isEmpty();
          }
        });
    return !recovered.isEmpty();
  }

  private static Map<String, String> compilerOptions() {
    Map<String, String> options = new HashMap<>();
    JavaCore.setComplianceOptions(JavaCore.latestSupportedJavaVersion(), options);
    return Map.copyOf(options);
  }

  private record Member(BodyDeclaration declaration, int start, int end, int rank) {}
}
