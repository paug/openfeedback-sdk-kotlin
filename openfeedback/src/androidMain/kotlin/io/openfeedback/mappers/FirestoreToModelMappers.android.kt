package io.openfeedback.mappers

import com.google.firebase.Timestamp
import kotlinx.datetime.Instant

internal actual fun timestampToInstant(nativeTimestamp: Any): Instant {
    (nativeTimestamp as Timestamp)
    return Instant.fromEpochSeconds(nativeTimestamp.seconds, nativeTimestamp.nanoseconds)
}

