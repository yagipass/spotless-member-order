package io.github.yagipass.memberorder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.Reader;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.stream.Stream;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class MemberSorterFixtureTest {

  private static final Path FIXTURES = fixturesDirectory();

  private static final List<String> SETTING_NAMES =
      List.of("categoryOrder", "visibilityOrder", "sortFields");

  private static final List<Named<MemberOrder>> OTHER_SETTINGS =
      List.of(
          Named.of(
              "the default settings",
              MemberOrder.parse(MemberOrder.DEFAULT_CATEGORY_ORDER, null, false)),
          Named.of("reversed orders", MemberOrder.parse("M,C,I,F,SM,SI,SF,T", "V,D,R,B", false)),
          Named.of(
              "sortFields and a visibility order",
              MemberOrder.parse(MemberOrder.DEFAULT_CATEGORY_ORDER, "B,R,D,V", true)));

  static List<String> fixtures() throws IOException {
    try (Stream<Path> files = Files.walk(FIXTURES)) {
      return files
          .filter(file -> file.getFileName().toString().equals("input.java"))
          .map(file -> FIXTURES.relativize(file.getParent()).toString().replace('\\', '/'))
          .sorted()
          .toList();
    }
  }

  static List<Arguments> fixturesWithEachSetting() throws IOException {
    List<Arguments> arguments = new ArrayList<>();
    for (String fixture : fixtures()) {
      arguments.add(Arguments.of(fixture, Named.of("the fixture's settings", order(fixture))));
      for (Named<MemberOrder> setting : OTHER_SETTINGS) {
        arguments.add(Arguments.of(fixture, setting));
      }
    }
    return arguments;
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("fixtures")
  void sortsInputIntoExpectedOutput(String fixture) throws IOException {
    assertEquals(expectedOutput(fixture), new MemberSorter(order(fixture)).sort(input(fixture)));
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("fixtures")
  void leavesExpectedOutputUnchangedBecauseItIsAlreadySorted(String fixture) throws IOException {
    String expected = expectedOutput(fixture);
    assertEquals(expected, new MemberSorter(order(fixture)).sort(expected));
  }

  @ParameterizedTest(name = "{0} with {1}")
  @MethodSource("fixturesWithEachSetting")
  void
      sortingTwiceGivesTheSameResultAsSortingOnceWhateverTheSettingsBecauseSpotlessDoesNotRetryAStepThatDoesNotConverge(
          String fixture, MemberOrder order) throws IOException {
    MemberSorter sorter = new MemberSorter(order);
    String once = sorter.sort(input(fixture));
    assertEquals(once, sorter.sort(once));
  }

  @Test
  void crlfFixturesStillContainCarriageReturnsSoTheyTestCrlf() throws IOException {
    List<String> crlfFixtures =
        fixtures().stream().filter(fixture -> fixture.startsWith("crlf/")).toList();
    assertFalse(crlfFixtures.isEmpty());
    for (String fixture : crlfFixtures) {
      assertTrue(input(fixture).contains("\r\n"), fixture);
      assertTrue(expectedOutput(fixture).contains("\r\n"), fixture);
    }
  }

  private static MemberOrder order(String fixture) throws IOException {
    Properties settings = new Properties();
    Path file = FIXTURES.resolve(fixture).resolve("settings.properties");
    if (Files.exists(file)) {
      try (Reader reader = Files.newBufferedReader(file)) {
        settings.load(reader);
      }
    }
    assertTrue(
        SETTING_NAMES.containsAll(settings.stringPropertyNames()),
        () ->
            file
                + " sets "
                + settings.stringPropertyNames()
                + ", but only "
                + SETTING_NAMES
                + " exist");
    String sortFields = settings.getProperty("sortFields", "false");
    assertTrue(
        sortFields.equals("true") || sortFields.equals("false"),
        () -> file + " sets sortFields to \"" + sortFields + "\" instead of true or false");
    return MemberOrder.parse(
        settings.getProperty("categoryOrder", MemberOrder.DEFAULT_CATEGORY_ORDER),
        settings.getProperty("visibilityOrder"),
        Boolean.parseBoolean(sortFields));
  }

  private static String input(String fixture) throws IOException {
    return Files.readString(FIXTURES.resolve(fixture).resolve("input.java"));
  }

  private static String expectedOutput(String fixture) throws IOException {
    Path expected = FIXTURES.resolve(fixture).resolve("expected.java");
    return Files.exists(expected) ? Files.readString(expected) : input(fixture);
  }

  private static Path fixturesDirectory() {
    try {
      return Path.of(
          Objects.requireNonNull(MemberSorterFixtureTest.class.getResource("/fixtures")).toURI());
    } catch (URISyntaxException e) {
      throw new IllegalStateException(e);
    }
  }
}
