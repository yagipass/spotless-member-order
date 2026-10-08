package io.github.yagipass.memberorder.integration;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class MavenConsumerTest {

  private static final Path LOCAL_REPOSITORY =
      Environment.workDirectory().resolve("maven-repository");

  private static final Path SETTINGS_FILE =
      Environment.workDirectory().resolve("maven-settings.xml");

  @TempDir Path project;

  @BeforeAll
  static void resolveTheLibraryFromThisBuildRatherThanAnEarlierRun() throws IOException {
    deleteRecursively(LOCAL_REPOSITORY.resolve("io/github/yagipass/spotless-member-order"));
    Files.createDirectories(SETTINGS_FILE.getParent());
    Files.writeString(
        SETTINGS_FILE,
        """
                <settings xmlns="http://maven.apache.org/SETTINGS/1.2.0">
                  <profiles>
                    <profile>
                      <id>spotless-member-order</id>
                      <pluginRepositories>
                        <pluginRepository>
                          <id>spotless-member-order</id>
                          <url>%s</url>
                        </pluginRepository>
                      </pluginRepositories>
                    </profile>
                  </profiles>
                  <activeProfiles>
                    <activeProfile>spotless-member-order</activeProfile>
                  </activeProfiles>
                </settings>
                """
            .formatted(Environment.repository().toUri()));
  }

  @Test
  void memberOrderElementWithDefaultsMakesSpotlessCheckFailUntilSpotlessApplyOrdersTheMembers()
      throws Exception {
    checkFailsThenApplyWritesExpectedOutputThenCheckPasses("defaults");

    Path published = Environment.publishedFile(".jar");
    Path resolved = LOCAL_REPOSITORY.resolve(Environment.repository().relativize(published));
    assertArrayEquals(
        Files.readAllBytes(published),
        Files.readAllBytes(resolved),
        "Maven ran a different jar than the one this build published");
  }

  @Test
  void xmlSettingsReachTheStepAndChangeTheOrderThatSpotlessApplies() throws Exception {
    assertNotEquals(Fixtures.expected("defaults"), Fixtures.expected("custom-order"));

    checkFailsThenApplyWritesExpectedOutputThenCheckPasses("custom-order");
  }

  @Test
  void memberOrderBeforeGoogleJavaFormatGivesOutputThatTheWholePipelineAccepts() throws Exception {
    checkFailsThenApplyWritesExpectedOutputThenCheckPasses("google-java-format");
  }

  @ParameterizedTest
  @CsvSource(
      delimiter = '|',
      value = {
        "<order>T:BRD,T:V,SF,F,C:BRD,C:V,SM:BRD,M:BRD,SM:V,M:V</order> | <order>T:BRD,T:V,SF,F,C:BRD,C:V,SM:BRD,M:BRD,SM:V</order> | order \"T:BRD,T:V,SF,F,C:BRD,C:V,SM:BRD,M:BRD,SM:V\" is invalid: missing M:V.",
      })
  void invalidXmlSettingFailsTheBuildWithAMessageThatNamesTheSettingAndTheProblem(
      String validElement, String invalidElement, String message) throws Exception {
    Path source = createProject("custom-order");
    Path pom = project.resolve("pom.xml");
    String valid = Files.readString(pom);
    String invalid = valid.replace(validElement, invalidElement);
    assertNotEquals(valid, invalid);
    Files.writeString(pom, invalid);

    Result result = mvn("spotless:apply");

    assertNotEquals(0, result.exitCode(), result.output());
    assertTrue(result.output().contains(message), result.output());
    assertEquals(Files.readString(Fixtures.file("input.java")), Files.readString(source));
  }

  private void checkFailsThenApplyWritesExpectedOutputThenCheckPasses(String scenario)
      throws Exception {
    Path source = createProject(scenario);

    Result unordered = mvn("spotless:check");
    assertNotEquals(0, unordered.exitCode(), unordered.output());
    assertTrue(unordered.output().contains("format violations"), unordered.output());

    Result apply = mvn("spotless:apply");
    assertEquals(0, apply.exitCode(), apply.output());
    assertEquals(Fixtures.expected(scenario), Files.readString(source));

    deleteRecursively(project.resolve("target"));
    Result ordered = mvn("spotless:check");
    assertEquals(0, ordered.exitCode(), ordered.output());
    assertTrue(ordered.output().contains("1 were already clean, 0 were skipped"), ordered.output());
  }

  private Path createProject(String scenario) throws IOException {
    Files.copy(Fixtures.file(scenario + "/pom.xml"), project.resolve("pom.xml"));
    return Fixtures.writeInput(project);
  }

  private Result mvn(String goal) throws IOException, InterruptedException {
    Path log = project.resolve("maven.log");
    List<String> command =
        List.of(
            Environment.mavenHome()
                .resolve(
                    System.getProperty("os.name").startsWith("Windows") ? "bin/mvn.cmd" : "bin/mvn")
                .toString(),
            "--batch-mode",
            "--no-transfer-progress",
            "--settings",
            SETTINGS_FILE.toString(),
            "-Dmaven.repo.local=" + LOCAL_REPOSITORY,
            "-Dspotless.version=" + Environment.spotlessMavenVersion(),
            "-Dspotless-member-order.version=" + Environment.libraryVersion(),
            goal);
    ProcessBuilder builder =
        new ProcessBuilder(command)
            .directory(project.toFile())
            .redirectErrorStream(true)
            .redirectOutput(log.toFile());
    Map<String, String> environment = builder.environment();
    environment.put("JAVA_HOME", System.getProperty("java.home"));
    environment.put("MAVEN_SKIP_RC", "true");
    environment.remove("MAVEN_ARGS");
    environment.remove("MAVEN_OPTS");
    Process process = builder.start();
    if (!process.waitFor(10, TimeUnit.MINUTES)) {
      process.destroyForcibly();
      fail("mvn " + goal + " did not finish within 10 minutes:\n" + Files.readString(log));
    }
    return new Result(process.exitValue(), Files.readString(log));
  }

  private static void deleteRecursively(Path directory) throws IOException {
    if (!Files.exists(directory)) {
      return;
    }
    try (Stream<Path> files = Files.walk(directory)) {
      for (Path file : files.sorted(Comparator.reverseOrder()).toList()) {
        Files.delete(file);
      }
    }
  }

  private record Result(int exitCode, String output) {}
}
