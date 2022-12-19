package io.openfeedback.extensions

import io.openfeedback.EnvVarKeys
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionContainer
import org.gradle.api.publish.PublishingExtension
import org.gradle.plugins.signing.SigningExtension

fun ExtensionContainer.configurePublishing(
    project: Project,
    artifactName: String
) = getByType(PublishingExtension::class.java).apply {
    publications {
        createReleasePublication(
            project = project,
            artifactName = artifactName
        )
    }

    repositories {
        mavenSonatypeSnapshot(project = project)
        mavenSonatypeStaging(project = project)
    }
}

fun ExtensionContainer.configureSigning() = configure(SigningExtension::class.java) {
    // GPG_PRIVATE_KEY should contain the armoured private key that starts with -----BEGIN PGP PRIVATE KEY BLOCK-----
    // It can be obtained with gpg --armour --export-secret-keys KEY_ID
    useInMemoryPgpKeys(
        System.getenv(EnvVarKeys.GPG.privateKey),
        System.getenv(EnvVarKeys.GPG.password)
    )
    sign((getByName("publishing") as PublishingExtension).publications)
}
