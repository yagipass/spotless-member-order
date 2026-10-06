import net.ltgt.gradle.errorprone.errorprone

plugins {
    `java-library`
    alias(libs.plugins.errorprone)
    alias(libs.plugins.maven.publish)
    alias(libs.plugins.spotless)
}

val integrationTestSourceSet = sourceSets.create("integrationTest")

val mavenDistribution = configurations.dependencyScope("mavenDistribution")
val mavenDistributionArchive = configurations.resolvable("mavenDistributionArchive") {
    extendsFrom(mavenDistribution.get())
    isTransitive = false
}

dependencies {
    implementation(libs.eclipse.jdt.core)
    compileOnly(libs.spotless.lib)
    compileOnly(libs.spotless.maven.plugin)
    compileOnly(libs.errorprone.annotations)
    compileOnly(libs.jspecify)

    testImplementation(libs.spotless.lib)
    testImplementation(libs.spotless.maven.plugin)
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platform.launcher)
    testCompileOnly(libs.errorprone.annotations)
    testCompileOnly(libs.jspecify)

    integrationTestSourceSet.implementationConfigurationName(gradleTestKit())
    integrationTestSourceSet.implementationConfigurationName(platform(libs.junit.bom))
    integrationTestSourceSet.implementationConfigurationName(libs.junit.jupiter)
    integrationTestSourceSet.runtimeOnlyConfigurationName(libs.junit.platform.launcher)
    integrationTestSourceSet.compileOnlyConfigurationName(libs.errorprone.annotations)
    integrationTestSourceSet.compileOnlyConfigurationName(libs.jspecify)

    errorprone(libs.errorprone.core)
    errorprone(libs.nullaway)
    errorprone(libs.errorprone.tidy)

    mavenDistribution.name(variantOf(libs.apache.maven) {
        classifier("bin")
        artifactType("zip")
    })
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
    (options as StandardJavadocDocletOptions).addStringOption("Xdoclint:all,-missing", "-quiet")
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release = 21
    options.compilerArgs.addAll(listOf("-Xlint:all", "-Werror"))
    options.errorprone {
        error(
            "DefaultLocale",
            "FinalClass",
            "InconsistentOverloads",
            "SuppressWarningsWithoutExplanation",
            "UngroupedOverloads",
            "UnusedException",
            "Var",
            "YodaCondition",
            "NullAway",
        )
        option("NullAway:AnnotatedPackages", "io.github.yagipass")
        option("NullAway:JSpecifyMode", true)
    }
}

val integrationTestDirectory = layout.buildDirectory.dir("integration-test")

publishing {
    repositories {
        maven {
            name = "integrationTest"
            url = uri(integrationTestDirectory.map { it.dir("repository") })
        }
    }
}

val unpackMaven = tasks.register<Sync>("unpackMaven") {
    description = "Unpacks the Maven distribution that the integration tests run."
    from(zipTree(mavenDistributionArchive.map { it.singleFile }))
    into(integrationTestDirectory.map { it.dir("maven") })
}

abstract class IntegrationTestEnvironment : CommandLineArgumentProvider {

    @get:Input
    abstract val libraryVersion: Property<String>

    @get:Input
    abstract val spotlessGradleVersion: Property<String>

    @get:Input
    abstract val spotlessMavenVersion: Property<String>

    @get:Internal
    abstract val repository: DirectoryProperty

    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    val publishedArtifacts: FileTree
        get() = repository.asFileTree.matching { include("**/*.jar", "**/*.pom", "**/*.module") }

    @get:InputDirectory
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val mavenHome: DirectoryProperty

    @get:InputFile
    @get:PathSensitive(PathSensitivity.NONE)
    abstract val readme: RegularFileProperty

    @get:Internal
    abstract val workDirectory: DirectoryProperty

    override fun asArguments(): List<String> = listOf(
        "-DmemberOrder.version=${libraryVersion.get()}",
        "-DmemberOrder.spotlessGradleVersion=${spotlessGradleVersion.get()}",
        "-DmemberOrder.spotlessMavenVersion=${spotlessMavenVersion.get()}",
        "-DmemberOrder.repository=${repository.get().asFile.absolutePath}",
        "-DmemberOrder.mavenHome=${mavenHome.get().asFile.absolutePath}",
        "-DmemberOrder.readme=${readme.get().asFile.absolutePath}",
        "-DmemberOrder.workDirectory=${workDirectory.get().asFile.absolutePath}",
    )
}

val mavenVersion = libs.versions.maven.get()

val integrationTest = tasks.register<Test>("integrationTest") {
    description = "Runs Spotless from Gradle and Maven consumer projects against the library published to a repository in the build directory."
    group = LifecycleBasePlugin.VERIFICATION_GROUP
    testClassesDirs = integrationTestSourceSet.output.classesDirs
    classpath = integrationTestSourceSet.runtimeClasspath
    shouldRunAfter(tasks.test)
    dependsOn("publishAllPublicationsToIntegrationTestRepository")
    jvmArgumentProviders.add(objects.newInstance<IntegrationTestEnvironment>().apply {
        libraryVersion = project.version.toString()
        spotlessGradleVersion = libs.versions.spotless.gradle
        spotlessMavenVersion = libs.versions.spotless.maven
        repository = integrationTestDirectory.map { it.dir("repository") }
        mavenHome.fileProvider(unpackMaven.map { it.destinationDir.resolve("apache-maven-$mavenVersion") })
        readme = layout.projectDirectory.file("README.md")
        workDirectory = integrationTestDirectory.map { it.dir("work") }
    })
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

tasks.check {
    dependsOn(integrationTest)
}

spotless {
    java {
        target("src/*/java/**/*.java")
        googleJavaFormat().reorderImports(true)
        removeUnusedImports()
        trimTrailingWhitespace()
        endWithNewline()
    }
}

mavenPublishing {
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
