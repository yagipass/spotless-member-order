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
        addStep(
            MemberOrderStep.builder()
                .order("T:BRD,T:V,SF,F,C:BRD,C:V,SM:BRD,M:BRD,SM:V,M:V")
                .build()
        )
    }
}
