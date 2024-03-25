import com.android.build.api.dsl.CommonExtension
import com.android.build.gradle.internal.tasks.factory.dependsOn
import dev.icerock.gradle.MultiplatformResourcesPluginExtension
import internal.configurePublishingInternal
import internal.publishIfNeededTaskProvider
import internal.registerReleaseTask
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.logging.LogLevel
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
        it.kotlinOptions {
            if (this is KotlinJvmOptions) {
                jvmTarget = JavaVersion.VERSION_17.toString()
            }
        }
        it.kotlinOptions.freeCompilerArgs += "-Xexpect-actual-classes"
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
        resourcesPackage.set(namespace)
    }
}

fun Project.library(
    namespace: String,
    moko: Boolean = false,
    compose: Boolean = false,
    publish: Boolean = false,
    kotlin: (KotlinMultiplatformExtension) -> Unit
) {
    val kotlinMultiplatformExtension = applyKotlinMultiplatformPlugin()
    if (compose) {
        applyJetbrainsComposePlugin()
    }
    if (publish) {
        configurePublishingInternal(kotlinMultiplatformExtension.androidTarget())
    }
    configureAndroid(namespace = namespace)
    configureKMP()
    configureKotlin()

    kotlin(kotlinMultiplatformExtension)

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


fun Project.configureRoot() {
    check(this == rootProject) {
        "configureRoot must be called from the root project"
    }

    val publishIfNeeded = project.publishIfNeededTaskProvider()
    val ossStagingReleaseTask = project.registerReleaseTask("ossStagingRelease")

    val eventName = System.getenv(EnvVarKeys.GitHub.event)
    val ref = System.getenv(EnvVarKeys.GitHub.ref)

    if (eventName == "push" && ref == "refs/heads/main" && project.version.toString().endsWith("SNAPSHOT")) {
        project.logger.log(LogLevel.LIFECYCLE, "Deploying snapshot to OssSnapshot...")
        publishIfNeeded.dependsOn(project.tasks.named("publishAllPublicationsToOssSnapshotsRepository"))
    }

    if (ref?.startsWith("refs/tags/") == true) {
        project.logger.log(LogLevel.LIFECYCLE, "Deploying release to OssStaging...")
        publishIfNeeded.dependsOn(ossStagingReleaseTask)
    }
}
