import com.android.build.api.dsl.CommonExtension
import com.gradleup.librarian.gradle.librarianModule
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinCompile
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

private fun Project.configureAndroid(namespace: String) {
    extensions.getByName("android").apply {
        this as CommonExtension<*,*,*,*,*, *>
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

fun Project.library(
    namespace: String,
    compose: Boolean = false,
    publish: Boolean = false,
    kotlin: (KotlinMultiplatformExtension) -> Unit
) {
    val kotlinMultiplatformExtension = applyKotlinMultiplatformPlugin()
    if (compose) {
        applyJetbrainsComposePlugin()
    }
    configureAndroid(namespace = namespace)
    configureKMP()

    configureKotlin()

    kotlin(kotlinMultiplatformExtension)

    librarianModule(publish)
}


fun Project.androidApp(
    namespace: String,
) {
    configureAndroid(namespace = namespace)
    configureKotlin()
}
