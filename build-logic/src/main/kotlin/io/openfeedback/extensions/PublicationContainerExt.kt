package io.openfeedback.extensions

import org.gradle.api.Project
import org.gradle.api.publish.PublicationContainer
import org.gradle.api.publish.maven.MavenPublication

fun PublicationContainer.createReleasePublication(
    project: Project,
    artifactName: String
) = create("default", MavenPublication::class.java) {
    apply {
        project.afterEvaluate {
            from(components.findByName("release"))
        }

        pom {
            groupId = "io.openfeedback"
            artifactId = artifactName
            version = project.rootProject.version.toString()

            name.set(artifactId)
            packaging = "aar"
            description.set(artifactId)
            url.set("https://github.com/paug/openfeedback-android-sdk")

            scm {
                url.set("https://github.com/paug/openfeedback-android-sdk")
                connection.set("https://github.com/paug/openfeedback-android-sdk")
                developerConnection.set("https://github.com/paug/openfeedback-android-sdk")
            }

            licenses {
                license {
                    name.set("MIT License")
                    url.set("https://github.com/paug/openfeedback-android-sdk/blob/master/LICENSE")
                }
            }

            developers {
                developer {
                    id.set("openfeedback team")
                    name.set("openfeedback team")
                }
            }
        }
    }
}
