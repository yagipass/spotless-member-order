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

  private static final List<String> SETTING_NAMES = List.of("order");

  private static final List<Named<MemberOrder>> OTHER_SETTINGS =
      List.of(
          Named.of("the default order", MemberOrder.parse(MemberOrder.DEFAULT_ORDER)),
          Named.of(
              "a reversed order split by visibility",
              MemberOrder.parse(
                  "M:V,M:D,M:R,M:B,C:V,C:D,C:R,C:B,F,SM:V,SM:D,SM:R,SM:B,SF,T:V,T:D,T:R,T:B")),
          Named.of(
              "an order that also reorders fields and initializers",
              MemberOrder.parse(
                  "T:B,T:R,T:D,T:V,SF:B,SF:R,SF:D,SF:V,SI,SM:B,SM:R,SM:D,SM:V,"
                      + "F:B,F:R,F:D,F:V,I,C:B,C:R,C:D,C:V,M:B,M:R,M:D,M:V")));

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
    return MemberOrder.parse(settings.getProperty("order", MemberOrder.DEFAULT_ORDER));
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
