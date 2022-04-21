[![Build Status](https://travis-ci.org/paug/openfeedback-android-sdk.svg?branch=master)](https://travis-ci.org/paug/openfeedback-android-sdk) [ ![Download](https://api.bintray.com/packages/openfeedback/Android/feedback-android-sdk-ui/images/download.svg) ](https://bintray.com/openfeedback/Android/feedback-android-sdk-ui/_latestVersion)
# Open-Feedback Android SDK

An Android client for Open-Feeedback https://github.com/HugoGresse/open-feedback:

![screenshot](docs/screenshot.png)

## Usage

The Composable `SessionFeedbackContainer` is the entry point to vote on a session. It'll make calls
between the Firebase which host your OpenFeedback instance and your mobile application. It is
mandatory to pass the `OpenFeedbackState` to give the Firebase configuration and your open-feedback
configuration which is common for all sessions of your event.

```kotlin
val openFeedbackState = rememberOpenFeedbackState(
    projectId = "<your-open-feedback-project-id>",
    firebaseConfig = OpenFeedback.FirebaseConfig(
        projectId = "<your-firebase-open-feedback-project-id>",
        applicationId = "<your-firebase-open-feedback-app-id>",
        apiKey = "<your-firebase-open-feedback-api-key>",
        databaseUrl = "https://<your-firebase-open-feedback-project-id>.firebaseio.com"
    )
)
SessionFeedbackContainer(
    openFeedbackState = openFeedbackState,
    sessionId = "<your-open-feedback-session-id>",
    language = "<language-code>"
)
```

That's all!

If you want to see an example, please check the [sample-app](sample-app/src/main/java/io/openfeedback/android/sample/MainActivity.kt).

## Installation

The SDK is available on mavenCentral:

```
repositories {
    mavenCentral()
}

dependencies {
    implementation("io.openfeedback:feedback-android-sdk-ui:0.0.6")
}
```

## Limitations and TODO

The SDK is still very young and misses some features, most notably comments. Feedbacks welcome.
