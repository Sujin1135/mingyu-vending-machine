plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
rootProject.name = "mingyu-vending-machine"

include(
    ":subproject:boot",
    ":subproject:application",
    ":subproject:domain",
    ":subproject:infrastructure",
    ":subproject:presentation",
)
