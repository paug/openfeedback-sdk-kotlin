package io.openfeedback

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate

internal fun Query.toFlow(): Flow<QuerySnapshot> = callbackFlow {
    val registration = addSnapshotListener(MetadataChanges.INCLUDE) { snapshot, exception ->
        if (snapshot != null) {
            runCatching {
                trySend(snapshot)
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

internal fun DocumentReference.toFlow(): Flow<DocumentSnapshot> = callbackFlow {
    val registration = addSnapshotListener(MetadataChanges.INCLUDE) { snapshot, exception ->
        if (snapshot != null) {
            runCatching {
                trySend(snapshot)
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
