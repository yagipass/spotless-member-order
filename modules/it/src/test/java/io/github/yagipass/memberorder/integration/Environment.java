package io.github.yagipass.memberorder.integration;

import java.nio.file.Path;

final class Environment {

  private Environment() {}

  static String libraryVersion() {
    return property("memberOrder.version");
  }

  static String spotlessGradleVersion() {
    return property("memberOrder.spotlessGradleVersion");
  }

  static String spotlessMavenVersion() {
    return property("memberOrder.spotlessMavenVersion");
  }

  static Path repository() {
    return Path.of(property("memberOrder.repository"));
  }

  static Path publishedFile(String extension) {
    String version = libraryVersion();
    return repository()
        .resolve("io/github/yagipass/spotless-member-order")
        .resolve(version)
        .resolve("spotless-member-order-" + version + extension);
  }

  static Path mavenHome() {
    return Path.of(property("memberOrder.mavenHome"));
  }

  static Path readme() {
    return Path.of(property("memberOrder.readme"));
  }

  static Path workDirectory() {
    return Path.of(property("memberOrder.workDirectory"));
  }

  private static String property(String name) {
    String value = System.getProperty(name);
    if (value == null) {
      throw new IllegalStateException(
          "System property "
              + name
              + " is not set. Run the integration tests with ./gradlew :it:test.");
    }
    return value;
  }
}
