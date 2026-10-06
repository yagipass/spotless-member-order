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
        addStep(
            MemberOrderStep.builder()
                .categoryOrder("SF,SI,F,I,C,SM,M,T")
                .visibilityOrder("B,R,D,V")
                .sortFields(true)
                .build()
        )
    }
}
