dependencies {
    implementation(project(":subproject:domain"))
    implementation(project(":subproject:infrastructure"))

    testImplementation(testFixtures(project(":subproject:domain")))
}
