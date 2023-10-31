
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
    api(libs.kotlin.coroutines.core)
    api(libs.kotlin.coroutines.android)
    api(libs.kotlin.coroutines.play.services)

    // Firestore
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.auth)
}
