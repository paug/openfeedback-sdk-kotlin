import kotlinx.validation.ApiValidationExtension
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.plugins.signing.SigningExtension
import org.jetbrains.compose.ComposeExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

fun Project.applyPublishingPlugin(): PublishingExtension {
    pluginManager.apply("maven-publish")
    return extension()
}

fun Project.applySigningPlugin(): SigningExtension {
    pluginManager.apply("signing")
    return extension()
}

fun Project.applyKotlinMultiplatformPlugin(): KotlinMultiplatformExtension {
    pluginManager.apply("org.jetbrains.kotlin.multiplatform")
    return extension()
}

fun Project.applyJetbrainsComposePlugin(): ComposeExtension {
    pluginManager.apply("org.jetbrains.compose")
    pluginManager.apply("org.jetbrains.kotlin.plugin.compose")
    return extension()
}

fun Project.applyBinaryCompatibilityValidation(): ApiValidationExtension {
    pluginManager.apply("org.jetbrains.kotlinx.binary-compatibility-validator")
    return extension()
}
