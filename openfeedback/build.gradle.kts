plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.plugin.serialization")
}

library(
    namespace = "io.openfeedback",
    publish = true
) {
    it.sourceSets {
        getByName("commonMain").apply {
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
        getByName("androidMain"){
            dependencies {
                api(libs.google.firebase.common)
                api(libs.google.firebase.firestore)
                implementation(libs.google.firebase.auth)
            }
        }
    }
}
