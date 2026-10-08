# spotless-member-order

[![CI](https://github.com/yagipass/spotless-member-order/actions/workflows/build.yml/badge.svg?branch=main)](https://github.com/yagipass/spotless-member-order/actions/workflows/build.yml?query=branch%3Amain)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.yagipass/spotless-member-order)](https://central.sonatype.com/artifact/io.github.yagipass/spotless-member-order)
[![License](https://img.shields.io/github/license/yagipass/spotless-member-order)](LICENSE)
![Java 21+](https://img.shields.io/badge/Java-21%2B-blue)

A [Spotless](https://github.com/diffplug/spotless) step for Gradle and Maven that orders the members of Java types by category and, optionally, visibility. Members in the same group keep their source order, and nothing is sorted by name. With the default settings, the step turns the first class below into the second.

```java
class Example {
    void zebra() {}

    private int count;

    Example() {}

    void apple() {}

    static final String NAME = "example";
}
```

```java
class Example {
    static final String NAME = "example";

    private int count;

    Example() {}

    void zebra() {}

    void apple() {}
}
```

Only members move, each with its Javadoc, annotations, the comments directly above it, and the comments at the end of its last line. Nothing else changes.

## Requirements

- Gradle or Maven running on Java 21 or later
- Spotless Gradle plugin 7.0.0 or Spotless Maven plugin 2.44.0 or later

## Usage

Replace `<version>` and `${spotless-member-order.version}` with the [latest version](https://central.sonatype.com/artifact/io.github.yagipass/spotless-member-order), and `<spotless-version>` and `${spotless.version}` with your Spotless version.

### Gradle

Add the step to `build.gradle.kts`.

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

In a multi-project build, put the `buildscript` block in the root project together with `id("com.diffplug.spotless") version "<spotless-version>" apply false`, so that the library is on the Spotless plugin's classloader.

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

`implementation` replaces Spotless's `<java>` with a subclass that adds `<memberOrder>`. Everything else in `<java>` works as before. It cannot be combined with another library that replaces `<java>` the same way.

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

`order` is a comma-separated list of entries. The default, `T,SF,SM,F,C,M`, follows Eclipse's default order and ignores visibility.

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

Field initializers and initializer blocks run in source order, so moving them can change behavior or break compilation. While `SF` is a single entry and `SI` is left out, as in the default, static fields and static initializers stay together in source order at the place of `SF`. The same goes for `F` and `I`.

Listing `SI`, or splitting `SF` by visibility, orders them like other members, and likewise for `F` and `I`. Splitting `SF` requires listing `SI`, and splitting `F` requires listing `I`. Reorder them only if no field or initializer depends on another. For example, `SF:BRD,SF:V,SI` moves `public static final int LIMIT = BASE * 2;` above `private static final int BASE = 10;`, which then fails to compile.

## Differences from Eclipse Sort Members

Spotless also offers Eclipse's Sort Members through `eclipse().sortMembersEnabled(true)`.

- Members in the same group keep their source order. Eclipse sorts methods and types by name.
- A category can be split by visibility and the parts placed anywhere, such as private static methods after all instance methods.
- Visibility follows the Java language. Eclipse treats interface methods without `public` and enum constructors without a modifier as package-private.
- There are no `// @SortMembers:` comments to override the settings per file.

## Limitations

The step leaves code unchanged rather than risk moving it wrongly. It leaves a whole file unchanged in these cases.

- Eclipse JDT cannot parse the file at the newest Java version it supports, such as old code that uses `enum` or `assert` as an identifier, or a file that starts with a byte order mark.
- The sorted file would not parse.
- The file contains `spotless:off` or `spotless:on`. Custom `toggleOffOn` markers or regexes are not detected, so a file that uses them can lose or swap code.

It leaves a type body unsorted, while still sorting the bodies nested in it, in these cases.

- A comment between members belongs to neither or could belong to either, such as a section heading with blank lines around it, or a comment directly below a member followed by a blank line.
- Anything other than whitespace follows the last member, such as a comment, or separates members, such as a stray `;`.
- A member shares its last line with the next member or the closing brace, as in `int x; int y;` or `int z; }`.

The top-level fields and methods of a Java 25 compact source file are not sorted. Classes inside it are.

The step does not order members by anything other than category and visibility, move enum constants, reorder top-level types, or keep `// region` blocks together.

## Development

See [CONTRIBUTING.md](CONTRIBUTING.md).

## License

[Apache License 2.0](LICENSE)
