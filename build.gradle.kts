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

version = "0.2.3"
allprojects {
    repositories {
        mavenCentral()
        google()
    }
}

configureRoot()