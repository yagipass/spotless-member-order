# spotless-member-order

[![CI](https://github.com/yagipass/spotless-member-order/actions/workflows/build.yml/badge.svg?branch=main)](https://github.com/yagipass/spotless-member-order/actions/workflows/build.yml?query=branch%3Amain)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.yagipass/spotless-member-order)](https://central.sonatype.com/artifact/io.github.yagipass/spotless-member-order)
[![License](https://img.shields.io/github/license/yagipass/spotless-member-order)](LICENSE)
![Java 21+](https://img.shields.io/badge/Java-21%2B-blue)

A [Spotless](https://github.com/diffplug/spotless) step for Gradle and Maven that orders the members of Java types by category and, optionally, by visibility. Members in the same group keep their original order: nothing is sorted by name.

```java
class Example {
    void zebra() {
    }

    private int count;

    Example() {
    }

    void apple() {
    }

    static final String NAME = "example";
}
```

becomes, with the default settings:

```java
class Example {
    static final String NAME = "example";

    private int count;

    Example() {
    }

    void zebra() {
    }

    void apple() {
    }
}
```

The step only moves members. A member takes along its Javadoc, its annotations, the comments directly above it, and the comments at the end of its last line. It does not change their text, the whitespace between them, imports, or anything else.

## Usage

The step needs Spotless Gradle plugin 7.0.0 or Spotless Maven plugin 2.44.0 or later. Earlier versions lack the step API it uses and fail with `NoClassDefFoundError: com/diffplug/spotless/SerializedFunction`. The integration tests run the Spotless versions in [`gradle/libs.versions.toml`](gradle/libs.versions.toml); 7.0.0 and 2.44.0 were checked once by hand.

In the examples, `<version>` and `${spotless-member-order.version}` stand for the version in the Maven Central badge above, and `<spotless-version>` and `${spotless.version}` for the Spotless plugin version you use.

### Gradle

`build.gradle.kts`:

```kotlin
import io.github.yagipass.memberorder.MemberOrderStep

buildscript {
    repositories {
        mavenCentral()
    }
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

The library must be on the same classloader as the Spotless plugin. In a multi-project build, put the `buildscript` block in the root project together with `id("com.diffplug.spotless") version "<spotless-version>" apply false`.

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

`implementation` swaps Spotless's `<java>` for a subclass that also accepts `<memberOrder>`. All the other `<java>` settings and steps work as before. Because a POM has only one `<java>`, this cannot be combined with another library that extends `<java>` the same way.

### With a formatter

Blank lines stay where they were, so the spacing between members can look different after sorting. Put `memberOrder` before a formatter, which fixes it.

```kotlin
repositories {
    mavenCentral()
}

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

| Setting | Default | |
|---|---|---|
| `categoryOrder` | `T,SF,SI,SM,F,I,C,M` | Order of the categories (Eclipse's default) |
| `visibilityOrder` | not set | Order of the visibilities within each category. When not set, visibility does not affect the order |
| `sortFields` | `false` | Also reorder fields and initializers. See [Fields and initializers](#fields-and-initializers) |

Categories: `T` member types, `SF` static fields, `SI` static initializers, `SM` static methods, `F` instance fields, `I` instance initializers, `C` constructors, `M` methods and annotation type elements.
Visibilities: `B` public, `R` protected, `D` package-private, `V` private.

Each order lists every code exactly once, separated by commas, and `sortFields` is `true` or `false`. Anything else fails the build with a message saying what is wrong. In Maven, an empty element, or one that holds an undefined property, counts as not set.

```kotlin
spotless {
    java {
        addStep(
            MemberOrderStep.builder()
                .categoryOrder("SF,SI,F,I,C,SM,M,T")
                .visibilityOrder("B,R,D,V")
                .sortFields(true)
                .build()
        )
    }
}
```

```xml
<memberOrder>
  <categoryOrder>SF,SI,F,I,C,SM,M,T</categoryOrder>
  <visibilityOrder>B,R,D,V</visibilityOrder>
  <sortFields>true</sortFields>
</memberOrder>
```

Members of interfaces and annotation types count as public unless declared private, and enum constructors count as private, as in the Java language.

### Fields and initializers

Field initializers and initializer blocks run in source order, so moving them can change what a program does, or stop it compiling (`int x = y; int y = 1;`). By default (`sortFields` false):

- static initializers are ordered as static fields (`SF`), and instance initializers as instance fields (`F`). `SI` and `I` have no effect.
- fields and initializers are not ordered by visibility.

So static fields and static initializers keep their relative order, and so do instance fields and instance initializers. Static and instance members may still move relative to each other, which does not change behavior: static ones run when the class is initialized, instance ones when an object is created.

`sortFields(true)` / `<sortFields>true</sortFields>` orders them like any other member. Only use it if no field initializer or initializer block depends on another one.

## Differences from Eclipse Sort Members

Compared with Eclipse's Sort Members, which Spotless also offers through `eclipse().sortMembersEnabled(true)`:

- Members in the same group keep their source order. Eclipse sorts methods and types, and fields unless "do not sort fields" is on, by name.
- Visibility follows the Java language. Eclipse counts as package-private both the interface methods without an explicit `public` (abstract, default, and static ones) and the enum constructors without a modifier.
- An invalid setting fails the build instead of silently falling back to the default.
- There are no `// @SortMembers:` comments to override the settings per file.

## Java versions

The step runs on Java 21 or later, so Gradle or Maven must run on Java 21 or later. It parses sources at the newest Java version that the bundled Eclipse JDT Core supports, whatever the JDK or the project's `--release`.

## Limitations

The step leaves code unchanged rather than risk moving it wrongly:

- A file that Eclipse JDT cannot parse is left unchanged. This includes old code that uses `enum` or `assert` as an identifier, and files that start with a byte order mark, which `javac` rejects too. Errors that only the compiler reports, such as `int i = 09;`, do not stop the sorting.
- A file that contains `spotless:off` or `spotless:on` is left unchanged, because `toggleOffOn()` would put the text between them back where it was, over whichever member had moved there. Custom markers set with `toggleOffOn("...", "...")` are not detected, so a file that uses them can lose or swap code.
- A file is left unchanged if the sorted file would not parse, such as when a Javadoc sits between a record header and its `{`, or just before the `;` that ends the enum constants.
- A type body is left unsorted, while the bodies nested in it are still sorted, when:
  - a comment between two members belongs to neither, such as a section heading with blank lines around it.
  - a comment directly below a member is followed by a blank line, since it could describe either neighbor.
  - anything other than whitespace follows the last member, such as a comment.
  - a member shares its last line with the next member or with the closing brace (`int x; int y;`, `int z; }`).
  - something other than whitespace separates members, such as a stray `;`.
- The top-level fields and methods of a compact source file (Java 25) are not sorted. Classes inside it are.

Not supported:

- Ordering by name, parameters, or anything other than category and visibility.
- Working out which fields can safely move.
- Moving enum constants or reordering top-level types.
- Keeping `// region` blocks together: region comments are treated like any other comment.

## Development

See [CONTRIBUTING.md](CONTRIBUTING.md).

## License

[Apache License 2.0](LICENSE)
