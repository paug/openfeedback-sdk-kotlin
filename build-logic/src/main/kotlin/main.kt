import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryTarget
import com.gradleup.librarian.gradle.Librarian
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.NativeOutputKind
import org.jetbrains.kotlin.gradle.targets.native.tasks.KotlinNativeTest
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask
import org.jetbrains.kotlin.gradle.tasks.KotlinNativeLink
import tapmoc.configureJavaCompatibility
import tapmoc.configureKotlinCompatibility

private fun Project.configureAndroidLibrary(namespace: String, enableAndroidResources: Boolean) {
    extensions.getByType(KotlinMultiplatformExtension::class.java).apply {
        targets.withType(KotlinMultiplatformAndroidLibraryTarget::class.java).configureEach {
            it.namespace = namespace
            it.compileSdk = 35
            it.minSdk = 23
            it.androidResources.enable = enableAndroidResources
            it.withHostTest {
                isIncludeAndroidResources = enableAndroidResources
            }
        }
    }
}

private fun Project.configureAndroidApplication(namespace: String) {
    extensions.getByType(ApplicationExtension::class.java).apply {
        this.namespace = namespace
        compileSdk = 35
        defaultConfig {
            targetSdk = 35
            minSdk = 23
        }
    }
}

private fun Project.configureKotlin(composeMetrics: Boolean) {
    tasks.withType(KotlinCompilationTask::class.java).configureEach {
        val freeCompilerArgs = it.compilerOptions.freeCompilerArgs
        freeCompilerArgs.add("-Xexpect-actual-classes")
        it.compilerOptions.optIn.add("kotlin.time.ExperimentalTime")
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
    extensions.getByType(KotlinMultiplatformExtension::class.java).apply {
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

    // Native test binaries can't be linked : the Firebase iOS frameworks are provided by Xcode.
    // ("ld: framework 'FirebaseCore' not found"). Common tests still run on the Android host,
    // which is enough as long as none of them relies on iOS specific code.
    tasks.withType(KotlinNativeTest::class.java).configureEach {
        it.enabled = false
    }
    tasks.withType(KotlinNativeLink::class.java).configureEach {
        if (it.binary.outputKind == NativeOutputKind.TEST) {
            it.enabled = false
        }
    }

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
