plugins {
    kotlin("jvm") version "2.1.10"
}

group = "io.vending.machine"
version = "1.0-SNAPSHOT"

allprojects {
    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "kotlin")

    dependencies {
        implementation("io.insert-koin:koin-core:4.0.2")

        testImplementation(kotlin("test"))
    }
}

kotlin {
    jvmToolchain(21)
}

tasks.test {
    useJUnitPlatform()
}

tasks.getByName<Jar>("jar") {
    enabled = true
}
