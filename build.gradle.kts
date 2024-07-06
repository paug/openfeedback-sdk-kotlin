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
    configurations.all {
        resolutionStrategy.dependencySubstitution.all {
            requested.let {
                if (it is ModuleComponentSelector && it.module == "bcprov-jdk15on") {
                    useTarget("${it.group}:bcprov-jdk18on:1.77")
                }
                if (it is ModuleComponentSelector && it.module == "bcpg-jdk15on") {
                    useTarget("${it.group}:bcpg-jdk18on:1.77")
                }
            }
        }
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