
plugins {
    id("io.openfeedback.build.lib")
}

openfeedback {
    configurePublishing("feedback-android-sdk-ui")
}
android {
    compileSdk = 31

    defaultConfig {
        minSdk = 21
        targetSdk = 31
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    composeOptions {
        kotlinCompilerExtensionVersion = rootProject.extra["composeVersion"] as String
    }

    buildFeatures.compose = true
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    val composeVersion = rootProject.extra["composeVersion"]

    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.4.0")
    implementation("androidx.compose.material:material:$composeVersion")
    implementation("androidx.compose.ui:ui:$composeVersion")
    implementation("androidx.compose.ui:ui-tooling:$composeVersion")
    // Weird but necessary for the compose preview.
    debugImplementation("androidx.lifecycle:lifecycle-runtime-ktx:2.4.0")
    debugImplementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.4.0")
    debugImplementation("androidx.savedstate:savedstate-ktx:1.1.0")
    debugImplementation("androidx.core:core-ktx:1.7.0")

    api(project(":openfeedback"))
}
