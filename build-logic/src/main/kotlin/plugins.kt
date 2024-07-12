import org.gradle.api.Project
import org.jetbrains.compose.ComposeExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension


fun Project.applyKotlinMultiplatformPlugin(): KotlinMultiplatformExtension {
    pluginManager.apply("org.jetbrains.kotlin.multiplatform")
    return extension()
}

fun Project.applyJetbrainsComposePlugin(): ComposeExtension {
    pluginManager.apply("org.jetbrains.compose")
    pluginManager.apply("org.jetbrains.kotlin.plugin.compose")
    return extension()
}

