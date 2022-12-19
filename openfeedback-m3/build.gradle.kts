
plugins {
    id("io.openfeedback.plugins.compose.lib")
}

openfeedback {
    configurePublishing("feedback-android-sdk-m3")
}

dependencies {
    api(projects.openfeedback)
    api(projects.openfeedbackViewmodel)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.uitooling)
}