package io.openfeedback.mappers

import cocoapods.FirebaseCore.FIRTimestamp
import kotlinx.cinterop.ExperimentalForeignApi
import kotlin.time.Instant

@OptIn(ExperimentalForeignApi::class)
internal actual fun timestampToInstant(nativeTimestamp: Any): Instant {
    val ts = nativeTimestamp as FIRTimestamp
    return Instant.fromEpochSeconds(ts.seconds, ts.nanoseconds)
}
