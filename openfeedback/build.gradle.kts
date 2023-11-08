
plugins {
    id("io.openfeedback.plugins.lib")
}

android {
    namespace = "io.openfeedback.android"
}

openfeedback {
    configurePublishing("feedback-android-sdk")
}

dependencies {
    api(libs.kotlinx.serialization.json)
    api(libs.kotlin.coroutines.core)
    api(libs.kotlin.coroutines.android)
    api(libs.kotlin.coroutines.play.services)

    // KMM Firestore
    api(libs.gitlive.firestore)
    api(libs.gitlive.auth)
    implementation(libs.gitlive.common)
}
