buildscript {
    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
    }
    dependencies {
        //noinspection UseTomlInstead
        classpath("build-logic:build-logic")
    }
}

version = "0.1.2"
allprojects {
    repositories {
        mavenCentral()
        google()
    }
}

configureRoot()