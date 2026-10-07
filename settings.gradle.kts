pluginManagement {
    includeBuild("build-logic")
}

rootProject.name = "spotless-member-order"

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

include("plugin", "it")

project(":plugin").projectDir = file("modules/plugin")
project(":it").projectDir = file("modules/it")
