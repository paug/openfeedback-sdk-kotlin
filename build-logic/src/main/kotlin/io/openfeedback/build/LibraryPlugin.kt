package io.openfeedback.build

import com.android.build.gradle.internal.tasks.factory.dependsOn
import net.mbonnin.vespene.lib.NexusStagingClient
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.logging.LogLevel
import org.gradle.api.plugins.ExtraPropertiesExtension
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.tasks.javadoc.Javadoc
import org.gradle.external.javadoc.StandardJavadocDocletOptions
import org.gradle.jvm.tasks.Jar

class LibraryPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.apply(mapOf("plugin" to "com.android.library"))
        target.apply(mapOf("plugin" to "org.jetbrains.kotlin.android"))
        target.apply(mapOf("plugin" to "maven-publish"))

        target.extensions.create("openfeedback", OpenFeedback::class.java, target)
    }
}

open class OpenFeedback(val project: Project) {
    fun Project.configurePublishing(artifactName: String) {
        project.configurePublishingInternal(artifactName)

        val publishIfNeeded = project.rootProject.publishIfNeededTaskProvider()

        val eventName = System.getenv("GITHUB_EVENT_NAME")
        val ref = System.getenv("GITHUB_REF")
        project.logger.log(LogLevel.LIFECYCLE, "publishIfNeeded eventName=$eventName ref=$ref")

        if (eventName == "push" && ref == "refs/heads/master") {
            project.logger.log(LogLevel.LIFECYCLE, "Deploying snapshot to OJO...")
            publishIfNeeded.dependsOn(project.tasks.named("publishAllPublicationsToOssSnapshotRepository"))
        }

        if (ref?.startsWith("refs/tags/") == true) {
            project.logger.log(LogLevel.LIFECYCLE, "Deploying release to Bintray...")
            publishIfNeeded.dependsOn("publishAllPublicationsToOssStagingRepository")
        }
    }
}

fun Project.publishIfNeededTaskProvider(): TaskProvider<Task> {
    return try {
        tasks.named("publishIfNeeded")
    } catch (e: Exception) {
        tasks.register("publishIfNeeded")
    }
}

fun Project.getOssStagingUrl(): String {
    val url = try {
        this.extensions.extraProperties["ossStagingUrl"] as String?
    } catch (e: ExtraPropertiesExtension.UnknownPropertyException) {
        null
    }
    if (url != null) {
        return url
    }
    val baseUrl = "https://s01.oss.sonatype.org/service/local/"
    val client = NexusStagingClient(
        baseUrl = baseUrl,
        username = System.getenv("SONATYPE_NEXUS_USERNAME"),
        password = System.getenv("SONATYPE_NEXUS_PASSWORD"),
    )
    val repositoryId = kotlinx.coroutines.runBlocking {
        client.createRepository(
            profileId = System.getenv("IO_OPENFEEDBACK_PROFILE_ID"),
            description = "io.openfeedback $version"
        )
    }
    return "${baseUrl}staging/deployByRepositoryId/${repositoryId}/".also {
        this.extensions.extraProperties["ossStagingUrl"] = it
    }
}

private fun Project.configurePublishingInternal(artifactName: String) {
    val publicationName = "default"
    val android = extensions.findByType(com.android.build.gradle.BaseExtension::class.java)

    /**
     * Javadoc
     */
    var javadocTask = tasks.findByName("javadoc") as Javadoc?
    var javadocJarTaskProvider: TaskProvider<Jar>? = null
    if (javadocTask == null && android != null) {
        javadocTask = tasks.create("javadoc", Javadoc::class.java) {
            // source = android.sourceSets.get("main").java.sourceFiles
            //classpath += project.files(android.joinToString(File.pathSeparator))
        }
    }

    if (javadocTask != null) {
        javadocJarTaskProvider = tasks.register("javadocJar", org.gradle.jvm.tasks.Jar::class.java) {
            it.apply {
                archiveClassifier.set("javadoc")
                dependsOn(javadocTask)
                from(javadocTask.destinationDir)
            }
        }
    }

    var sourcesJarTaskProvider: TaskProvider<org.gradle.jvm.tasks.Jar>? = null
    val javaPluginConvention = project.convention.findPlugin(JavaPluginConvention::class.java)
    if (javaPluginConvention != null && android == null) {
        sourcesJarTaskProvider = tasks.register("sourcesJar", org.gradle.jvm.tasks.Jar::class.java) {
            it.apply {
                archiveClassifier.set("sources")
                from(javaPluginConvention.sourceSets.getByName("main").allSource)
            }
        }
    } else if (android != null) {
        sourcesJarTaskProvider = tasks.register("sourcesJar", org.gradle.jvm.tasks.Jar::class.java) {
            it.apply {
                archiveClassifier.set("sources")
                // from(android.sourceSets["main"].java.sourceFiles)
            }
        }
    }

    tasks.withType(Javadoc::class.java) {
        // TODO: fix the javadoc warnings
        (it.options as StandardJavadocDocletOptions).addStringOption("Xdoclint:none", "-quiet")
    }

    extensions.getByType(PublishingExtension::class.java).apply {
        publications {
            it.create(publicationName, MavenPublication::class.java) {
                it.apply {
                    from(components.findByName("release"))

                    if (javadocJarTaskProvider != null) {
                        artifact(javadocJarTaskProvider.get())
                    }
                    if (sourcesJarTaskProvider != null) {
                        artifact(sourcesJarTaskProvider.get())
                    }

                    pom { pom ->
                        pom.apply {
                            groupId = "io.openfeedback"
                            artifactId = artifactName
                            version = "0.0.6-SNAPSHOT"

                            name.set(artifactId)
                            packaging = "aar"
                            description.set(artifactId)
                            url.set("https://github.com/martinbonnin/openfeedback-android-sdk")

                            scm { scm ->
                                scm.url.set("https://github.com/martinbonnin/openfeedback-android-sdk")
                                scm.connection.set("https://github.com/martinbonnin/openfeedback-android-sdk")
                                scm.developerConnection.set("https://github.com/martinbonnin/openfeedback-android-sdk")
                            }

                            licenses { licenseSpec ->
                                licenseSpec.license { license ->
                                    license.name.set("MIT License")
                                    license.url.set("https://github.com/martinbonnin/openfeedback-android-sdk/blob/master/LICENSE")
                                }
                            }

                            developers { developerSpec ->
                                developerSpec.developer { developer ->
                                    developer.id.set("openfeedback team")
                                    developer.name.set("openfeedback team")
                                }
                            }
                        }
                    }
                }
            }
        }

        repositories { repositoryHandler ->
            repositoryHandler.maven { repository ->
                repository.name = "ossSnapshots"
                repository.url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
                repository.credentials { credentials ->
                    credentials.username = System.getenv("SONATYPE_NEXUS_USERNAME")
                    credentials.password = System.getenv("SONATYPE_NEXUS_PASSWORD")
                }
            }

            repositoryHandler.maven { repository ->
                repository.name = "ossStaging"
                repository.setUrl {
                    uri(rootProject.getOssStagingUrl())
                }
                repository.credentials { credentials ->
                    credentials.username = System.getenv("SONATYPE_NEXUS_USERNAME")
                    credentials.password = System.getenv("SONATYPE_NEXUS_PASSWORD")
                }
            }
        }
    }
}