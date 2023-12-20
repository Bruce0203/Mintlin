package buildsrc.plugins

plugins {
    id("buildsrc.plugins.base")
    kotlin("multiplatform")
}

kotlin {
    jvm {
        jvmToolchain(17)
    }
}
