import com.android.build.api.dsl.CommonExtension
import dev.icerock.gradle.MultiplatformResourcesPluginExtension
import io.openfeedback.OpenFeedback
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinCompile
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

private fun Project.configureAndroid(namespace: String) {
    extensions.getByName("android").apply {
        this as CommonExtension<*,*,*,*,*>
        compileSdk = 34
        this.namespace = namespace

        defaultConfig {
            minSdk = 23
        }

        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_17
            targetCompatibility = JavaVersion.VERSION_17
        }
    }
}

private fun Project.configureKotlin() {
    tasks.withType(KotlinCompile::class.java) {
        kotlinOptions {
            if (this is KotlinJvmOptions) {
                jvmTarget = JavaVersion.VERSION_17.toString()
            }
        }
        this.kotlinOptions.freeCompilerArgs += "-Xexpect-actual-classes"
    }
}

private fun Project.configureKMP() {
    (extensions.getByName("kotlin") as KotlinMultiplatformExtension).apply {
        applyDefaultHierarchyTemplate()
        androidTarget()
        iosX64()
        iosArm64()
        iosSimulatorArm64()
    }
}

fun Project.configureMoko(namespace: String) {
    pluginManager.apply("dev.icerock.mobile.multiplatform-resources")
    extensions.getByType(MultiplatformResourcesPluginExtension::class.java).apply {
        multiplatformResourcesPackage = namespace
        disableStaticFrameworkWarning = true
    }
}

fun Project.library(
    namespace: String,
    artifactName: String?,
    moko: Boolean = false
) {
    if (artifactName != null) {
        extensions.getByType(OpenFeedback::class.java).apply {
            configurePublishing(artifactName)
        }
    }
    configureAndroid(namespace = namespace)
    configureKMP()
    configureKotlin()

    if (moko) {
        configureMoko(namespace)
    }
}

fun Project.androidApp(
    namespace: String,
) {
    configureAndroid(namespace = namespace)
    configureKotlin()
}