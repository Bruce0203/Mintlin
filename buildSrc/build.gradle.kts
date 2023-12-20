apply(from = "../gradle/repositories.settings.gradle")
plugins {
    `kotlin-dsl`
    `kotlin-dsl-precompiled-script-plugins`
}

dependencies {
    implementation(libs.shadow)
    implementation(libs.kotlin.gradle.plugin)

//    implementation(libs.kotest.junit.runner)
//    implementation(libs.kotest.engine)
//    implementation(libs.kotest.assertion)
//    implementation(libs.kotest.property)

    // hack to access version catalogue https://github.com/gradle/gradle/issues/15383
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
}