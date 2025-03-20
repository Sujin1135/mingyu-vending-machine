plugins {
    kotlin("jvm")
    application
}

dependencies {
    implementation(project(":subproject:application"))
    implementation(project(":subproject:domain"))
    implementation(project(":subproject:infrastructure"))
}

application {
    mainClass.set("io.vending.machine.MainKt")
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "io.vending.machine.MainKt"
    }

    // Fat JAR 만들기 - 모든 의존성 포함
    from(sourceSets.main.get().output)
    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath
            .get()
            .filter { it.name.endsWith("jar") }
            .map { zipTree(it) }
    })

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}
