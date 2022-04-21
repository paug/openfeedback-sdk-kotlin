plugins {
    `embedded-kotlin`
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
        create("io.openfeedback.build.lib") {
            id = "io.openfeedback.build.lib"
            displayName = ""
            description = ""
            implementationClass = "io.openfeedback.build.LibraryPlugin"
        }
    }
}

gradlePlugin {
    plugins {
        create("io.openfeedback.build.app") {
            id = "io.openfeedback.build.app"
            displayName = ""
            description = ""
            implementationClass = "io.openfeedback.build.AppPlugin"
        }
    }
}
