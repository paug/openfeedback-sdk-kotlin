plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jetbrains.kotlin.plugin.serialization")
}

library(
    namespace = "io.openfeedback",
    artifactName = "feedback-sdk",
    moko = true
)

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(libs.kotlin.coroutines.core)
                api(libs.kotlinx.datetime)
                api(libs.kotlinx.serialization.json)

                api(libs.gitlive.app)
                api(libs.gitlive.firestore)
                implementation(libs.gitlive.auth)
                implementation(libs.gitlive.common)

                api(libs.moko.resources)

                implementation(libs.kermit)
            }
        }
        val androidMain by getting {
            dependencies {
                api(libs.firebase.common)
                api(libs.firebase.firestore)
                implementation(libs.firebase.auth)
            }
        }
    }
}

