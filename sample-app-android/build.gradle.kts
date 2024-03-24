plugins {
    id("io.openfeedback.plugins.app")
    alias(libs.plugins.jetbrains.compose)
}

android {
    namespace = "io.openfeedback.android.sample"
    defaultConfig {
        versionCode = 1
        versionName = "1"
    }
}

dependencies {
    implementation(projects.openfeedbackM3)
    implementation(projects.sampleAppShared)

    implementation(libs.androidx.core)
    implementation(libs.appcompat)
    implementation(libs.activity.compose)

    implementation(compose.material3)
}
