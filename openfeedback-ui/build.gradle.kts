
plugins {
    id("io.openfeedback.build.lib")
}

openfeedback {
    configurePublishing("feedback-android-sdk-ui")
}
android {
    compileSdk = 33

    defaultConfig {
        minSdk = 21
        targetSdk = 33
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.androidx.compose.compiler.get()
    }

    buildFeatures.compose = true
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.uitooling)
    // Weird but necessary for the compose preview.
    debugImplementation(libs.androidx.lifecycle.runtime)
    debugImplementation(libs.androidx.lifecycle.viewmodel.ktx)
    debugImplementation(libs.androidx.savedstate)
    debugImplementation(libs.androidx.core)

    api(project(":openfeedback"))
}
