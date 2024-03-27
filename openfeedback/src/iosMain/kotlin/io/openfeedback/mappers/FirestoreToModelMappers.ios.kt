package io.openfeedback.mappers

import dev.gitlive.firebase.firestore.Timestamp
import io.openfeedback.model.Comment
import kotlinx.datetime.Instant

actual fun timestampToInstant(nativeTimestamp: Any): Instant {
    (nativeTimestamp as Timestamp)
    return Instant.fromEpochSeconds(nativeTimestamp.seconds, nativeTimestamp.nanoseconds)
}
