package io.openfeedback.extensions

import io.openfeedback.EnvVarKeys
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import net.mbonnin.vespene.lib.NexusStagingClient
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.logging.LogLevel
import org.gradle.api.logging.Logger
import org.gradle.api.plugins.ExtraPropertiesExtension
import org.gradle.api.tasks.TaskProvider
import org.gradle.plugins.signing.Sign
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock
import kotlin.reflect.jvm.internal.impl.load.kotlin.JvmType
import kotlin.time.Duration.Companion.minutes
import kotlin.time.ExperimentalTime

internal fun Project.configurePublishingInternal(artifactName: String) {
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
}

fun Project.publishIfNeededTaskProvider(): TaskProvider<Task> {
    return try {
        tasks.named("publishIfNeeded")
    } catch (ignored: Exception) {
        tasks.register("publishIfNeeded")
    }
}

private val baseUrl = "https://s01.oss.sonatype.org/service/local/"
private val lock = ReentrantLock()

private val nexusStagingClient by lazy {
    NexusStagingClient(
        baseUrl = baseUrl,
        username = System.getenv(EnvVarKeys.Nexus.username)
            ?: error("please set the ${EnvVarKeys.Nexus.username} environment variable"),
        password = System.getenv(EnvVarKeys.Nexus.password)
            ?: error("please set the ${EnvVarKeys.Nexus.password} environment variable"),
    )
}


internal fun Project.getOssStagingRepoId(): String? = lock.withLock {
    return try {
        this.extensions.extraProperties["ossStagingRepositoryId"] as String?
    } catch (ignored: ExtraPropertiesExtension.UnknownPropertyException) {
        null
    }
}

private fun Project.getOrCreateOssStagingRepoId(): String = lock.withLock {
    var repoId = getOssStagingRepoId()
    if (repoId != null) {
        return repoId
    }

    repoId = runBlocking {
        nexusStagingClient.createRepository(
            profileId = System.getenv(EnvVarKeys.Nexus.profileId),
            description = "io.openfeedback ${rootProject.version}"
        )
    }
    this.extensions.extraProperties["ossStagingRepositoryId"] = repoId

    return repoId
}


fun Project.getOssStagingUrl(): String {
    return "${baseUrl}staging/deployByRepositoryId/${getOrCreateOssStagingRepoId()}/"
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

fun Project.registerReleaseTask(name: String, configure: Task.() -> Unit): TaskProvider<Task> {
    return project.tasks.register(name) {
        configure()
        dependsOn("publishAllPublicationsToOssStagingRepository")
        inputs.property(
            "repoId",
            getOssStagingRepoId()
                ?: error("You need to publish to OssStaging in the same Gradle invocation before calling closeAndReleaseStagingRepository.")
        )
        doLast {
            closeAndReleaseStagingRepository(inputs.properties.get("repoId") as String)
        }
    }
}