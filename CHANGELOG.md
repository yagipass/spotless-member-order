# Changelog

## [v0.3.1](https://github.com/yagipass/spotless-member-order/compare/v0.3.0...v0.3.1) - 2026-10-24

### Dependencies
- build(deps): bump the actions group with 2 updates by @dependabot[bot] in https://github.com/yagipass/spotless-member-order/pull/42
### Other Changes
- build: stop overlapping and hung workflow runs by @yagipass in https://github.com/yagipass/spotless-member-order/pull/31
- build: publish without the Gradle cache and delay Dependabot updates by @yagipass in https://github.com/yagipass/spotless-member-order/pull/32
- build: keep the Maven Central secrets in a main-only environment by @yagipass in https://github.com/yagipass/spotless-member-order/pull/34
- build: queue release runs on main so that none of them is skipped by @yagipass in https://github.com/yagipass/spotless-member-order/pull/36
- build: manage the repository settings with gh-infra by @yagipass in https://github.com/yagipass/spotless-member-order/pull/38
- build: group the changelog by kind of change and bump minor for features by @yagipass in https://github.com/yagipass/spotless-member-order/pull/41
- build: configure CodeRabbit reviews for this repository by @yagipass in https://github.com/yagipass/spotless-member-order/pull/44

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
