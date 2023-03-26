package io.openfeedback.extensions

import org.gradle.api.publish.maven.MavenPublication

fun MavenPublication.pom(
    name: String,
    description: String
) = pom {
    this.name.set(name)
    this.description.set(description)
    this.url.set("https://github.com/paug/openfeedback-android-sdk")

    scm {
        this.url.set("https://github.com/paug/openfeedback-android-sdk")
        this.connection.set("https://github.com/paug/openfeedback-android-sdk")
        this.developerConnection.set("https://github.com/paug/openfeedback-android-sdk")
    }

    licenses {
        license {
            this.name.set("MIT License")
            this.url.set("https://github.com/paug/openfeedback-android-sdk/blob/master/LICENSE")
        }
    }

    developers {
        developer {
            this.id.set("openfeedback team")
            this.name.set("openfeedback team")
        }
    }
}
