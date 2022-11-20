plugins {
    id("io.openfeedback.build.app")
}

android {
    compileSdk = 33

    defaultConfig {
        minSdk = 21
        targetSdk = 33
        versionCode = 1
        versionName = "1"
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
    implementation(libs.androidx.core)
    implementation("androidx.appcompat:appcompat:1.4.1")
    implementation("androidx.activity:activity-compose:1.5.0-alpha01")

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.material)

    implementation(project(":openfeedback-ui"))
}
