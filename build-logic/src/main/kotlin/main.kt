import com.android.build.api.dsl.CommonExtension
import com.gradleup.librarian.gradle.Librarian
import com.gradleup.librarian.gradle.configureAndroidCompatibility
import org.gradle.api.Project
import org.gradle.api.attributes.Attribute
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask
import tapmoc.configureJavaCompatibility
import tapmoc.configureKotlinCompatibility

private fun Project.configureAndroid(namespace: String) {
    configureAndroidCompatibility(23, 35, 35)

    extensions.getByType(CommonExtension::class.java).apply {
        this.namespace = namespace
    }
}

private fun Project.configureKotlin(composeMetrics: Boolean) {
    tasks.withType(KotlinCompilationTask::class.java) {
        val freeCompilerArgs = it.compilerOptions.freeCompilerArgs
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
    (extensions.getByName("kotlin") as KotlinMultiplatformExtension).apply {
        applyDefaultHierarchyTemplate()
        androidTarget {
            publishLibraryVariants("release")
        }
        iosX64()
        iosArm64()
        iosSimulatorArm64()
    }
}

fun Project.library(
    namespace: String,
    compose: Boolean = false,
    kotlin: (KotlinMultiplatformExtension) -> Unit
) {
    val kotlinMultiplatformExtension = applyKotlinMultiplatformPlugin()
    if (compose) {
        applyJetbrainsComposePlugin()
    }
    configureAndroid(namespace = namespace)
    configureKMP()

    configureKotlin(compose)

    kotlin(kotlinMultiplatformExtension)

    Librarian.module(project)
}

fun Project.androidApp(
    namespace: String,
) {
    configureJavaCompatibility(17)
    configureKotlinCompatibility("2.0.0")
    configureAndroid(namespace = namespace)
    configureKotlin(composeMetrics = true)
}
