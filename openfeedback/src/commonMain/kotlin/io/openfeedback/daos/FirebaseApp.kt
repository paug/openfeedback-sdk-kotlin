package io.openfeedback.daos

import io.openfeedback.FirebaseConfig

expect class FirebaseApp

expect fun createApp(config: FirebaseConfig): FirebaseApp
