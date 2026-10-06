package io.github.yagipass.memberorder.maven;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.github.yagipass.memberorder.MemberOrderStep;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class MemberOrderStepFactoryTest {

  @Test
  void emptyMemberOrderElementBuildsTheDefaultStep() {
    assertEquals(MemberOrderStep.create(), new MemberOrderStepFactory().newFormatterStep(null));
  }

  @Test
  void xmlSettingsReachTheStepAsIfSetThroughTheBuilder() {
    MemberOrderStepFactory factory = new MemberOrderStepFactory();
    factory.setCategoryOrder("SF,SI,F,I,C,M,SM,T");
    factory.setVisibilityOrder("B,R,D,V");
    factory.setSortFields("true");

    assertEquals(
        MemberOrderStep.builder()
            .categoryOrder("SF,SI,F,I,C,M,SM,T")
            .visibilityOrder("B,R,D,V")
            .sortFields(true)
            .build(),
        factory.newFormatterStep(null));
  }

  @Test
  void invalidXmlSettingFailsTheMavenBuild() {
    MemberOrderStepFactory factory = new MemberOrderStepFactory();
    factory.setVisibilityOrder("B,V");

    assertThrows(IllegalArgumentException.class, () -> factory.newFormatterStep(null));
  }

  @ParameterizedTest
  @ValueSource(strings = {"TRUE", "True", "FALSE", "False"})
  void sortFieldsIgnoresCaseLikeMavenBooleanParameters(String value) {
    MemberOrderStepFactory factory = new MemberOrderStepFactory();
    factory.setSortFields(value);

    assertEquals(
        MemberOrderStep.builder().sortFields(Boolean.parseBoolean(value)).build(),
        factory.newFormatterStep(null));
  }

  @ParameterizedTest
  @ValueSource(strings = {"yes", "1", "on", "ture"})
  void sortFieldsOtherThanTrueOrFalseFailsInsteadOfSilentlyMeaningFalse(String value) {
    IllegalArgumentException error =
        assertThrows(
            IllegalArgumentException.class,
            () -> new MemberOrderStepFactory().setSortFields(value));

    assertEquals("sortFields \"" + value + "\" is invalid: use true or false.", error.getMessage());
  }
}
