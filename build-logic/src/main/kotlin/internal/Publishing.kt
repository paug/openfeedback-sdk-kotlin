package internal

import EnvVarKeys
import applyPublishingPlugin
import applySigningPlugin
import extension
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import net.mbonnin.vespene.lib.NexusStagingClient
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.UnknownDomainObjectException
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.logging.LogLevel
import org.gradle.api.plugins.ExtensionContainer
import org.gradle.api.provider.Provider
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.TaskProvider
import org.gradle.plugins.signing.Sign
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinAndroidTarget
import kotlin.time.Duration.Companion.minutes

internal fun Project.configurePublications(
    publishing: PublishingExtension,
    artifactName: String
) {
    val project = this

    publishing.publications.configureEach {
        (it as MavenPublication).apply {
            groupId = "io.openfeedback"
            version = project.rootProject.version.toString()
            artifactId = artifactName

            pom {
                it.name.set(artifactName)

                it.description.set(artifactId)
                it.url.set("https://github.com/paug/openfeedback-android-sdk")

                it.scm {
                    it.url.set("https://github.com/paug/openfeedback-android-sdk")
                    it.connection.set("https://github.com/paug/openfeedback-android-sdk")
                    it.developerConnection.set("https://github.com/paug/openfeedback-android-sdk")
                }

                it.licenses {
                    it.license {
                        it.name.set("MIT License")
                        it.url.set("https://github.com/paug/openfeedback-android-sdk/blob/master/LICENSE")
                    }
                }

                it.developers {
                    it.developer {
                        it.id.set("openfeedback team")
                        it.name.set("openfeedback team")
                    }
                }
            }
        }
    }
}


internal fun Project.configurePublishingInternal(
    androidTarget: KotlinAndroidTarget
) {
    val publishing = applyPublishingPlugin()

    /**
     * Signing
     */
    val privateKey = System.getenv(EnvVarKeys.GPG.privateKey)
    val password = System.getenv(EnvVarKeys.GPG.password)
    applySigningPlugin().apply {
        // GPG_PRIVATE_KEY should contain the armoured private key that starts with -----BEGIN PGP PRIVATE KEY BLOCK-----
        // It can be obtained with gpg --armour --export-secret-keys KEY_ID
        useInMemoryPgpKeys(
            privateKey,
            password
        )
        sign(publishing.publications)
    }

    tasks.withType(Sign::class.java).configureEach {
        it.isEnabled = !privateKey.isNullOrBlank()
    }

    /**
     * Android publication
     */
    androidTarget.apply {
        publishLibraryVariants("release")
    }

    /**
     * Pom
     */
    configurePublications(
        publishing = publishing,
        artifactName = name
    )

    /**
     * Repositories
     */
    publishing.repositories {
        it.mavenSonatypeSnapshot(project = project)
        it.mavenSonatypeStaging(project = project)
    }

    rootProject.tasks.named("ossStagingRelease").configure {
        it.dependsOn(this@configurePublishingInternal.tasks.named("publishAllPublicationsToOssStagingRepository"))
    }
}

private fun Project.getOrCreateRepoIdTask(): TaskProvider<Task> {
    return try {
        rootProject.tasks.named("createStagingRepo")
    } catch (e: UnknownDomainObjectException) {
        rootProject.tasks.register("createStagingRepo") {
            it.outputs.file(rootProject.layout.buildDirectory.file("stagingRepoId"))

            it.doLast {
                val repoId = runBlocking {
                    nexusStagingClient.createRepository(
                        profileId = System.getenv(EnvVarKeys.Nexus.profileId),
                        description = "io.openfeedback ${rootProject.version}"
                    )
                }
                logger.log(LogLevel.LIFECYCLE, "repo created: $repoId")
                it.outputs.files.singleFile.writeText(repoId)
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

fun Project.getOrCreateRepoId(): Provider<String> {
    return getOrCreateRepoIdTask().map {
        it.outputs.files.singleFile.readText()
    }
}

fun Project.getOrCreateRepoUrl(): Provider<String> {
    return getOrCreateRepoId().map { "${baseUrl}staging/deployByRepositoryId/$it/" }
}

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

internal fun Project.registerReleaseTask(name: String): TaskProvider<Task> {
    val task = try {
        rootProject.tasks.named(name)
    } catch (e: UnknownDomainObjectException) {
        val repoId = getOrCreateRepoId()
        rootProject.tasks.register(name) {
            it.inputs.property(
                "repoId",
                repoId
            )
            it.doLast {
                it.closeAndReleaseStagingRepository(it.inputs.properties.get("repoId") as String)
            }
        }
    }

    return task
}

fun RepositoryHandler.mavenSonatypeSnapshot(project: Project) = maven {
    it.name = "ossSnapshots"
    it.url = project.uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
    it.credentials {
        it.username = System.getenv(EnvVarKeys.Nexus.username)
        it.password = System.getenv(EnvVarKeys.Nexus.password)
    }
}

fun RepositoryHandler.mavenSonatypeStaging(project: Project) = maven {
    it.name = "ossStaging"
    it.setUrl {
        project.uri(project.getOrCreateRepoUrl())
    }
    it.credentials {
        it.username = System.getenv(EnvVarKeys.Nexus.username)
        it.password = System.getenv(EnvVarKeys.Nexus.password)
    }
}

