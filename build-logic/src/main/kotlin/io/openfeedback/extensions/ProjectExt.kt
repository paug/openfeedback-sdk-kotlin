package io.openfeedback.extensions

import com.android.build.gradle.internal.tasks.factory.dependsOn
import io.openfeedback.EnvVarKeys
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import net.mbonnin.vespene.lib.NexusStagingClient
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.UnknownDomainObjectException
import org.gradle.api.logging.LogLevel
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.TaskProvider
import org.gradle.plugins.signing.Sign
import kotlin.time.Duration.Companion.minutes
import kotlin.time.ExperimentalTime

internal fun Project.configurePublishingInternal(artifactName: String) {
    println("configurePublishingInternal for $name")
    val android = extensions.findByType(com.android.build.gradle.LibraryExtension::class.java)!!

    android.publishing {
        singleVariant("release") {
            withJavadocJar()
            withSourcesJar()
        }
    }

    extensions.configurePublishing(
        project = this@configurePublishingInternal,
        artifactName = artifactName
    )

    extensions.configureSigning()

    tasks.withType(Sign::class.java).configureEach {
        isEnabled = !System.getenv(EnvVarKeys.GPG.privateKey).isNullOrBlank()
    }

    rootProject.tasks.named("ossStagingRelease").configure {
        dependsOn(this@configurePublishingInternal.tasks.named("publishAllPublicationsToOssStagingRepository"))
    }
}

private fun Project.getOrCreateRepoIdTask(): TaskProvider<Task> {
    return try {
        rootProject.tasks.named("createStagingRepo")
    } catch (e: UnknownDomainObjectException) {
        rootProject.tasks.register("createStagingRepo") {
            outputs.file(rootProject.layout.buildDirectory.file("stagingRepoId"))

            doLast {
                val repoId = runBlocking {
                    nexusStagingClient.createRepository(
                        profileId = System.getenv(EnvVarKeys.Nexus.profileId),
                        description = "io.openfeedback ${rootProject.version}"
                    )
                }
                logger.log(LogLevel.LIFECYCLE, "repo created: $repoId")
                this.outputs.files.singleFile.writeText(repoId)
            }
        }
    }
}

fun Project.publishIfNeededTaskProvider(): TaskProvider<Task> {
    return try {
        tasks.named("publishIfNeeded")
    } catch (ignored: Exception) {
        tasks.register("publishIfNeeded")
    }
}

private val baseUrl = "https://s01.oss.sonatype.org/service/local/"

private val nexusStagingClient by lazy {
    NexusStagingClient(
        baseUrl = baseUrl,
        username = System.getenv(EnvVarKeys.Nexus.username)
            ?: error("please set the ${EnvVarKeys.Nexus.username} environment variable"),
        password = System.getenv(EnvVarKeys.Nexus.password)
            ?: error("please set the ${EnvVarKeys.Nexus.password} environment variable"),
    )
}

fun Project.getOrCreateOssStagingUrl(): Provider<String> {
    return getOrCreateRepoIdTask().map {
        val repoId = it.outputs.files.singleFile.readText()
        "${baseUrl}staging/deployByRepositoryId/$repoId/"
    }
}

@OptIn(ExperimentalTime::class)
fun Task.closeAndReleaseStagingRepository(repoId: String) {
    runBlocking {
        logger.log(LogLevel.LIFECYCLE, "Closing repository $repoId")
        nexusStagingClient.closeRepositories(listOf(repoId))
        withTimeout(5.minutes) {
            nexusStagingClient.waitForClose(repoId, 1000) {
                logger.log(LogLevel.LIFECYCLE, ".")
            }
        }
        nexusStagingClient.releaseRepositories(listOf(repoId), true)
    }
}

private fun Project.registerReleaseTask(name: String): TaskProvider<Task> {
    val task = try {
        rootProject.tasks.named(name)
    } catch (e: UnknownDomainObjectException) {
        val repoId = getOrCreateOssStagingUrl()
        rootProject.tasks.register(name) {
            inputs.property(
                "repoId",
                repoId
            )
            doLast {
                println("Closing repo: ${inputs.properties.get("repoId") as String}")
                //closeAndReleaseStagingRepository(inputs.properties.get("repoId") as String)
            }
        }
    }

    return task
}

fun Project.configureRoot() {
    check(this == rootProject) {
        "configureRoot must be called from the root project"
    }

    val publishIfNeeded = project.publishIfNeededTaskProvider()
    val ossStagingReleaseTask = project.registerReleaseTask("ossStagingRelease")

    val eventName = System.getenv(EnvVarKeys.GitHub.event)
    val ref = System.getenv(EnvVarKeys.GitHub.ref)

    if (eventName == "push" && ref == "refs/heads/main" && project.version.toString().endsWith("SNAPSHOT")) {
        project.logger.log(LogLevel.LIFECYCLE, "Deploying snapshot to OssSnapshot...")
        publishIfNeeded.dependsOn(project.tasks.named("publishAllPublicationsToOssSnapshotsRepository"))
    }

    if (ref?.startsWith("refs/tags/") == true) {
        project.logger.log(LogLevel.LIFECYCLE, "Deploying release to OssStaging...")
        publishIfNeeded.dependsOn(ossStagingReleaseTask)
    }
}