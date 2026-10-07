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

repositories {
    mavenCentral()
}

spotless {
    java {
        addStep(MemberOrderStep.create())
        googleJavaFormat()
    }
}
