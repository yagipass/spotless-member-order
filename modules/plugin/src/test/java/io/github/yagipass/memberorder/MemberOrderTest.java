package io.github.yagipass.memberorder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class MemberOrderTest {

  private static final String ALL_VISIBILITIES = "B,R,D,V";

  @Test
  void defaultCategoryOrderIsTheEclipseDefault() {
    MemberOrder order = MemberOrder.parse(MemberOrder.DEFAULT_CATEGORY_ORDER, null, false);

    assertEquals(
        List.of(
            Category.TYPE,
            Category.STATIC_FIELD,
            Category.STATIC_INITIALIZER,
            Category.STATIC_METHOD,
            Category.FIELD,
            Category.INITIALIZER,
            Category.CONSTRUCTOR,
            Category.METHOD),
        order.categories());
  }

  @Test
  void codesFollowTheEclipseMeaning() {
    MemberOrder order = MemberOrder.parse("M,C,I,F,SM,SI,SF,T", "V,D,R,B", false);

    assertEquals(
        List.of(
            Category.METHOD,
            Category.CONSTRUCTOR,
            Category.INITIALIZER,
            Category.FIELD,
            Category.STATIC_METHOD,
            Category.STATIC_INITIALIZER,
            Category.STATIC_FIELD,
            Category.TYPE),
        order.categories());
    assertEquals(
        List.of(Visibility.PRIVATE, Visibility.PACKAGE, Visibility.PROTECTED, Visibility.PUBLIC),
        order.visibilities());
  }

  @Test
  void whitespaceAroundCodesIsAllowedSoMultiLineXmlValuesWork() {
    MemberOrder spaced = MemberOrder.parse("\n    T, SF ,SI,\tSM,F,I,C,M\n", " B , R,D,V ", false);

    assertEquals(
        MemberOrder.parse(MemberOrder.DEFAULT_CATEGORY_ORDER, ALL_VISIBILITIES, false), spaced);
  }

  @ParameterizedTest(name = "categoryOrder \"{0}\" fails because of {1}")
  @CsvSource(
      delimiter = '|',
      value = {
        "T,SF,SI,SM,F,I,C,X | unknown code \"X\"",
        "t,sf,si,sm,f,i,c,m | unknown code \"t\"",
        "T,T,SF,SI,SM,F,I,C,M | duplicate code \"T\"",
        "T,SF,SI,SM,F,I,C | missing M",
        "T,SF,SM,F,C,M | missing SI,I",
        "T,SF,SI,SM,F,I,C,M, | unknown code \"\"",
        "T;SF;SI;SM;F;I;C;M | unknown code \"T;SF;SI;SM;F;I;C;M\"",
        "'' | unknown code \"\"",
      })
  void invalidCategoryOrderIsRejectedInsteadOfFallingBackToTheDefault(
      String categoryOrder, String reason) {
    IllegalArgumentException error =
        assertThrows(
            IllegalArgumentException.class, () -> MemberOrder.parse(categoryOrder, null, false));

    assertEquals(
        "categoryOrder \""
            + categoryOrder
            + "\" is invalid: "
            + reason
            + ". List each of T,SF,SI,SM,F,I,C,M exactly once, separated by commas.",
        error.getMessage());
  }

  @ParameterizedTest(name = "visibilityOrder \"{0}\" fails because of {1}")
  @CsvSource(
      delimiter = '|',
      value = {
        "B,V | missing R,D",
        "B,R,D,V,B | duplicate code \"B\"",
        "B,R,D,P | unknown code \"P\"",
        "'' | unknown code \"\"",
        "'   ' | unknown code \"\"",
      })
  void invalidVisibilityOrderIsRejectedEvenWhenBlank(String visibilityOrder, String reason) {
    IllegalArgumentException error =
        assertThrows(
            IllegalArgumentException.class,
            () -> MemberOrder.parse(MemberOrder.DEFAULT_CATEGORY_ORDER, visibilityOrder, false));

    assertEquals(
        "visibilityOrder \""
            + visibilityOrder
            + "\" is invalid: "
            + reason
            + ". List each of B,R,D,V exactly once, separated by commas.",
        error.getMessage());
  }

  @Test
  void withoutVisibilityOrderVisibilityNeverSeparatesMembers() {
    MemberOrder order = MemberOrder.parse(MemberOrder.DEFAULT_CATEGORY_ORDER, null, false);

    assertEquals(List.of(), order.visibilities());
    assertEquals(
        order.rank(Category.METHOD, Visibility.PUBLIC),
        order.rank(Category.METHOD, Visibility.PRIVATE));
  }

  @Test
  void visibilityOrderSeparatesMembersOnlyWithinTheirCategory() {
    MemberOrder order =
        MemberOrder.parse(MemberOrder.DEFAULT_CATEGORY_ORDER, ALL_VISIBILITIES, false);

    assertTrue(
        order.rank(Category.METHOD, Visibility.PUBLIC)
            < order.rank(Category.METHOD, Visibility.PRIVATE));
    assertTrue(
        order.rank(Category.CONSTRUCTOR, Visibility.PRIVATE)
            < order.rank(Category.METHOD, Visibility.PUBLIC));
  }

  @Test
  void withoutSortFieldsFieldsAndInitializersShareOneRankPerStaticnessSoTheirOrderIsKept() {
    MemberOrder order = MemberOrder.parse("SI,I,T,SF,SM,F,C,M", ALL_VISIBILITIES, false);

    assertSameRank(
        order,
        Category.STATIC_FIELD,
        Visibility.PUBLIC,
        Category.STATIC_INITIALIZER,
        Visibility.PACKAGE);
    assertSameRank(
        order, Category.STATIC_FIELD, Visibility.PUBLIC, Category.STATIC_FIELD, Visibility.PRIVATE);
    assertSameRank(
        order, Category.FIELD, Visibility.PRIVATE, Category.INITIALIZER, Visibility.PACKAGE);
    assertSameRank(order, Category.FIELD, Visibility.PUBLIC, Category.FIELD, Visibility.PRIVATE);
    assertTrue(
        order.rank(Category.TYPE, Visibility.PUBLIC)
            < order.rank(Category.STATIC_INITIALIZER, Visibility.PACKAGE));
  }

  @Test
  void withSortFieldsInitializersAndFieldVisibilitiesAreSortedToo() {
    MemberOrder order =
        MemberOrder.parse(MemberOrder.DEFAULT_CATEGORY_ORDER, ALL_VISIBILITIES, true);

    assertTrue(
        order.rank(Category.STATIC_FIELD, Visibility.PRIVATE)
            < order.rank(Category.STATIC_INITIALIZER, Visibility.PACKAGE));
    assertTrue(
        order.rank(Category.FIELD, Visibility.PUBLIC)
            < order.rank(Category.FIELD, Visibility.PRIVATE));
    assertNotEquals(
        order.rank(Category.FIELD, Visibility.PACKAGE),
        order.rank(Category.INITIALIZER, Visibility.PACKAGE));
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
