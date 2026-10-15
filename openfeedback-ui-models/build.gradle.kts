plugins {
    alias(libs.plugins.android.kotlin.multiplatform.library)
}

library(
    namespace = "io.openfeedback.ui.models",
    compose = true,
) { kotlinMultiplatformExtension ->
    kotlinMultiplatformExtension.sourceSets {
        getByName("commonMain") {
            dependencies {
                implementation(libs.compose.runtime)
                api(libs.vanniktech.multiplatform.locale)
                api(libs.jetbrains.kotlinx.collections.immutable)
            }
        }
    }
}