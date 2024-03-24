plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.compose")
}

androidApp("io.openfeedback.android.sample")

android {
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
