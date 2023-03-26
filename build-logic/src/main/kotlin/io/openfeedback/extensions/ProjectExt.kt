package io.openfeedback.extensions

import io.openfeedback.EnvVarKeys
import net.mbonnin.vespene.lib.NexusStagingClient
import org.gradle.api.Project
import org.gradle.api.plugins.ExtraPropertiesExtension

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
