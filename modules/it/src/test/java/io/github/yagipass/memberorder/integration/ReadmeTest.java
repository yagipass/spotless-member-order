package io.github.yagipass.memberorder.integration;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;

class ReadmeTest {

  private static final Pattern BUILD_SNIPPET =
      Pattern.compile("```(?:kotlin|xml)\\n(.*?)```", Pattern.DOTALL);

  @Test
  void everyBuildSnippetInTheReadmeIsPartOfAConsumerProjectThatTheIntegrationTestsRun()
      throws IOException {
    List<List<String>> testedBuildFiles = new ArrayList<>();
    for (Path file : Fixtures.buildFiles()) {
      testedBuildFiles.add(significantLines(Files.readString(file)));
    }
    Matcher snippets = BUILD_SNIPPET.matcher(Files.readString(Environment.readme()));

    List<String> checked = new ArrayList<>();
    while (snippets.find()) {
      List<String> snippet = significantLines(snippets.group(1));
      assertTrue(
          testedBuildFiles.stream()
              .anyMatch(file -> Collections.indexOfSubList(file, snippet) >= 0),
          "This README snippet is not part of any build file in modules/it/src/test/resources:\n"
              + snippets.group(1));
      checked.add(snippets.group(1));
    }

    assertTrue(
        checked.stream().anyMatch(snippet -> snippet.contains("addStep(")),
        "no Gradle snippet found in the README");
    assertTrue(
        checked.stream().anyMatch(snippet -> snippet.contains("<memberOrder")),
        "no Maven snippet found in the README");
  }

  private static List<String> significantLines(String text) {
    return text.lines().map(String::strip).filter(line -> !line.isEmpty()).toList();
  }
}
