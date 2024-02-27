package com.spend.swift.db

import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.spend.swift.FIREBASE

private val auth = Firebase.auth

fun firebaseAuth(
    onSuccess: () -> Unit = {},
    onError: () -> Unit = {}
) {
    if(auth.currentUser != null){
        onSuccess()
        return
    }

    auth
        .signInAnonymously()
        .addOnSuccessListener {
            Log.d(FIREBASE, "auth ok")
            onSuccess()
        }
        .addOnFailureListener{
            it.printStackTrace()
            Log.d(FIREBASE, "auch cancel: ${it.message}")
            onError()
        }
}

