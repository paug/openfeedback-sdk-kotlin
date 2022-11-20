
plugins {
    id("io.openfeedback.plugins.compose.lib")
}

openfeedback {
    configurePublishing("feedback-android-sdk-ui")
}

dependencies {
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.uitooling)

    api(project(":openfeedback"))
}
