package io.openfeedback.extensions

import io.openfeedback.EnvVarKeys
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler

fun RepositoryHandler.mavenSonatypeSnapshot(project: Project) = maven {
    name = "ossSnapshots"
    url = project.uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
    credentials {
        username = System.getenv(EnvVarKeys.Nexus.username)
        password = System.getenv(EnvVarKeys.Nexus.password)
    }
}

fun RepositoryHandler.mavenSonatypeStaging(project: Project) = maven {
    name = "ossStaging"
    setUrl {
        project.uri(project.getOrCreateOssStagingUrl())
    }
    credentials {
        username = System.getenv(EnvVarKeys.Nexus.username)
        password = System.getenv(EnvVarKeys.Nexus.password)
    }
}
