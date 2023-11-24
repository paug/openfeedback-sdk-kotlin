
plugins {
    id("io.openfeedback.plugins.lib")
    id("io.openfeedback.plugins.publishing")
    alias(libs.plugins.jetbrains.compose)
}

android {
    namespace = "io.openfeedback.android.m3"
}

openfeedback {
    configurePublishing("feedback-android-sdk-m3")
}

dependencies {
    api(projects.openfeedback)
    api(projects.openfeedbackViewmodel)

    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(compose.material3)
    implementation(compose.ui)
    implementation(compose.uiTooling)
}
