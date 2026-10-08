package io.github.yagipass.memberorder.maven;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.github.yagipass.memberorder.MemberOrderStep;
import org.junit.jupiter.api.Test;

class MemberOrderStepFactoryTest {

  @Test
  void emptyMemberOrderElementBuildsTheDefaultStep() {
    assertEquals(MemberOrderStep.create(), new MemberOrderStepFactory().newFormatterStep(null));
  }

  @Test
  void xmlOrderReachesTheStepAsIfSetThroughTheBuilder() {
    MemberOrderStepFactory factory = new MemberOrderStepFactory();
    factory.setOrder("T:BRD,T:V,SF,F,C:BRD,C:V,SM:BRD,M:BRD,SM:V,M:V");

    assertEquals(
        MemberOrderStep.builder().order("T:BRD,T:V,SF,F,C:BRD,C:V,SM:BRD,M:BRD,SM:V,M:V").build(),
        factory.newFormatterStep(null));
  }

  @Test
  void invalidXmlOrderFailsTheMavenBuild() {
    MemberOrderStepFactory factory = new MemberOrderStepFactory();
    factory.setOrder("T,SF");

    assertThrows(IllegalArgumentException.class, () -> factory.newFormatterStep(null));
  }
}
