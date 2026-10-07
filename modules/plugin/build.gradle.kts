plugins {
    id("java-conventions")
    `java-library`
    alias(libs.plugins.maven.publish)
}

val artifactId = "spotless-member-order"

base {
    archivesName = artifactId
}

dependencies {
    implementation(libs.eclipse.jdt.core)
    compileOnly(libs.spotless.lib)
    compileOnly(libs.spotless.maven.plugin)

    testImplementation(libs.spotless.lib)
    testImplementation(libs.spotless.maven.plugin)
}

val generateLibraryVersion = tasks.register("generateLibraryVersion") {
    description = "Generates the class that holds the library version, which the formatter step state includes."
    val version = project.version.toString()
    val outputDir = layout.buildDirectory.dir("generated/sources/libraryVersion/java/main")
    inputs.property("version", version)
    outputs.dir(outputDir)
    doLast {
        val file = outputDir.get().file("io/github/yagipass/memberorder/LibraryVersion.java").asFile
        file.parentFile.mkdirs()
        file.writeText(
            """
            |package io.github.yagipass.memberorder;
            |
            |final class LibraryVersion {
            |
            |    static final String VALUE = "$version";
            |
            |    private LibraryVersion() {
            |    }
            |}
            |""".trimMargin()
        )
    }
}

sourceSets.main {
    java.srcDir(generateLibraryVersion)
}

tasks.javadoc {
    title = "$artifactId ${project.version} API"
    (options as StandardJavadocDocletOptions).addStringOption("Xdoclint:all,-missing", "-quiet")
}

val integrationTestRepository = layout.buildDirectory.dir("integration-test-repository")

publishing {
    repositories {
        maven {
            name = "integrationTest"
            url = uri(integrationTestRepository)
        }
    }
}

configurations.consumable("integrationTestRepository") {
    description = "The Maven repository that the library is published to for the integration tests."
    attributes {
        attribute(Usage.USAGE_ATTRIBUTE, objects.named("maven-repository"))
    }
    outgoing.artifact(integrationTestRepository) {
        builtBy("publishAllPublicationsToIntegrationTestRepository")
    }
}

mavenPublishing {
    coordinates(artifactId = artifactId)
    publishToMavenCentral()
    if (providers.gradleProperty("signingInMemoryKey").isPresent) {
        signAllPublications()
    }
    pom {
        name = "spotless-member-order"
        description = "Spotless formatter step that orders Java type members by category and visibility, keeping their original order within each group"
        url = "https://github.com/yagipass/spotless-member-order"
        licenses {
            license {
                name = "Apache-2.0"
                url = "https://www.apache.org/licenses/LICENSE-2.0.txt"
            }
        }
        developers {
            developer {
                id = "yagipass"
                name = "yagipass"
                url = "https://github.com/yagipass"
            }
        }
        scm {
            connection = "scm:git:https://github.com/yagipass/spotless-member-order.git"
            developerConnection = "scm:git:ssh://git@github.com/yagipass/spotless-member-order.git"
            url = "https://github.com/yagipass/spotless-member-order"
        }
    }
}
