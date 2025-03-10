@file:Suppress(
    "CANNOT_OVERRIDE_INVISIBLE_MEMBER",
    "INVISIBLE_MEMBER",
    "INVISIBLE_REFERENCE",
)
package io.openfeedback.mappers

import dev.gitlive.firebase.firestore.Timestamp
import kotlinx.datetime.Instant

internal actual fun timestampToInstant(nativeTimestamp: Any): Instant {
    val ts = Timestamp(nativeTimestamp)
    return Instant.fromEpochSeconds(ts.seconds, ts.nanoseconds)
}
