plugins {
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.kotlin.serialization)
}

library(
    namespace = "io.openfeedback",
) { kotlinMultiplatformExtension ->
    kotlinMultiplatformExtension.sourceSets {
        getByName("commonMain") {
            dependencies {
                api(libs.jetbrains.kotlinx.coroutines)
                api(libs.jetbrains.kotlinx.datetime)
                api(libs.jetbrains.kotlinx.serialization.json)

                api(libs.gitlive.firebase.app)
                api(libs.gitlive.firebase.firestore)
                implementation(libs.gitlive.firebase.auth)
                implementation(libs.gitlive.firebase.common)

                implementation(libs.touchlab.kermit)
            }
        }
        getByName("androidMain") {
            dependencies {
                api(libs.google.firebase.common)
                api(libs.google.firebase.firestore)
                implementation(libs.google.firebase.auth)
            }
        }
    }
}
