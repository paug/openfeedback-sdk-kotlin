import com.gradleup.librarian.gradle.librarianRoot

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
                if (it is ModuleComponentSelector && it.module == "bcpkix-jdk15on") {
                    useTarget("${it.group}:bcpkix-jdk18on:1.77")
                }
            }
        }
    }
}

apply(plugin = "com.github.ben-manes.versions")
apply(plugin = "nl.littlerobots.version-catalog-update")
