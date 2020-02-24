buildscript {
    val kotlinVersion = "1.3.61"
    project.extra.set("kotlinVersion", kotlinVersion)
    project.extra.set("composeVersion", "0.1.0-dev05")

    repositories {
        google()
        jcenter()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:4.0.0-alpha09")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${kotlinVersion}")
    }
}

version = "0.0.1"

subprojects {
    repositories {
        google()
        jcenter()
    }

    if (name != "sample-app") {
        apply(plugin = "maven-publish")
        afterEvaluate {
            configurePublishing()
        }
    }
}


fun Project.configurePublishing() {
    val publicationName = "default"
    val android = extensions.findByType(com.android.build.gradle.BaseExtension::class.java)

    /**
     * Javadoc
     */
    var javadocTask = tasks.findByName("javadoc") as Javadoc?
    var javadocJarTaskProvider: TaskProvider<org.gradle.jvm.tasks.Jar>? = null
    if (javadocTask == null && android != null) {
        javadocTask = tasks.create("javadoc", Javadoc::class.java) {
            source = android.sourceSets["main"].java.sourceFiles
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
            from(android.sourceSets["main"].java.sourceFiles)
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
                    artifactId = this@configurePublishing.name.substring("open".length)
                    version = rootProject.version as String

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
                name = "bintray"
                url = uri("https://api.bintray.com/maven/openfeedback/Android/${this@configurePublishing.name.substring("open".length)}/;publish=1;override=1")
                credentials {
                    username = findProperty("bintray.user") as String?
                    password = findProperty("bintray.apikey") as String?
                }
            }
        }
    }
}