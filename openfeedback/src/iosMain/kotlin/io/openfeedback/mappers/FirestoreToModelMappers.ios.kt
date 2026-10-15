package io.openfeedback.mappers

import cocoapods.FirebaseCore.FIRTimestamp
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.datetime.Instant

@OptIn(ExperimentalForeignApi::class)
internal actual fun timestampToInstant(nativeTimestamp: Any): Instant {
    val ts = nativeTimestamp as FIRTimestamp
    return Instant.fromEpochSeconds(ts.seconds, ts.nanoseconds)
}
