package io.github.yagipass.memberorder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.diffplug.spotless.FormatterStep;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

class MemberOrderStepTest {

  private static final File FILE = new File("Example.java");

  private static final String UNSORTED =
      """
            class Example {
                private void helper() {
                }

                public void run() {
                }

                private int count;
            }
            """;

  @Test
  void
      stepIsNamedMemberOrderBecauseSpotlessSettingsSuchAsSuppressLintsForAndReplaceStepReferToTheStepByThatName() {
    assertEquals("memberOrder", MemberOrderStep.create().getName());
  }

  @Test
  void createdStepSortsByCategoryAndKeepsSourceOrderWithinACategory() throws Exception {
    String expected =
        """
                class Example {
                    private int count;

                    private void helper() {
                    }

                    public void run() {
                    }
                }
                """;

    assertEquals(expected, MemberOrderStep.create().format(UNSORTED, FILE));
  }

  @Test
  void builtStepAppliesItsSettings() throws Exception {
    FormatterStep step = MemberOrderStep.builder().order("M:BRD,M:V,C,SM,SF,F,T").build();
    String expected =
        """
                class Example {
                    public void run() {
                    }

                    private void helper() {
                    }

                    private int count;
                }
                """;

    assertEquals(expected, step.format(UNSORTED, FILE));
  }

  @Test
  void createIsTheBuilderWithTheDocumentedDefaultsSoSpellingThemOutKeepsTheSameStepAndItsCaches() {
    assertEquals(MemberOrderStep.builder().build(), MemberOrderStep.create());
    assertEquals(
        MemberOrderStep.builder().order("T,SF,SM,F,C,M").build(), MemberOrderStep.create());
  }

  @Test
  void stepStillEqualsAndFormatsAfterJavaSerializationBecauseGradleAndMavenCacheIt()
      throws Exception {
    FormatterStep step =
        MemberOrderStep.builder().order("T,SF:BRD,SF:V,SI,SM,F,C,M:BRD,M:V").build();

    FormatterStep copy = deserialize(serialize(step));

    assertEquals(step, copy);
    assertEquals(step.hashCode(), copy.hashCode());
    assertEquals(step.format(UNSORTED, FILE), copy.format(UNSORTED, FILE));
  }

  @Test
  void equivalentSettingsGiveEqualStepsSoCosmeticBuildChangesKeepCachesValid() {
    FormatterStep compact = MemberOrderStep.builder().order("T,SF,SM,F,C,M:BRD,M:V").build();
    FormatterStep spaced = MemberOrderStep.builder().order(" T, SF, SM, F, C, M:BRD, M:V ").build();

    assertEquals(compact, spaced);
    assertEquals(compact.hashCode(), spaced.hashCode());
  }

  @Test
  void differentSettingsGiveDifferentStepsSoCachedResultsAreNotReused() {
    FormatterStep defaults = MemberOrderStep.create();

    assertNotEquals(defaults, MemberOrderStep.builder().order("SF,F,C,M,SM,T").build());
    assertNotEquals(defaults, MemberOrderStep.builder().order("T,SF,SM,F,C,M:BRD,M:V").build());
    assertNotEquals(
        MemberOrderStep.builder().order("T,SF,SM,F,C,M:BRD,M:V").build(),
        MemberOrderStep.builder().order("T,SF,SM,F,C,M:V,M:BRD").build());
    assertNotEquals(defaults, MemberOrderStep.builder().order("T,SF,SI,SM,F,I,C,M").build());
  }

  @Test
  void serializedStepContainsTheLibraryVersionSoMavenReformatsAfterAnUpgrade() throws IOException {
    assertTrue(LibraryVersion.VALUE.matches("\\d+\\.\\d+\\.\\d+.*"), LibraryVersion.VALUE);

    String serialized =
        new String(serialize(MemberOrderStep.create()), StandardCharsets.ISO_8859_1);

    assertTrue(serialized.contains(LibraryVersion.VALUE));
  }

  @Test
  void invalidSettingsFailWhenTheStepIsBuiltRatherThanWhenFilesAreFormatted() {
    assertThrows(
        IllegalArgumentException.class, () -> MemberOrderStep.builder().order("T,SF").build());
  }

  private static byte[] serialize(FormatterStep step) throws IOException {
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    try (ObjectOutputStream out = new ObjectOutputStream(bytes)) {
      out.writeObject(step);
    }
    return bytes.toByteArray();
  }

  private static FormatterStep deserialize(byte[] bytes)
      throws IOException, ClassNotFoundException {
    try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bytes))) {
      return (FormatterStep) in.readObject();
    }
  }
}
