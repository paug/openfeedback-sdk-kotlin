
plugins {
    id("io.openfeedback.plugins.compose.lib")
    id("io.openfeedback.plugins.publishing")
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
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.uitooling)
}
