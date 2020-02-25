import com.android.build.gradle.BaseExtension

plugins {
    id("com.android.library")
    id("kotlin-android")
}

extensions.findByType(BaseExtension::class.java)!!.apply {
    compileSdkVersion(29)
    defaultConfig {
        minSdkVersion(21)
        targetSdkVersion(29)
        versionCode = 1
        versionName = "1"
    }

    compileOptions {
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    buildFeatures.compose = true
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    val composeVersion = rootProject.extra["composeVersion"]
    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:${rootProject.extra["kotlinVersion"]}")

    implementation("androidx.compose:compose-runtime:$composeVersion")
    implementation("androidx.ui:ui-framework:$composeVersion")
    implementation("androidx.ui:ui-layout:$composeVersion")
    implementation("androidx.ui:ui-material:$composeVersion")
    implementation("androidx.ui:ui-foundation:$composeVersion")
    implementation("androidx.ui:ui-animation:$composeVersion")
    implementation("androidx.ui:ui-tooling:$composeVersion")

    api(project(":openfeedback"))
}

