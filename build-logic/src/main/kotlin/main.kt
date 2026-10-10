import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryTarget
import com.gradleup.librarian.gradle.Librarian
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask
import tapmoc.configureJavaCompatibility
import tapmoc.configureKotlinCompatibility

private fun Project.configureAndroidLibrary(namespace: String, enableAndroidResources: Boolean) {
    extensions.configure<KotlinMultiplatformExtension> {
        targets.withType(KotlinMultiplatformAndroidLibraryTarget::class.java).configureEach {
            this.namespace = namespace
            compileSdk = 35
            minSdk = 23
            androidResources.enable = enableAndroidResources
        }
    }
}

private fun Project.configureAndroidApplication(namespace: String) {
    extensions.configure(ApplicationExtension::class.java) {
        this.namespace = namespace
        defaultConfig {
            targetSdk = 35
            compileSdk = 35
            minSdk = 23
        }
    }
}

private fun Project.configureKotlin(composeMetrics: Boolean) {
    tasks.withType(KotlinCompilationTask::class.java) {
        val freeCompilerArgs = compilerOptions.freeCompilerArgs
        freeCompilerArgs.add("-Xexpect-actual-classes")
        if (composeMetrics) {
            if (project.findProperty("composeCompilerReports") == "true") {
                freeCompilerArgs.add("-P")
                freeCompilerArgs.add("plugin:androidx.compose.compiler.plugins.kotlin:reportsDestination=${project.layout.buildDirectory.asFile.get().absolutePath}/compose_compiler")
            }
            if (project.findProperty("composeCompilerMetrics") == "true") {
                freeCompilerArgs.add("-P")
                freeCompilerArgs.add("plugin:androidx.compose.compiler.plugins.kotlin:metricsDestination=${project.layout.buildDirectory.asFile.get().absolutePath}/compose_compiler")
            }
        }
    }
}

private fun Project.configureKMP() {
    extensions.configure<KotlinMultiplatformExtension> {
        applyDefaultHierarchyTemplate()
        iosX64()
        iosArm64()
        iosSimulatorArm64()
    }
}

fun Project.library(
    namespace: String,
    compose: Boolean = false,
    enableAndroidResources: Boolean = false,
    kotlin: (KotlinMultiplatformExtension) -> Unit
) {
    val kotlinMultiplatformExtension = applyKotlinMultiplatformPlugin()
    if (compose) {
        applyJetbrainsComposePlugin()
    }
    configureAndroidLibrary(namespace = namespace, enableAndroidResources = enableAndroidResources)
    configureKMP()

    configureKotlin(composeMetrics = compose)

    kotlin(kotlinMultiplatformExtension)

    Librarian.module(project)
}

fun Project.androidApp(
    namespace: String,
) {
    configureJavaCompatibility(17)
    configureKotlinCompatibility("2.2.0")
    configureAndroidApplication(namespace = namespace)
    configureKotlin(composeMetrics = true)
}
