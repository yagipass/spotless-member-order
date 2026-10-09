# spotless-member-order

[![CI](https://github.com/yagipass/spotless-member-order/actions/workflows/build.yml/badge.svg?branch=main)](https://github.com/yagipass/spotless-member-order/actions/workflows/build.yml?query=branch%3Amain)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.yagipass/spotless-member-order)](https://central.sonatype.com/artifact/io.github.yagipass/spotless-member-order)
[![License](https://img.shields.io/github/license/yagipass/spotless-member-order)](LICENSE)
![Java 21+](https://img.shields.io/badge/Java-21%2B-blue)

A [Spotless](https://github.com/diffplug/spotless) step for Gradle and Maven that orders the members of Java types by category and visibility. Members in the same group keep their source order.

```java
// Before
class Example {
    void zebra() {}

    private int count;

    Example() {}

    void apple() {}

    static final String NAME = "example";
}

// After, with the default order
class Example {
    static final String NAME = "example";

    private int count;

    Example() {}

    void zebra() {}

    void apple() {}
}
```

Members move with their Javadoc, annotations, and comments. Nothing else changes.

## Usage

Requires Gradle or Maven on Java 21 or later, with Spotless Gradle plugin 7.0.0 or Spotless Maven plugin 2.44.0 or later.

### Gradle

```kotlin
import io.github.yagipass.memberorder.MemberOrderStep

buildscript {
    dependencies {
        classpath("io.github.yagipass:spotless-member-order:<version>")
    }
}

plugins {
    java
    id("com.diffplug.spotless") version "<spotless-version>"
}

spotless {
    java {
        addStep(MemberOrderStep.create())
    }
}
```

In a multi-project build, put `buildscript` in the root project, which also needs `id("com.diffplug.spotless") version "<spotless-version>" apply false`.

### Maven

```xml
<plugin>
  <groupId>com.diffplug.spotless</groupId>
  <artifactId>spotless-maven-plugin</artifactId>
  <version>${spotless.version}</version>
  <dependencies>
    <dependency>
      <groupId>io.github.yagipass</groupId>
      <artifactId>spotless-member-order</artifactId>
      <version>${spotless-member-order.version}</version>
    </dependency>
  </dependencies>
  <configuration>
    <java implementation="io.github.yagipass.memberorder.maven.Java">
      <memberOrder/>
    </java>
  </configuration>
</plugin>
```

`implementation` cannot be combined with another library that replaces `<java>`.

### With a formatter

Blank lines stay where they were, so put a formatter after `memberOrder`.

```kotlin
spotless {
    java {
        addStep(MemberOrderStep.create())
        googleJavaFormat()
    }
}
```

```xml
<java implementation="io.github.yagipass.memberorder.maven.Java">
  <memberOrder/>
  <googleJavaFormat/>
</java>
```

## Configuration

`order` is a comma-separated list of entries. The default is `T,SF,SM,F,C,M`, Eclipse's default order.

| Code | Category |
|---|---|
| `T` | member types |
| `SF` | static fields |
| `SI` | static initializers |
| `SM` | static methods |
| `F` | instance fields |
| `I` | instance initializers |
| `C` | constructors |
| `M` | methods and annotation type elements |

| Code | Visibility |
|---|---|
| `B` | public |
| `R` | protected |
| `D` | package-private |
| `V` | private |

An entry is a category, such as `M` for all methods, or a category with visibilities in `BRDV` order, such as `M:BRD` for the methods that are not private. Every category and visibility pair must be in exactly one entry, but `SI` and `I` can be left out. Members in the same entry keep their source order. An invalid `order` fails the build.

This order puts private types and constructors after the others, and private methods, static or not, after all other methods.

```kotlin
spotless {
    java {
        addStep(
            MemberOrderStep.builder()
                .order("T:BRD,T:V,SF,F,C:BRD,C:V,SM:BRD,M:BRD,SM:V,M:V")
                .build()
        )
    }
}
```

```xml
<memberOrder>
  <order>T:BRD,T:V,SF,F,C:BRD,C:V,SM:BRD,M:BRD,SM:V,M:V</order>
</memberOrder>
```

### Fields and initializers

Unless `SI` is listed, static fields and static initializers stay together in source order at `SF`. The same goes for `F` and `I`. Listing them reorders them like other members, which breaks code where one depends on another.

```java
// T,SF:BRD,SF:V,SI,SM,F,C,M moves LIMIT above BASE, which then fails to compile.
private static final int BASE = 10;
public static final int LIMIT = BASE * 2;
```

## Differences from Eclipse Sort Members

This step differs from Spotless's `eclipse().sortMembersEnabled(true)` in these ways.

- It keeps source order where Eclipse sorts methods and types by name.
- It can split a category by visibility and place the parts anywhere.
- It follows the Java language for visibility, where Eclipse treats interface methods without `public` and enum constructors without a modifier as package-private.
- It has no `// @SortMembers:` comments to override the settings per file.

## Limitations

The step leaves a whole file unchanged in these cases.

- Eclipse JDT cannot parse the file at the newest Java version it supports, for example because it uses `enum` as an identifier or starts with a byte order mark.
- The sorted file would not parse.
- The file contains `spotless:off` or `spotless:on`. Custom `toggleOffOn` markers are not detected, so a file that uses them can lose or swap code.

It leaves a type body unsorted, but still sorts the bodies nested in it, in these cases.

- A comment between members could belong to neither or either, such as a heading with blank lines around it, or a comment directly below a member followed by a blank line.
- Anything other than whitespace follows the last member or separates members.
- A member shares its last line with the next member or the closing brace.

It does not sort the top-level members of Java 25 compact source files, move enum constants, reorder top-level types, or keep `// region` blocks together.

## Development

See [CONTRIBUTING.md](CONTRIBUTING.md).

## License

[Apache License 2.0](LICENSE)
