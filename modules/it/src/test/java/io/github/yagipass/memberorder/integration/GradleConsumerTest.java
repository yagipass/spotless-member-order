package io.github.yagipass.memberorder.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.BuildTask;
import org.gradle.testkit.runner.GradleRunner;
import org.gradle.testkit.runner.TaskOutcome;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class GradleConsumerTest {

  @TempDir Path project;

  @Test
  void addStepWithDefaultsMakesSpotlessCheckFailUntilSpotlessApplyOrdersTheMembers()
      throws IOException {
    checkFailsThenApplyWritesExpectedOutputThenCheckPasses("defaults");
  }

  @Test
  void builderSettingsChangeTheOrderThatSpotlessApplies() throws IOException {
    assertNotEquals(Fixtures.expected("defaults"), Fixtures.expected("custom-order"));

    checkFailsThenApplyWritesExpectedOutputThenCheckPasses("custom-order");
  }

  @Test
  void memberOrderBeforeGoogleJavaFormatGivesOutputThatTheWholePipelineAccepts()
      throws IOException {
    checkFailsThenApplyWritesExpectedOutputThenCheckPasses("google-java-format");
  }

  @Test
  void
      fileWithToggleOffOnMarkersIsLeftUnchangedBecauseSpotlessWouldRestoreEachFencedRegionOverWhicheverMemberMovedThere()
          throws IOException {
    Path source = createProject("toggle-off-on");
    Path fenced = source.resolveSibling("Fenced.java");
    String original = Files.readString(Fixtures.file("toggle-off-on/Fenced.java"));
    Files.writeString(fenced, original);

    gradle("spotlessApply").build();

    assertEquals(original, Files.readString(fenced));
    assertEquals(Fixtures.expected("defaults"), Files.readString(source));
  }

  @Test
  void nextBuildReusesTheConfigurationCacheAndSkipsFormattingBecauseTheStepIsSerializableAndStable()
      throws IOException {
    Path source = createProject("defaults");
    Files.writeString(source, Fixtures.expected("defaults"));

    BuildResult first = gradle("spotlessCheck").build();
    BuildResult second = gradle("spotlessCheck").build();

    assertTrue(first.getOutput().contains("Configuration cache entry stored."), first.getOutput());
    assertEquals(TaskOutcome.SUCCESS, outcome(first, ":spotlessJava"));
    assertTrue(
        second.getOutput().contains("Configuration cache entry reused."), second.getOutput());
    assertEquals(TaskOutcome.UP_TO_DATE, outcome(second, ":spotlessJava"));
  }

  private void checkFailsThenApplyWritesExpectedOutputThenCheckPasses(String scenario)
      throws IOException {
    Path source = createProject(scenario);

    BuildResult unordered = gradle("spotlessCheck").buildAndFail();
    assertTrue(unordered.getOutput().contains("format violations"), unordered.getOutput());

    gradle("spotlessApply").build();
    assertEquals(Fixtures.expected(scenario), Files.readString(source));

    BuildResult ordered = gradle("spotlessCheck").build();
    assertEquals(TaskOutcome.SUCCESS, outcome(ordered, ":spotlessJava"));
  }

  private Path createProject(String scenario) throws IOException {
    Files.copy(Fixtures.file("settings.gradle.kts"), project.resolve("settings.gradle.kts"));
    String build =
        Files.readString(Fixtures.file(scenario + "/build.gradle.kts"))
            .replace("<version>", Environment.libraryVersion())
            .replace("<spotless-version>", Environment.spotlessGradleVersion());
    Files.writeString(project.resolve("build.gradle.kts"), build);
    Files.writeString(
        initScript(),
        """
                allprojects {
                    buildscript.repositories.exclusiveContent {
                        forRepository {
                            buildscript.repositories.maven("%s")
                        }
                        filter {
                            includeModule("io.github.yagipass", "spotless-member-order")
                        }
                    }
                }
                """
            .formatted(Environment.repository().toUri()));
    return Fixtures.writeInput(project);
  }

  private GradleRunner gradle(String task) {
    return GradleRunner.create()
        .withProjectDir(project.toFile())
        .withTestKitDir(Environment.workDirectory().resolve("gradle-test-kit").toFile())
        .withArguments(
            task,
            "--configuration-cache",
            "--init-script",
            initScript().toString(),
            "--stacktrace");
  }

  private Path initScript() {
    return project.resolve("repository.init.gradle.kts");
  }

  private static TaskOutcome outcome(BuildResult result, String taskPath) {
    BuildTask task =
        Objects.requireNonNull(
            result.task(taskPath), () -> taskPath + " did not run:\n" + result.getOutput());
    return task.getOutcome();
  }
}
