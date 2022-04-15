plugins {
    id("org.jetbrains.kotlin.jvm").version("1.6.10")
    id("java-gradle-plugin")
}

repositories {
    mavenCentral()
    google()
}

dependencies {
    implementation("net.mbonnin.vespene:vespene-lib:0.5")
    implementation("com.android.tools.build:gradle:7.1.3")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.10")
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
