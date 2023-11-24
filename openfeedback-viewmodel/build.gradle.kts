plugins {
    id("io.openfeedback.plugins.lib")
    id("io.openfeedback.plugins.publishing")
    alias(libs.plugins.jetbrains.compose)
}

android {
    namespace = "io.openfeedback.android.viewmodel"
}

openfeedback {
    configurePublishing("feedback-android-sdk-viewmodel")
}

dependencies {
    implementation(projects.openfeedback)
    implementation(compose.runtime)
    api(libs.moko.mvvm.core)
    api(libs.kmm.locale)
}