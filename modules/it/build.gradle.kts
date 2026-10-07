plugins {
    id("java-conventions")
}

val libraryRepository = configurations.dependencyScope("libraryRepository")
val libraryRepositoryFiles = configurations.resolvable("libraryRepositoryFiles") {
    extendsFrom(libraryRepository.get())
    attributes {
        attribute(Usage.USAGE_ATTRIBUTE, objects.named("maven-repository"))
    }
}

val mavenDistribution = configurations.dependencyScope("mavenDistribution")
val mavenDistributionArchive = configurations.resolvable("mavenDistributionArchive") {
    extendsFrom(mavenDistribution.get())
    isTransitive = false
}

dependencies {
    testImplementation(gradleTestKit())

    libraryRepository.name(project(":plugin"))

    mavenDistribution.name(variantOf(libs.apache.maven) {
        classifier("bin")
        artifactType("zip")
    })
}

val unpackMaven = tasks.register<Sync>("unpackMaven") {
    description = "Unpacks the Maven distribution that the integration tests run."
    from(zipTree(mavenDistributionArchive.map { it.singleFile }))
    into(layout.buildDirectory.dir("maven"))
}

abstract class IntegrationTestEnvironment : CommandLineArgumentProvider {

    @get:Input
    abstract val libraryVersion: Property<String>

    @get:Input
    abstract val spotlessGradleVersion: Property<String>

    @get:Input
    abstract val spotlessMavenVersion: Property<String>

    @get:Internal
    abstract val repository: ConfigurableFileCollection

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
        "-DmemberOrder.repository=${repository.singleFile.absolutePath}",
        "-DmemberOrder.mavenHome=${mavenHome.get().asFile.absolutePath}",
        "-DmemberOrder.readme=${readme.get().asFile.absolutePath}",
        "-DmemberOrder.workDirectory=${workDirectory.get().asFile.absolutePath}",
    )
}

val mavenVersion = libs.versions.maven.get()

tasks.test {
    description = "Runs Spotless from Gradle and Maven consumer projects against the library published to a repository in the build directory."
    shouldRunAfter(":plugin:test")
    jvmArgumentProviders.add(objects.newInstance<IntegrationTestEnvironment>().apply {
        libraryVersion = project.version.toString()
        spotlessGradleVersion = libs.versions.spotless.gradle
        spotlessMavenVersion = libs.versions.spotless.maven
        repository.from(libraryRepositoryFiles)
        mavenHome.fileProvider(unpackMaven.map { it.destinationDir.resolve("apache-maven-$mavenVersion") })
        readme = layout.settingsDirectory.file("README.md")
        workDirectory = layout.buildDirectory.dir("work")
    })
}
