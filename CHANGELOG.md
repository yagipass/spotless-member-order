# Changelog

## [v0.3.1](https://github.com/yagipass/spotless-member-order/compare/v0.3.0...v0.3.1) - 2026-10-31

### Dependencies
- build(deps): bump the actions group with 2 updates by @dependabot[bot] in https://github.com/yagipass/spotless-member-order/pull/42
- build(deps): bump gradle-wrapper from 9.8.1 to 9.8.2 by @dependabot[bot] in https://github.com/yagipass/spotless-member-order/pull/53
- build(deps): bump Songmu/tagpr from 1.21.2 to 1.21.3 in the actions group by @dependabot[bot] in https://github.com/yagipass/spotless-member-order/pull/54
- build(deps): bump Songmu/tagpr from 1.21.3 to 1.21.4 in the actions group by @dependabot[bot] in https://github.com/yagipass/spotless-member-order/pull/64
- build(deps): bump treefmt-nix from `27b3b12` to `0d6d739` in the flake-inputs group by @dependabot[bot] in https://github.com/yagipass/spotless-member-order/pull/63
- build(deps): bump errorprone from 2.50.0 to 2.51.0 by @dependabot[bot] in https://github.com/yagipass/spotless-member-order/pull/62
- build(deps): bump com.uber.nullaway:nullaway from 0.14.2 to 0.14.3 by @dependabot[bot] in https://github.com/yagipass/spotless-member-order/pull/61
### Other Changes
- build: stop overlapping and hung workflow runs by @yagipass in https://github.com/yagipass/spotless-member-order/pull/31
- build: publish without the Gradle cache and delay Dependabot updates by @yagipass in https://github.com/yagipass/spotless-member-order/pull/32
- build: keep the Maven Central secrets in a main-only environment by @yagipass in https://github.com/yagipass/spotless-member-order/pull/34
- build: queue release runs on main so that none of them is skipped by @yagipass in https://github.com/yagipass/spotless-member-order/pull/36
- build: manage the repository settings with gh-infra by @yagipass in https://github.com/yagipass/spotless-member-order/pull/38
- build: group the changelog by kind of change and bump minor for features by @yagipass in https://github.com/yagipass/spotless-member-order/pull/41
- build: configure CodeRabbit reviews for this repository by @yagipass in https://github.com/yagipass/spotless-member-order/pull/44
- build: put pre-commit on PATH in the nix develop shell by @yagipass in https://github.com/yagipass/spotless-member-order/pull/46
- build: lint workflows with jactionlint instead of actionlint by @yagipass in https://github.com/yagipass/spotless-member-order/pull/48
- build: keep CodeRabbit's summary out of pull request descriptions by @yagipass in https://github.com/yagipass/spotless-member-order/pull/52
- build: configure cubic reviews for this repository by @yagipass in https://github.com/yagipass/spotless-member-order/pull/50
- docs: say what a pull request title becomes and what tagpr writes by @yagipass in https://github.com/yagipass/spotless-member-order/pull/58
- build: tighten the secret, workflow, and flake input checks by @yagipass in https://github.com/yagipass/spotless-member-order/pull/60

## [v0.3.0](https://github.com/yagipass/spotless-member-order/compare/v0.2.0...v0.3.0) - 2026-10-16

- build(deps): bump nixpkgs from `73e728d` to `8edc0c7` by @dependabot[bot] in https://github.com/yagipass/spotless-member-order/pull/16
- build(deps): bump com.diffplug.spotless:spotless-maven-plugin from 3.10.3 to 3.10.4 by @dependabot[bot] in https://github.com/yagipass/spotless-member-order/pull/17
- build(deps): bump Songmu/tagpr from 1.21.1 to 1.21.2 in the actions group by @dependabot[bot] in https://github.com/yagipass/spotless-member-order/pull/18
- build(deps): bump com.diffplug.spotless:spotless-lib from 4.10.3 to 4.10.4 by @dependabot[bot] in https://github.com/yagipass/spotless-member-order/pull/20
- build(deps): bump gradle-wrapper from 9.8.0 to 9.8.1 by @dependabot[bot] in https://github.com/yagipass/spotless-member-order/pull/21
- build(deps): bump com.diffplug.spotless from 8.10.3 to 8.10.4 by @dependabot[bot] in https://github.com/yagipass/spotless-member-order/pull/19
- feat!: keep overloaded methods and constructors together by @yagipass in https://github.com/yagipass/spotless-member-order/pull/24
- build: keep the gitleaks hook scanning files with newer git-hooks.nix by @yagipass in https://github.com/yagipass/spotless-member-order/pull/26
- build: lock GitHub Actions dependencies with a workflow lockfile by @yagipass in https://github.com/yagipass/spotless-member-order/pull/28

## [v0.2.0](https://github.com/yagipass/spotless-member-order/compare/v0.1.0...v0.2.0) - 2026-10-10

- docs: drop the buildscript repositories block from the Gradle example by @yagipass in https://github.com/yagipass/spotless-member-order/pull/8
- docs: shorten the README to what users need by @yagipass in https://github.com/yagipass/spotless-member-order/pull/11
- docs: shorten the README further and show examples in code blocks by @yagipass in https://github.com/yagipass/spotless-member-order/pull/13
- feat: place members with a given annotation together in the order by @yagipass in https://github.com/yagipass/spotless-member-order/pull/14

## [v0.1.0](https://github.com/yagipass/spotless-member-order/commits/v0.1.0) - 2026-10-08

- build(deps): bump org.apache.maven:apache-maven from 3.9.16 to 3.10.0 by @dependabot[bot] in https://github.com/yagipass/spotless-member-order/pull/1
- build: split the build into plugin and integration test projects by @yagipass in https://github.com/yagipass/spotless-member-order/pull/4
- feat!: order members by category and visibility pairs with one setting by @yagipass in https://github.com/yagipass/spotless-member-order/pull/6
