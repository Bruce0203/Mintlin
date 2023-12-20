package buildsrc.plugins

plugins {
    id("buildsrc.plugins.kmp")
    application
    id("com.github.johnrengelman.shadow")
}

tasks {
    shadowJar {
        configurations.add(project.configurations.named("jvmRuntimeClasspath").get())
        from(named<Jar>("jvmJar"))
        archiveFileName.set("${project.name}.jar")
    }
}
