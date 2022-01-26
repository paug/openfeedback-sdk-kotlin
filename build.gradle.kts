buildscript {
    val kotlinVersion = "1.6.10"
    project.extra.set("kotlinVersion", kotlinVersion)
    project.extra.set("composeVersion", "1.2.0-alpha02")

    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.0.4")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${kotlinVersion}")
    }
}

version = "0.0.6-SNAPSHOT"

subprojects {
    repositories {
        google()
        mavenCentral()
    }

    if (name != "sample-app") {
        apply(plugin = "maven-publish")
        afterEvaluate {
            afterEvaluate {
                configurePublishing()
            }
        }
    }
}

class PomValues(
        val artifactName: String
)

fun Project.configurePublishing() {
    val publicationName = "default"
    val android = extensions.findByType(com.android.build.gradle.BaseExtension::class.java)

    val values = when(name) {
        "openfeedback" -> PomValues(artifactName = "feedback-android-sdk")
        "openfeedback-ui" -> PomValues(artifactName = "feedback-android-sdk-ui")
        else -> error("don't know how to configure $name")
    }
    /**
     * Javadoc
     */
    var javadocTask = tasks.findByName("javadoc") as Javadoc?
    var javadocJarTaskProvider: TaskProvider<org.gradle.jvm.tasks.Jar>? = null
    if (javadocTask == null && android != null) {
        javadocTask = tasks.create("javadoc", Javadoc::class.java) {
            // source = android.sourceSets.get("main").java.sourceFiles
            //classpath += project.files(android.joinToString(File.pathSeparator))
        }
    }

    if (javadocTask != null) {
        javadocJarTaskProvider = tasks.register("javadocJar", org.gradle.jvm.tasks.Jar::class.java) {
            archiveClassifier.set("javadoc")
            dependsOn(javadocTask)
            from(javadocTask.destinationDir)
        }
    }

    var sourcesJarTaskProvider: TaskProvider<org.gradle.jvm.tasks.Jar>? = null
    val javaPluginConvention = project.convention.findPlugin(JavaPluginConvention::class.java)
    if (javaPluginConvention != null && android == null) {
        sourcesJarTaskProvider = tasks.register("sourcesJar", org.gradle.jvm.tasks.Jar::class.java) {
            archiveClassifier.set("sources")
            from(javaPluginConvention.sourceSets.get("main").allSource)
        }
    } else if (android != null) {
        sourcesJarTaskProvider = tasks.register("sourcesJar", org.gradle.jvm.tasks.Jar::class.java) {
            archiveClassifier.set("sources")
            // from(android.sourceSets["main"].java.sourceFiles)
        }
    }

    tasks.withType(Javadoc::class.java) {
        // TODO: fix the javadoc warnings
        (options as StandardJavadocDocletOptions).addStringOption("Xdoclint:none", "-quiet")
    }

    configure<PublishingExtension> {
        publications {
            create<MavenPublication>(publicationName) {
                from(components.findByName("release"))

                if (javadocJarTaskProvider != null) {
                    artifact(javadocJarTaskProvider.get())
                }
                if (sourcesJarTaskProvider != null) {
                    artifact(sourcesJarTaskProvider.get())
                }

                pom {
                    groupId = "io.openfeedback"
                    artifactId = values.artifactName
                    version = "0.0.6-SNAPSHOT"

                    name.set(artifactId)
                    packaging = "aar"
                    description.set(artifactId)
                    url.set("https://github.com/martinbonnin/openfeedback-android-sdk")

                    scm {
                        url.set("https://github.com/martinbonnin/openfeedback-android-sdk")
                        connection.set("https://github.com/martinbonnin/openfeedback-android-sdk")
                        developerConnection.set("https://github.com/martinbonnin/openfeedback-android-sdk")
                    }

                    licenses {
                        license {
                            name.set("MIT License")
                            url.set("https://github.com/martinbonnin/openfeedback-android-sdk/blob/master/LICENSE")
                        }
                    }

                    developers {
                        developer {
                            id.set("Martin Bonnin")
                            name.set("Martin Bonnin")
                        }
                    }
                }
            }
        }

        repositories {
            maven {
                name = "ojo"
                url = uri("https://oss.jfrog.org/artifactory/oss-snapshot-local/")
                credentials {
                    username = System.getenv("BINTRAY_USER")
                    password = System.getenv("BINTRAY_API_KEY")
                }
            }

            maven {
                name = "bintray"
                url = uri("https://api.bintray.com/maven/openfeedback/Android/${values.artifactName}/;publish=1;override=1")
                credentials {
                    username = System.getenv("BINTRAY_USER")
                    password = System.getenv("BINTRAY_API_KEY")
                }
            }
        }
    }
}

val publishToBintray = tasks.register("publishToBintray") {
    dependsOn(subprojects.flatMap {subproject ->
        subproject.tasks.matching {
            it.name == "publishAllPublicationsToBintrayRepository"
        }
    })
}

val publishToOjo = tasks.register("publishToOjo") {
    dependsOn(subprojects.flatMap {subproject ->
        subproject.tasks.matching {
            it.name == "publishAllPublicationsToOjoRepository"
        }
    })
}

tasks.register("publishIfNeeded") {
    val eventName = System.getenv("GITHUB_EVENT_NAME")
    val ref = System.getenv("GITHUB_REF")
    project.logger.log(LogLevel.LIFECYCLE, "publishIfNeeded eventName=$eventName ref=$ref")

    if (eventName == "push" && ref == "refs/heads/master") {
        project.logger.log(LogLevel.LIFECYCLE, "Deploying snapshot to OJO...")
        dependsOn(publishToOjo)
    }

    if (ref?.startsWith("refs/tags/") == true) {
        project.logger.log(LogLevel.LIFECYCLE, "Deploying release to Bintray...")
        dependsOn(publishToBintray)
    }
}
