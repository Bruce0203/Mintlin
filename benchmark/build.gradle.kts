import org.jetbrains.kotlin.allopen.gradle.AllOpenExtension

apply(from = "../gradle/repositories.settings.gradle")
plugins {
    kotlin("jvm")
    java
    application
    alias(libs.plugins.kotlin.allopen)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.jmh)
    `java-library`
}

configure<AllOpenExtension> {
    annotation("org.openjdk.jmh.annotations.State")
}

dependencies {
    api(libs.kotlinx.benchmark.runtime)
    jmhApi(project(":foundation"))
}

jmh {
    threads.set(1)
    warmup.set("1s")
    timeUnit.set("ns")
    timeOnIteration.set("2s")
    warmupIterations.set(5)
    iterations.set(3)
    fork.set(1)
    jvmArgsAppend.add("-Xms4G -Xmx4G")
}
