
plugins {
    id("io.openfeedback.plugins.compose.lib")
    id("io.openfeedback.plugins.publishing")
}

android {
    namespace = "io.openfeedback.android.viewmodel"
}

openfeedback {
    configurePublishing("feedback-android-sdk-viewmodel")
}

dependencies {
    implementation(projects.openfeedback)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.runtime)
}