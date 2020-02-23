buildscript {
    val kotlinVersion = "1.3.61"
    project.extra.set("kotlinVersion", kotlinVersion)
    project.extra.set("composeVersion", "0.1.0-dev05")

    repositories {
        google()
        jcenter()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:4.0.0-alpha09")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${kotlinVersion}")
    }
}

subprojects {
    repositories {
        google()
        jcenter()
    }
}
