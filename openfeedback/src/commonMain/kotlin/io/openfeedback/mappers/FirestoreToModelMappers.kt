package io.openfeedback.mappers

import kotlinx.datetime.Instant

expect fun timestampToInstant(nativeTimestamp: Any): Instant
