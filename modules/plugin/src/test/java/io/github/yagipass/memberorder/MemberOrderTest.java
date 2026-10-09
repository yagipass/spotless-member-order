package io.github.yagipass.memberorder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class MemberOrderTest {

  @Test
  void defaultOrderIsTheEclipseCategoryOrderAndIgnoresVisibility() {
    MemberOrder order = MemberOrder.parse(MemberOrder.DEFAULT_ORDER);

    assertTrue(
        order.rank(Category.TYPE, Visibility.PRIVATE)
            < order.rank(Category.STATIC_FIELD, Visibility.PUBLIC));
    assertTrue(
        order.rank(Category.STATIC_FIELD, Visibility.PRIVATE)
            < order.rank(Category.STATIC_METHOD, Visibility.PUBLIC));
    assertTrue(
        order.rank(Category.STATIC_METHOD, Visibility.PRIVATE)
            < order.rank(Category.FIELD, Visibility.PUBLIC));
    assertTrue(
        order.rank(Category.FIELD, Visibility.PRIVATE)
            < order.rank(Category.CONSTRUCTOR, Visibility.PUBLIC));
    assertTrue(
        order.rank(Category.CONSTRUCTOR, Visibility.PRIVATE)
            < order.rank(Category.METHOD, Visibility.PUBLIC));
    assertSameRank(order, Category.METHOD, Visibility.PUBLIC, Category.METHOD, Visibility.PRIVATE);
  }

  @Test
  void codesFollowTheEclipseMeaning() {
    MemberOrder order = MemberOrder.parse("M:V,M:D,M:R,M:B,C,I,F,SM,SI,SF,T");

    assertTrue(
        order.rank(Category.METHOD, Visibility.PRIVATE)
            < order.rank(Category.METHOD, Visibility.PACKAGE));
    assertTrue(
        order.rank(Category.METHOD, Visibility.PACKAGE)
            < order.rank(Category.METHOD, Visibility.PROTECTED));
    assertTrue(
        order.rank(Category.METHOD, Visibility.PROTECTED)
            < order.rank(Category.METHOD, Visibility.PUBLIC));
    assertTrue(
        order.rank(Category.METHOD, Visibility.PUBLIC)
            < order.rank(Category.CONSTRUCTOR, Visibility.PUBLIC));
    assertTrue(
        order.rank(Category.CONSTRUCTOR, Visibility.PUBLIC)
            < order.rank(Category.INITIALIZER, Visibility.PACKAGE));
    assertTrue(
        order.rank(Category.INITIALIZER, Visibility.PACKAGE)
            < order.rank(Category.FIELD, Visibility.PUBLIC));
    assertTrue(
        order.rank(Category.FIELD, Visibility.PUBLIC)
            < order.rank(Category.STATIC_METHOD, Visibility.PUBLIC));
    assertTrue(
        order.rank(Category.STATIC_METHOD, Visibility.PUBLIC)
            < order.rank(Category.STATIC_INITIALIZER, Visibility.PACKAGE));
    assertTrue(
        order.rank(Category.STATIC_INITIALIZER, Visibility.PACKAGE)
            < order.rank(Category.STATIC_FIELD, Visibility.PUBLIC));
    assertTrue(
        order.rank(Category.STATIC_FIELD, Visibility.PUBLIC)
            < order.rank(Category.TYPE, Visibility.PUBLIC));
  }

  @Test
  void membersOfOneEntryShareARankSoTheyKeepTheirSourceOrder() {
    MemberOrder order = MemberOrder.parse("T,SF,SM,F,C,M:BRD,M:V");

    assertSameRank(order, Category.METHOD, Visibility.PUBLIC, Category.METHOD, Visibility.PACKAGE);
    assertSameRank(
        order, Category.METHOD, Visibility.PROTECTED, Category.METHOD, Visibility.PACKAGE);
    assertTrue(
        order.rank(Category.METHOD, Visibility.PACKAGE)
            < order.rank(Category.METHOD, Visibility.PRIVATE));
  }

  @Test
  void entriesOfDifferentCategoriesCanInterleaveSoPrivateStaticMethodsCanFollowAllOtherMethods() {
    MemberOrder order = MemberOrder.parse("T,SF,F,C,SM:BRD,M:BRD,SM:V,M:V");

    assertTrue(
        order.rank(Category.STATIC_METHOD, Visibility.PUBLIC)
            < order.rank(Category.METHOD, Visibility.PUBLIC));
    assertTrue(
        order.rank(Category.METHOD, Visibility.PACKAGE)
            < order.rank(Category.STATIC_METHOD, Visibility.PRIVATE));
    assertTrue(
        order.rank(Category.STATIC_METHOD, Visibility.PRIVATE)
            < order.rank(Category.METHOD, Visibility.PRIVATE));
  }

  @Test
  void unlistedInitializersShareTheRankOfTheirFieldsSoInitializationOrderIsKept() {
    MemberOrder order = MemberOrder.parse("SF,M,F,C,SM,T");

    assertSameRank(
        order,
        Category.STATIC_INITIALIZER,
        Visibility.PACKAGE,
        Category.STATIC_FIELD,
        Visibility.PUBLIC);
    assertSameRank(
        order, Category.STATIC_FIELD, Visibility.PUBLIC, Category.STATIC_FIELD, Visibility.PRIVATE);
    assertSameRank(
        order, Category.INITIALIZER, Visibility.PACKAGE, Category.FIELD, Visibility.PRIVATE);
    assertTrue(
        order.rank(Category.STATIC_INITIALIZER, Visibility.PACKAGE)
            < order.rank(Category.METHOD, Visibility.PUBLIC));
  }

  @Test
  void listedInitializersGetTheirOwnPlaceAwayFromTheirFields() {
    MemberOrder order = MemberOrder.parse("SI,T,SF,SM,F,C,I,M");

    assertTrue(
        order.rank(Category.STATIC_INITIALIZER, Visibility.PACKAGE)
            < order.rank(Category.TYPE, Visibility.PUBLIC));
    assertTrue(
        order.rank(Category.CONSTRUCTOR, Visibility.PUBLIC)
            < order.rank(Category.INITIALIZER, Visibility.PACKAGE));
    assertTrue(
        order.rank(Category.FIELD, Visibility.PUBLIC)
            < order.rank(Category.INITIALIZER, Visibility.PACKAGE));
  }

  @Test
  void splittingStaticFieldsLeavesInstanceFieldsAndInitializersInSourceOrder() {
    MemberOrder order = MemberOrder.parse("T,SF:BRD,SF:V,SI,SM,F,C,M");

    assertTrue(
        order.rank(Category.STATIC_FIELD, Visibility.PUBLIC)
            < order.rank(Category.STATIC_FIELD, Visibility.PRIVATE));
    assertTrue(
        order.rank(Category.STATIC_FIELD, Visibility.PRIVATE)
            < order.rank(Category.STATIC_INITIALIZER, Visibility.PACKAGE));
    assertSameRank(order, Category.FIELD, Visibility.PUBLIC, Category.FIELD, Visibility.PRIVATE);
    assertSameRank(
        order, Category.INITIALIZER, Visibility.PACKAGE, Category.FIELD, Visibility.PRIVATE);
  }

  @Test
  void
      annotatedMembersTakeTheirAnnotationEntryWhateverTheirCategoryAndVisibilitySoStaticAndInstanceBeanMethodsStayTogether() {
    MemberOrder order = MemberOrder.parse("T,SF,F,C,@Bean,SM,M");
    List<String> bean = List.of("Bean");
    int beans = order.rank(Category.METHOD, Visibility.PACKAGE, bean);

    assertEquals(beans, order.rank(Category.STATIC_METHOD, Visibility.PUBLIC, bean));
    assertEquals(beans, order.rank(Category.METHOD, Visibility.PRIVATE, bean));
    assertEquals(beans, order.rank(Category.CONSTRUCTOR, Visibility.PUBLIC, bean));
    assertEquals(beans, order.rank(Category.TYPE, Visibility.PROTECTED, bean));
    assertTrue(order.rank(Category.CONSTRUCTOR, Visibility.PUBLIC) < beans);
    assertTrue(beans < order.rank(Category.STATIC_METHOD, Visibility.PUBLIC));
  }

  @Test
  void annotatedFieldsStayWithTheOtherFieldsBecauseMovingThemCanChangeInitializationOrder() {
    MemberOrder order = MemberOrder.parse("@Inject,T,SF,SM,F,C,M");
    List<String> inject = List.of("Inject");

    assertEquals(
        order.rank(Category.FIELD, Visibility.PRIVATE),
        order.rank(Category.FIELD, Visibility.PRIVATE, inject));
    assertEquals(
        order.rank(Category.STATIC_FIELD, Visibility.PUBLIC),
        order.rank(Category.STATIC_FIELD, Visibility.PUBLIC, inject));
  }

  @Test
  void membersWithoutAListedAnnotationKeepTheirCategoryEntry() {
    MemberOrder order = MemberOrder.parse("T,SF,SM,F,C,M,@Bean");

    assertEquals(
        order.rank(Category.METHOD, Visibility.PUBLIC),
        order.rank(Category.METHOD, Visibility.PUBLIC, List.of("Override", "Deprecated")));
  }

  @Test
  void
      aMemberWithAnnotationsOfSeveralEntriesTakesTheEntryListedFirstNotTheAnnotationWrittenFirst() {
    MemberOrder order = MemberOrder.parse("T,SF,SM,F,C,@Inject,M,@Deprecated");

    assertEquals(
        order.rank(Category.METHOD, Visibility.PUBLIC, List.of("Inject")),
        order.rank(Category.METHOD, Visibility.PUBLIC, List.of("Deprecated", "Inject")));
  }

  @Test
  void whitespaceAroundEntriesIsAllowedSoMultiLineXmlValuesWork() {
    MemberOrder spaced = MemberOrder.parse("\n    T, SF ,SM,\tF,C,\n    M:BRD, M:V\n");

    assertEquals(MemberOrder.parse("T,SF,SM,F,C,M:BRD,M:V"), spaced);
  }

  @ParameterizedTest(name = "order \"{0}\" fails because {1}")
  @CsvSource(
      delimiter = '|',
      value = {
        "T,SF,SM,F,C,X | unknown category \"X\"; use T,SF,SI,SM,F,I,C,M",
        "t,sf,sm,f,c,m | unknown category \"t\"; use T,SF,SI,SM,F,I,C,M",
        "T,SF,SM,F,C,M, | unknown category \"\"; use T,SF,SI,SM,F,I,C,M",
        "T;SF;SM;F;C;M | unknown category \"T;SF;SM;F;C;M\"; use T,SF,SI,SM,F,I,C,M",
        "'' | unknown category \"\"; use T,SF,SI,SM,F,I,C,M",
        "T,SF,SM,F,C,M: | \"M:\" lists no visibility",
        "T,SF,SM,F,C,M:BRP | unknown visibility \"P\" in \"M:BRP\"; use B,R,D,V",
        "T,SF,SM,F,C,M:VB | \"M:VB\" must list its visibilities once each, in B,R,D,V order",
        "T,SF,SM,F,C,M:BB,M:RDV | \"M:BB\" must list its visibilities once each, in B,R,D,V order",
        "T,SF,SI:D,SM,F,C,M | \"SI:D\" gives initializers a visibility, but they have none",
        "T,SF,SM,F,C,M,M | duplicate M",
        "T,SF,SM,F,C,M,M:V | duplicate M:V",
        "T,SF,SM,F,C,M:BR,M:RDV | duplicate M:R",
        "T,SF,SI,SI,SM,F,C,M | duplicate SI",
        "T,SF,SM,F,C | missing M",
        "SF,F,C,SM:BRD,M | missing T,SM:V",
        "T,SF:BRD,SF:V,SM,F,C,M | SF is split by visibility, so SI must be listed to place the"
            + " initializers",
        "T,SF,SM,F:B,F:RDV,C,M | F is split by visibility, so I must be listed to place the"
            + " initializers",
        "T,SF,SM,F,C,M,@ | \"@\" does not name an annotation by its simple name",
        "T,SF,SM,F,C,M,@org.example.Bean | \"@org.example.Bean\" does not name an annotation by its"
            + " simple name",
        "T,SF,SM,F,C,M,@Bean:B | \"@Bean:B\" does not name an annotation by its simple name",
        "T,SF,SM,F,C,M,@interface | \"@interface\" does not name an annotation by its simple name",
        "T,SF,SM,F,C,M,@Bean,@Bean | duplicate @Bean",
        "@Bean | missing T,SF,SM,F,C,M",
      })
  void invalidOrderIsRejectedInsteadOfFallingBackToTheDefault(String order, String reason) {
    IllegalArgumentException error =
        assertThrows(IllegalArgumentException.class, () -> MemberOrder.parse(order));

    assertEquals("order \"" + order + "\" is invalid: " + reason + ".", error.getMessage());
  }

  private static void assertSameRank(
      MemberOrder order,
      Category category,
      Visibility visibility,
      Category otherCategory,
      Visibility otherVisibility) {
    assertEquals(order.rank(category, visibility), order.rank(otherCategory, otherVisibility));
  }
}
