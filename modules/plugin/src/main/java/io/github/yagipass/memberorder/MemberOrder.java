package io.github.yagipass.memberorder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

record MemberOrder(List<MemberOrder.Entry> entries) implements Serializable {

  static final String DEFAULT_ORDER = "T,SF,SM,F,C,M";

  private static final Map<Category, Category> FIELDS_OF_INITIALIZERS =
      new EnumMap<>(
          Map.of(
              Category.STATIC_INITIALIZER, Category.STATIC_FIELD,
              Category.INITIALIZER, Category.FIELD));

  static MemberOrder parse(String order) {
    List<Entry> entries = new ArrayList<>();
    Map<Category, Set<Visibility>> listed = new EnumMap<>(Category.class);
    for (String token : order.split(",", -1)) {
      Entry entry = Entry.parse(order, token.strip());
      Set<Visibility> seen =
          listed.computeIfAbsent(entry.category(), category -> EnumSet.noneOf(Visibility.class));
      Set<Visibility> repeated = EnumSet.noneOf(Visibility.class);
      repeated.addAll(entry.visibilities());
      repeated.retainAll(seen);
      if (!repeated.isEmpty()) {
        throw invalid(order, "duplicate " + describe(entry.category(), repeated));
      }
      seen.addAll(entry.visibilities());
      entries.add(entry);
    }
    List<String> missing = new ArrayList<>();
    for (Category category : Category.values()) {
      if (FIELDS_OF_INITIALIZERS.containsKey(category)) {
        continue;
      }
      Set<Visibility> unlisted = EnumSet.allOf(Visibility.class);
      unlisted.removeAll(listed.getOrDefault(category, Set.of()));
      if (!unlisted.isEmpty()) {
        missing.add(describe(category, unlisted));
      }
    }
    if (!missing.isEmpty()) {
      throw invalid(order, "missing " + String.join(",", missing));
    }
    FIELDS_OF_INITIALIZERS.forEach(
        (initializers, fields) -> {
          boolean split = entries.stream().filter(entry -> entry.category() == fields).count() > 1;
          if (split && !listed.containsKey(initializers)) {
            throw invalid(
                order,
                fields.code()
                    + " is split by visibility, so "
                    + initializers.code()
                    + " must be listed to place the initializers");
          }
        });
    return new MemberOrder(List.copyOf(entries));
  }

  int rank(Category category, Visibility visibility) {
    Category placed =
        entries.stream().anyMatch(entry -> entry.category() == category)
            ? category
            : FIELDS_OF_INITIALIZERS.getOrDefault(category, category);
    for (int i = 0; i < entries.size(); i++) {
      Entry entry = entries.get(i);
      if (entry.category() == placed && entry.visibilities().contains(visibility)) {
        return i;
      }
    }
    throw new IllegalStateException("No entry for " + describe(category, Set.of(visibility)));
  }

  record Entry(Category category, List<Visibility> visibilities) implements Serializable {

    private static Entry parse(String order, String text) {
      int colon = text.indexOf(':');
      String categoryCode = colon < 0 ? text : text.substring(0, colon);
      Category category =
          find(Category.values(), Category::code, categoryCode)
              .orElseThrow(
                  () ->
                      invalid(
                          order,
                          "unknown category \""
                              + categoryCode
                              + "\"; use "
                              + codes(Category.values(), Category::code)));
      if (colon < 0) {
        return new Entry(category, List.of(Visibility.values()));
      }
      if (FIELDS_OF_INITIALIZERS.containsKey(category)) {
        throw invalid(
            order, "\"" + text + "\" gives initializers a visibility, but they have none");
      }
      String letters = text.substring(colon + 1);
      if (letters.isEmpty()) {
        throw invalid(order, "\"" + text + "\" lists no visibility");
      }
      List<Visibility> visibilities = new ArrayList<>();
      for (String letter : letters.codePoints().mapToObj(Character::toString).toList()) {
        Visibility visibility =
            find(Visibility.values(), Visibility::code, letter)
                .orElseThrow(
                    () ->
                        invalid(
                            order,
                            "unknown visibility \""
                                + letter
                                + "\" in \""
                                + text
                                + "\"; use "
                                + codes(Visibility.values(), Visibility::code)));
        if (!visibilities.isEmpty() && visibility.compareTo(visibilities.getLast()) <= 0) {
          throw invalid(
              order,
              "\""
                  + text
                  + "\" must list its visibilities once each, in "
                  + codes(Visibility.values(), Visibility::code)
                  + " order");
        }
        visibilities.add(visibility);
      }
      return new Entry(category, List.copyOf(visibilities));
    }
  }

  private static String describe(Category category, Collection<Visibility> visibilities) {
    return visibilities.size() == Visibility.values().length
        ? category.code()
        : category.code()
            + ":"
            + visibilities.stream().map(Visibility::code).collect(Collectors.joining());
  }

  private static <T> Optional<T> find(T[] all, Function<T, String> codeOf, String code) {
    return Arrays.stream(all).filter(candidate -> codeOf.apply(candidate).equals(code)).findFirst();
  }

  private static <T> String codes(T[] all, Function<T, String> codeOf) {
    return Arrays.stream(all).map(codeOf).collect(Collectors.joining(","));
  }

  private static IllegalArgumentException invalid(String order, String reason) {
    return new IllegalArgumentException("order \"" + order + "\" is invalid: " + reason + ".");
  }
}
