apply(from = "../gradle/repositories.settings.gradle")
plugins {
    buildsrc.plugins.kmp
    alias(libs.plugins.kotlinx.serialization)
}

kotlin.sourceSets {
    commonMain {
        dependencies {
            api(libs.kotlinx.io)
            api(libs.kotlinx.serialization.core)
            api(libs.kotlinx.serialization.json)
            api(libs.kotlinx.atomicfu)
            api(libs.kotlinx.datetime)
            api(libs.kotlin.reflect)
        }
    }
    jvmTest {
        tasks.withType<Test>().configureEach { useJUnitPlatform() }
        dependencies {
            implementation(libs.kotest.junit.runner)
            implementation(libs.kotest.engine)
        }
    }
}

