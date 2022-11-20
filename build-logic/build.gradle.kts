plugins {
    `embedded-kotlin`
    `kotlin-dsl`
    `java-gradle-plugin`
}

repositories {
    mavenCentral()
    google()
}

dependencies {
    implementation(libs.vespene)
    implementation(libs.android.gradle.plugin)
    implementation(libs.kotlin.gradle.plugin)
}

gradlePlugin {
    plugins {
        register("io.openfeedback.build.lib") {
            id = "io.openfeedback.build.lib"
            implementationClass = "io.openfeedback.build.LibraryPlugin"
        }
        register("io.openfeedback.build.app") {
            id = "io.openfeedback.build.app"
            implementationClass = "io.openfeedback.build.AppPlugin"
        }
    }
}
