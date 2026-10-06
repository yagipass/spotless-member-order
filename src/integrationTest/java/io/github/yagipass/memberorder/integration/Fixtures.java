package io.github.yagipass.memberorder.integration;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

final class Fixtures {

  private static final Path ROOT = root();

  private static final Set<String> BUILD_FILES = Set.of("build.gradle.kts", "pom.xml");

  private Fixtures() {}

  static Path file(String path) {
    return ROOT.resolve(path);
  }

  static String expected(String scenario) throws IOException {
    return Files.readString(file(scenario + "/expected.java"));
  }

  static Path writeInput(Path project) throws IOException {
    Path source = project.resolve("src/main/java/com/example/Example.java");
    Files.createDirectories(source.getParent());
    Files.copy(file("input.java"), source);
    return source;
  }

  static List<Path> buildFiles() throws IOException {
    try (Stream<Path> files = Files.walk(ROOT)) {
      return files
          .filter(file -> BUILD_FILES.contains(file.getFileName().toString()))
          .sorted()
          .toList();
    }
  }

  private static Path root() {
    try {
      return Objects.requireNonNull(
          Path.of(Objects.requireNonNull(Fixtures.class.getResource("/input.java")).toURI())
              .getParent());
    } catch (URISyntaxException e) {
      throw new IllegalStateException(e);
    }
  }
}
