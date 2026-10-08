# Contributing

## Setup

Run `nix develop` (or `direnv allow`). It provides JDK 25 and installs the git hooks. `./gradlew` downloads Gradle.

Without Nix, use JDK 21 or later. The git hooks are then not installed.

The build has not been tried on Windows.

## Build and test

The library is in `modules/plugin`, the integration tests in `modules/it`, and the build settings they share in `build-logic`.

| Command | Purpose |
|---|---|
| `./gradlew build` | compile, check formatting, run the unit and integration tests, and build the jars |
| `./gradlew :plugin:test` | unit tests only |
| `./gradlew :it:test` | integration tests only |
| `./gradlew spotlessApply` | format Java |
| `./gradlew publishToMavenLocal` | install the library into the local Maven repository to try it in another project; it is signed only when `signingInMemoryKey` is set |
| `nix fmt` | format Nix |
| `nix flake check` | run the git hooks over every tracked file, as CI does |

CI runs `./gradlew build` on JDK 21 and JDK 25. To do the same locally, run it again with `JAVA_HOME` set to a JDK 21.

### Unit test fixtures

Each directory in `modules/plugin/src/test/resources/fixtures` is one case: `input.java`, the `expected.java` that sorting must produce (leave it out when the input must stay unchanged), and an optional `settings.properties` that may set `order`, with the default from the README. Any other key fails the test. Every input is also sorted twice with four different settings to check that the second run changes nothing.

### Integration tests

`./gradlew :it:test` publishes the library to `modules/plugin/build/integration-test-repository`, then runs Spotless in small consumer projects made from `modules/it/src/test/resources`:

- Gradle through TestKit, with its own Gradle user home in `modules/it/build/work/gradle-test-kit`.
- Maven through the distribution that Gradle downloads (`apache-maven` in `gradle/libs.versions.toml`), with its local repository in `modules/it/build/work/maven-repository` and a generated settings file. `~/.m2/settings.xml` is not read.

Each scenario directory holds a Gradle build, a Maven build, and the `expected.java` that both must produce from `input.java`, except `toggle-off-on`, which holds only a Gradle build, expects `defaults/expected.java`, and also checks that its `Fenced.java` stays unchanged. The build files are the README examples: `ReadmeTest` fails if a README snippet is not part of one of them, so change both together. `<version>` and `<spotless-version>` in `build.gradle.kts` are replaced when the test copies it.

The first run downloads Spotless, google-java-format and their dependencies, so it needs network access. To run one class, use `./gradlew :it:test --tests '*MavenConsumerTest'`.

## Rules

- `./gradlew build` fails on unformatted Java and on any javac (`-Xlint:all -Werror`) or Error Prone warning, NullAway included. Every package, test packages too, needs a `package-info.java` with `@NullMarked`.
- Test fixtures in `modules/plugin/src/test/resources` and `modules/it/src/test/resources` are neither formatted nor touched by the whitespace hooks, and git checks them out with LF line endings. A fixture that needs CRLF line endings goes in `modules/plugin/src/test/resources/fixtures/crlf/<case>/`; git keeps those files byte for byte.
- Commit messages follow Conventional Commits, checked by the `commit-msg` hook.

## Proposing a change

Open an issue before you start on a new feature or a change to how members are ordered, with an input and the output you expect. A small fix, such as a typo, can go straight to a pull request.

A pull request:

- references its issue (`Closes #N`).
- adds tests. A bug fix adds a test that reproduces the bug.
- passes `./gradlew build`.

## Release

Merge the release pull request that [tagpr](https://github.com/Songmu/tagpr) keeps open, after approving its checks. Label it `tagpr:minor` or `tagpr:major` for more than a patch release. `build.yml` then tags `vX.Y.Z`, uploads the signed artifacts to Maven Central, attaches the jar to the GitHub release, and publishes the release. Then publish the deployment by hand in the [Central Portal](https://central.sonatype.com/publishing/deployments).

This needs Actions to be allowed to create pull requests, and four secrets: `MAVEN_CENTRAL_TOKEN_USERNAME` and `MAVEN_CENTRAL_TOKEN_PASSWORD` (a Central Portal user token), and `MAVEN_GPG_PRIVATE_KEY` and `MAVEN_GPG_PASSPHRASE` (the ASCII-armored signing key and its passphrase).
