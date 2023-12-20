apply(from = "../gradle/repositories.settings.gradle")
plugins {
    alias(libs.plugins.kotlinx.serialization)
    buildsrc.plugins.kmp
    buildsrc.plugins.shadow
    application
}

application {
    mainClass.set("mintlin.minecraft.server.MainKt")
}

kotlin.sourceSets {
    commonMain {
        dependencies {
            api(project(":foundation"))
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
