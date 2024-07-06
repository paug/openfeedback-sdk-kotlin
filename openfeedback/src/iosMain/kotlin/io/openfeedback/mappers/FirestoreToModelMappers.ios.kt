package io.openfeedback.mappers

import dev.gitlive.firebase.firestore.Timestamp
import kotlinx.datetime.Instant

internal actual fun timestampToInstant(nativeTimestamp: Any): Instant {
    (nativeTimestamp as Timestamp)
    return Instant.fromEpochSeconds(nativeTimestamp.seconds, nativeTimestamp.nanoseconds)
}
