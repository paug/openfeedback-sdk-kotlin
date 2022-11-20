package io.openfeedback.extensions

import io.openfeedback.EnvVarKeys
import net.mbonnin.vespene.lib.NexusStagingClient
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.plugins.ExtraPropertiesExtension
import org.gradle.api.tasks.TaskProvider
import org.gradle.plugins.signing.Sign

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

fun Project.getOssStagingUrl(): String {
    val url = try {
        this.extensions.extraProperties["ossStagingUrl"] as String?
    } catch (ignored: ExtraPropertiesExtension.UnknownPropertyException) {
        null
    }
    if (url != null) {
        return url
    }
    val baseUrl = "https://s01.oss.sonatype.org/service/local/"
    val client = NexusStagingClient(
        baseUrl = baseUrl,
        username = System.getenv(EnvVarKeys.Nexus.username),
        password = System.getenv(EnvVarKeys.Nexus.password),
    )
    val repositoryId = kotlinx.coroutines.runBlocking {
        client.createRepository(
            profileId = System.getenv(EnvVarKeys.Nexus.profileId),
            description = "io.openfeedback ${rootProject.version}"
        )
    }
    return "${baseUrl}staging/deployByRepositoryId/${repositoryId}/".also {
        this.extensions.extraProperties["ossStagingUrl"] = it
    }
}
