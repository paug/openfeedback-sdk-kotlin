#!/usr/bin/env kotlin
import java.io.File

/**
 * A script to run locally in order to make a release.
 *
 * You need kotlin 1.3.70+ installed on your machine
 */

fun runCommand(vararg args: String): String {
    val builder = ProcessBuilder(*args)
            .redirectError(ProcessBuilder.Redirect.INHERIT)

    val process = builder.start()
    val ret = process.waitFor()

    val output = process.inputStream.bufferedReader().readText()
    if (ret != 0) {
        throw java.lang.Exception("command ${args.joinToString(" ")} failed:\n$output")
    }

    return output
}

fun setCurrentVersion(version: String) {
    val gradleProperties = File("librarian.root.properties")
    val newContent = gradleProperties.readLines().map {
        it.replace(Regex("pom.version=.*"), "pom.version=$version")
    }.joinToString(separator = "\n", postfix = "\n")
    gradleProperties.writeText(newContent)
}

fun getCurrentVersion(): String {
    val versionLines = File("librarian.root.properties").readLines().filter { it.startsWith("pom.version=") }

    require(versionLines.isNotEmpty()) {
        "cannot find the version in ./gradle.properties"
    }

    require(versionLines.size == 1) {
        "multiple versions found in ./gradle.properties"
    }

    val regex = Regex("pom.version=(.*)-SNAPSHOT")
    val matchResult = regex.matchEntire(versionLines.first())

    require(matchResult != null) {
        "'${versionLines.first()}' doesn't match ${regex.pattern}"
    }

    return matchResult.groupValues[1]
}

val versionRegex = Regex("(?<major>[0-9]+)\\.(?<minor>[0-9]+)\\.(?<patch>[0-9]+)(-(?<prereleaseName>alpha|beta)\\.(?<prerelease>[0-9]+))?(-SNAPSHOT)?")
fun getNext(version: String, position: Int): String {
    val groupName = when (position) {
        0 -> "major"
        1 -> "minor"
        2 -> "patch"
        3 -> "prerelease"
        else -> throw IllegalArgumentException("position must be 0, 1, 2 or 3")
    }
    return version.replace(versionRegex) {
        when (groupName) {
            "major" -> "${it.groups["major"]!!.value.toInt() + 1}.0.0"
            "minor" -> "${it.groups["major"]!!.value}.${it.groups["minor"]!!.value.toInt() + 1}.0"
            "patch" -> "${it.groups["major"]!!.value}.${it.groups["minor"]!!.value}.${it.groups["patch"]!!.value.toInt() + 1}"
            "prerelease" -> {
                val prereleaseName = it.groups["prereleaseName"]?.value ?: "alpha"
                val newPrerelease = (it.groups["prerelease"]?.value?.toInt() ?: 0) + 1
                "${it.groups["major"]!!.value}.${it.groups["minor"]!!.value}.${it.groups["patch"]!!.value}-$prereleaseName.$newPrerelease"
            }
            else -> throw IllegalArgumentException("unknown group $groupName")
        }
    }
}

fun getNextPreRelease(version: String) = getNext(version, 3)
fun getNextPatch(version: String) = getNext(version, 2)
fun getNextMinor(version: String) = getNext(version, 1)
fun getNextMajor(version: String) = getNext(version, 0)

if (runCommand("git", "status", "--porcelain").isNotEmpty()) {
    println("Your git repo is not clean. Make sur to stash or commit your changes before making a release")
    System.exit(1)
}

val version = getCurrentVersion()
val nextPreRelease = getNextPreRelease(version)
val nextPatch = getNextPatch(version)
val nextMinor = getNextMinor(version)
val nextMinorAfterMinor = getNextMinor(nextMinor)
val nextMajor = getNextMajor(version)
val nextMinorAfterMajor = getNextMinor(nextMajor)

var tagVersion: String = ""
var nextSnapshot: String = ""

while (tagVersion.isEmpty()) {
    println("Current version is '$version-SNAPSHOT'.")
    println("1. current: tag $version and bump to $nextMinor-SNAPSHOT")
    println("2. prerelease: tag $version and bump to $nextPreRelease-SNAPSHOT")
    println("3. patch: tag $nextPatch and bump to $nextMinor-SNAPSHOT")
    println("4. minor: tag $nextMinor and bump to $nextMinorAfterMinor-SNAPSHOT")
    println("5. major: tag $nextMajor and bump to $nextMinorAfterMajor-SNAPSHOT")
    println("What do you want to do [1/2/3/4/5]?")

    val answer = readLine()!!.trim()
    when (answer) {
        "1" -> {
            tagVersion = version
            nextSnapshot = "$nextMinor-SNAPSHOT"
        }
        "2" -> {
            tagVersion = version
            nextSnapshot = "$nextPreRelease-SNAPSHOT"
        }
        "3" -> {
            tagVersion = nextPatch
            nextSnapshot = "$nextMinor-SNAPSHOT"
        }
        "4" -> {
            tagVersion = nextMinor
            nextSnapshot = "$nextMinorAfterMinor-SNAPSHOT"
        }
        "5" -> {
            tagVersion = nextMajor
            nextSnapshot = "$nextMinorAfterMajor-SNAPSHOT"
        }
    }
}

setCurrentVersion(tagVersion)

runCommand("git", "commit", "-a", "-m", "release $tagVersion")
runCommand("git", "tag", "v$tagVersion")

setCurrentVersion(nextSnapshot)
runCommand("git", "commit", "-a", "-m", "version is now $nextSnapshot")

println("Everything is done. Verify everything is ok and type `git push origin master` to trigger the new version.")
