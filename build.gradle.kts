plugins {
    alias(libs.plugins.kotlin.jvm)
    `java-test-fixtures`
}

group = "io.vending.machine"
version = "1.0-SNAPSHOT"

allprojects {
    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(
        plugin =
            rootProject.libs.plugins.kotlin.jvm
                .get()
                .pluginId,
    )
    apply(plugin = "java-test-fixtures")

    dependencies {
        implementation(rootProject.libs.koin)
        implementation(rootProject.libs.bundles.language)
        implementation(rootProject.libs.bundles.arrow.kt)

        testImplementation(rootProject.libs.bundles.test)
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
