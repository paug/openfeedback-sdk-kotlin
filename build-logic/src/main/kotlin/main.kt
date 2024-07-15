import com.android.build.api.dsl.CommonExtension
import com.gradleup.librarian.gradle.*
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinCompile
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

private fun Project.configureAndroid(namespace: String) {
    configureAndroidCompatibility(23, 34, 34)

    configureJavaCompatibility(17)
    //configureKotlinCompatibility(librarianProperties().kotlinCompatibility() ?: error("no kotlin compatibility found"))
    configureKotlinCompatibility("2.0.0")

    extensions.getByType(CommonExtension::class.java).apply {
        this.namespace = namespace
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
