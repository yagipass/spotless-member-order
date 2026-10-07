import net.ltgt.gradle.errorprone.errorprone

plugins {
    java
    id("net.ltgt.errorprone")
    id("com.diffplug.spotless")
}

val libs = the<VersionCatalogsExtension>().named("libs")

fun library(alias: String): Provider<MinimalExternalModuleDependency> = libs.findLibrary(alias).get()

dependencies {
    compileOnly(library("errorprone-annotations"))
    compileOnly(library("jspecify"))

    testImplementation(platform(library("junit-bom")))
    testImplementation(library("junit-jupiter"))
    testRuntimeOnly(library("junit-platform-launcher"))
    testCompileOnly(library("errorprone-annotations"))
    testCompileOnly(library("jspecify"))

    errorprone(library("errorprone-core"))
    errorprone(library("nullaway"))
    errorprone(library("errorprone-tidy"))
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

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
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
