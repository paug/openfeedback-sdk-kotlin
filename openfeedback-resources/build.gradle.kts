plugins {
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.kotlin.serialization)

    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
}

library(
    namespace = "io.openfeedback.resources",
    compose = true,
    enableAndroidResources = true,
) { kotlinMultiplatformExtension ->
    kotlinMultiplatformExtension.sourceSets {
        getByName("commonMain") {
            dependencies {
                implementation(libs.compose.ui)
                api(libs.compose.components.resources)

                api(libs.lyricist)
            }
        }
    }
}

compose.resources {
    publicResClass = true
    packageOfResClass = "io.openfeedback.resources"
    generateResClass = always
}
