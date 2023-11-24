
plugins {
    id("io.openfeedback.plugins.lib")
    id("io.openfeedback.plugins.publishing")
    alias(libs.plugins.jetbrains.compose)
}

android {
    namespace = "io.openfeedback.android.m2"
}

openfeedback {
    configurePublishing("feedback-android-sdk-m2")
}

dependencies {
    api(projects.openfeedback)
    api(projects.openfeedbackViewmodel)

    implementation(libs.moko.mvvm.compose)
    implementation(compose.material)
    implementation(compose.ui)
    implementation(compose.uiTooling)
}
