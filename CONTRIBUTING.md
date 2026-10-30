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
| `nix run --inputs-from . nixpkgs#gitleaks -- git --log-opts="--full-history --all --diff-filter=tuxdb --diff-merges=first-parent" --redact --verbose` | scan the whole git history for secrets, as CI does |

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
- Name branches `<type>/<slug>` after the Conventional Commits type. `.github/labeler.yml` labels the pull request by it, `feat/` as `enhancement`, `fix/` as `bug`, and `docs/` as `documentation`, and `.github/release.yml` groups the changelog by these labels and Dependabot's `dependencies`.

## Proposing a change

Open an issue before you start on a new feature or a change to how members are ordered, with an input and the output you expect. A small fix, such as a typo, can go straight to a pull request.

A pull request:

- references its issue (`Closes #N`), if it has one.
- adds tests when it changes behavior. A bug fix adds a test that reproduces the bug.
- passes `./gradlew build`.
- has a Conventional Commits title. Pull requests are squash-merged, so the title becomes the commit on `main`. It is also the pull request's entry in `CHANGELOG.md`.

## Release

Before merging a pull request, label it by hand if it comes from a fork, which the labeler skips, and add `breaking-change` if it breaks existing users.

Merge the release pull request that [tagpr](https://github.com/Songmu/tagpr) keeps open, after approving its checks. It is a minor release if a merged pull request is labeled `enhancement` or `breaking-change`, and a patch release otherwise. Label it `tagpr:major` for a major release. `release.yml` then tags `vX.Y.Z`, builds and tests it again, uploads the signed artifacts to Maven Central, attaches the jar to the GitHub release, and publishes the release. Then publish the deployment by hand in the [Central Portal](https://central.sonatype.com/publishing/deployments).

tagpr writes `CHANGELOG.md` and the version in `gradle.properties`, so do not edit them by hand.

This needs the "Allow GitHub Actions to create and approve pull requests" setting, which `.github/infra.yml` turns on, and an environment `maven-central` that only `main` can deploy to, with four secrets: `MAVEN_CENTRAL_TOKEN_USERNAME` and `MAVEN_CENTRAL_TOKEN_PASSWORD` (a Central Portal user token), and `MAVEN_GPG_PRIVATE_KEY` and `MAVEN_GPG_PASSPHRASE` (the ASCII-armored signing key and its passphrase).

## Repository settings

The repository settings, labels, rulesets, and Actions permissions are managed with [gh-infra](https://github.com/babarot/gh-infra) in `.github/infra.yml`. To change them, edit the file, review the difference with `gh infra plan .github/infra.yml`, and apply it with `gh infra apply .github/infra.yml`. Applying needs admin access to the repository.

Environments and secrets are not managed by gh-infra. Change them in the repository settings on GitHub.
