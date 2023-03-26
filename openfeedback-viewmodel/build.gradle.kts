
plugins {
    id("io.openfeedback.plugins.compose.lib")
    id("io.openfeedback.publishing")
}

dependencies {
    implementation(projects.openfeedback)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.runtime)
}