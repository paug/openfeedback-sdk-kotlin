package io.openfeedback.plugins

import com.android.build.api.dsl.LibraryExtension
import io.openfeedback.extensions.mavenSonatypeSnapshot
import io.openfeedback.extensions.mavenSonatypeStaging
import io.openfeedback.extensions.pom
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.findByType
import org.gradle.kotlin.dsl.get
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class PublishingPlugin: Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("org.gradle.maven-publish")
            }
        }
        val groupId = System.getenv("GROUP_ID")
            ?: target.properties["GROUP_ID"] as String?
            ?: "io.openfeedback"
        val artifactId = target.name
        val version = target.properties["VERSION_CODE"] as String?
            ?: target.version as String?
            ?: throw IllegalStateException("Please, provide me a version to publish!")
        if (target.extensions.findByType<KotlinMultiplatformExtension>() != null) {
            target.afterEvaluate {
                this.group = groupId
                this.version = version
                extensions.configure<KotlinMultiplatformExtension>("kotlin") {
                    extensions.configure<PublishingExtension>("publishing") {
                        this.publications {
                            create<MavenPublication>("maven") {
                                this.groupId = groupId
                                this.artifactId = artifactId
                                this.version = version
                                this.pom(name = artifactId, description = artifactId)
                                from(target.components["release"])
                            }
                        }
                        repositories {
                            if (version.endsWith("-SNAPSHOT")) {
                                mavenSonatypeSnapshot(target)
                            } else {
                                mavenSonatypeStaging(target)
                            }
                        }
                    }
                }
            }
        } else if (target.extensions.findByType<LibraryExtension>() != null) {
            target.extensions.configure<LibraryExtension> {
                publishing {
                    singleVariant("release") {
                        withSourcesJar()
                        withJavadocJar()
                    }
                }
            }
            target.afterEvaluate {
                target.extensions.configure<PublishingExtension>("publishing") {
                    repositories {
                        if (version.endsWith("-SNAPSHOT")) {
                            mavenSonatypeSnapshot(target)
                        } else {
                            mavenSonatypeStaging(target)
                        }
                    }
                    publications {
                        create<MavenPublication>("maven") {
                            this.groupId = groupId
                            this.artifactId = artifactId
                            this.version = version
                            this.pom(name = artifactId, description = artifactId)
                            from(target.components["release"])
                        }
                    }
                }
            }
        }
    }
}
