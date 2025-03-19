plugins {
    application
}

dependencies {
    implementation(project(":subproject:application"))
    implementation(project(":subproject:domain"))
    implementation(project(":subproject:infrastructure"))
    implementation(project(":subproject:presentation"))
}

application {
    mainClass.set("io.vending.machine.MainKt")
}
