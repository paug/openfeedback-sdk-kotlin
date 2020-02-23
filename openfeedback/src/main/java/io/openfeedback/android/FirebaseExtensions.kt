package io.openfeedback.android

import com.google.firebase.firestore.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate

fun Query.toFlow(): Flow<QuerySnapshot> = callbackFlow {
    val registration = addSnapshotListener(MetadataChanges.INCLUDE) { snapshot, exception ->
        if (snapshot != null) {
            runCatching {
                offer(snapshot!!)
            }
        }
        if (exception != null) {
            close(exception)
        }
    }

    awaitClose {
        registration.remove()
    }
}.conflate()

fun DocumentReference.toFlow(): Flow<DocumentSnapshot> = callbackFlow {
    val registration = addSnapshotListener(MetadataChanges.INCLUDE) { snapshot, exception ->
        if (snapshot != null) {
            runCatching {
                offer(snapshot!!)
            }
        }
        if (exception != null) {
            close(exception)
        }
    }

    awaitClose {
        registration.remove()
    }
}.conflate()