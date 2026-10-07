package io.github.yagipass.memberorder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.jspecify.annotations.Nullable;

record MemberOrder(List<Category> categories, List<Visibility> visibilities, boolean sortFields)
    implements Serializable {

  static final String DEFAULT_CATEGORY_ORDER = "T,SF,SI,SM,F,I,C,M";

  static MemberOrder parse(
      String categoryOrder, @Nullable String visibilityOrder, boolean sortFields) {
    List<Category> categories =
        parseCodes("categoryOrder", categoryOrder, Category.values(), Category::code);
    List<Visibility> visibilities =
        visibilityOrder == null
            ? List.of()
            : parseCodes("visibilityOrder", visibilityOrder, Visibility.values(), Visibility::code);
    return new MemberOrder(categories, visibilities, sortFields);
  }

  int rank(Category category, Visibility visibility) {
    if (!sortFields) {
      if (category == Category.STATIC_FIELD || category == Category.STATIC_INITIALIZER) {
        return categoryRank(Category.STATIC_FIELD);
      }
      if (category == Category.FIELD || category == Category.INITIALIZER) {
        return categoryRank(Category.FIELD);
      }
    }
    int visibilityRank = visibilities.isEmpty() ? 0 : visibilities.indexOf(visibility);
    return categoryRank(category) + visibilityRank;
  }

  private int categoryRank(Category category) {
    return categories.indexOf(category) * Visibility.values().length;
  }

  private static <T> List<T> parseCodes(
      String setting, String value, T[] all, Function<T, String> codeOf) {
    List<T> parsed = new ArrayList<>();
    for (String token : value.split(",", -1)) {
      String code = token.strip();
      T item =
          Arrays.stream(all)
              .filter(candidate -> codeOf.apply(candidate).equals(code))
              .findFirst()
              .orElseThrow(
                  () -> invalid(setting, value, all, codeOf, "unknown code \"" + code + "\""));
      if (parsed.contains(item)) {
        throw invalid(setting, value, all, codeOf, "duplicate code \"" + code + "\"");
      }
      parsed.add(item);
    }
    if (parsed.size() < all.length) {
      String missing =
          Arrays.stream(all)
              .filter(item -> !parsed.contains(item))
              .map(codeOf)
              .collect(Collectors.joining(","));
      throw invalid(setting, value, all, codeOf, "missing " + missing);
    }
    return List.copyOf(parsed);
  }

  private static <T> IllegalArgumentException invalid(
      String setting, String value, T[] all, Function<T, String> codeOf, String reason) {
    String expected = Arrays.stream(all).map(codeOf).collect(Collectors.joining(","));
    return new IllegalArgumentException(
        setting
            + " \""
            + value
            + "\" is invalid: "
            + reason
            + ". List each of "
            + expected
            + " exactly once, separated by commas.");
  }
}
