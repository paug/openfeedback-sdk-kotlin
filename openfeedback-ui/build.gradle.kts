
plugins {
    id("io.openfeedback.plugins.compose.lib")
    id("io.openfeedback.publishing")
}

dependencies {
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.foundation)

    api(projects.openfeedback)
    api(projects.openfeedbackM2)
    api(projects.openfeedbackViewmodel)
}
