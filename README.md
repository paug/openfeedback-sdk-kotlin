# Open-Feedback Android SDK [![Build Status](https://travis-ci.org/paug/openfeedback-android-sdk.svg?branch=master)](https://travis-ci.org/paug/openfeedback-android-sdk) [ ![Download](https://api.bintray.com/packages/openfeedback/Android/feedback-android-sdk-ui/images/download.svg) ](https://bintray.com/openfeedback/Android/feedback-android-sdk-ui/_latestVersion)

An Android client for Open-Feeedback https://github.com/HugoGresse/open-feedback:

![screenshot](docs/screenshot.png)

The SDK exposes a regular View and a Composable if you want to try [compose](https://developer.android.com/jetpack/compose). Please consult the [sample-app](sample-app/src/main/java/io/openfeedback/android/sample/MainActivity.kt) for usage.

# Installation

The SDK is available on jcenter:

```
repositories {
    jcenter()
}

dependencies {
    implementation("io.openfeedback:feedback-android-sdk-ui:0.0.1")
}
```

# Limitations and TODO

The SDK is still very young and misses some features, most notably comments. Feedbacks welcome.
