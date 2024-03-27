package io.openfeedback.mappers

import com.google.firebase.Timestamp
import kotlinx.datetime.Instant

actual fun timestampToInstant(nativeTimestamp: Any): Instant {
    (nativeTimestamp as Timestamp)
    return Instant.fromEpochSeconds(nativeTimestamp.seconds, nativeTimestamp.nanoseconds)
}

