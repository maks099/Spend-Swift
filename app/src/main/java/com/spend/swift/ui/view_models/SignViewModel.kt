package com.spend.swift.ui.view_models

import android.util.Log
import android.util.Patterns
import androidx.annotation.StringRes
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.spend.swift.R
import com.spend.swift.SpendSwiftApp
import com.spend.swift.db.Collections
import com.spend.swift.db.firebaseAuth
import com.spend.swift.helpers.SharedKeys
import com.spend.swift.helpers.SharedPrefsHelper
import com.spend.swift.helpers.toast
import com.spend.swift.model.Profile

class SignViewModel : ViewModel() {
    var profile by mutableStateOf(Profile(
        email = "test@gmail.com",
        password = "Helloworld2#"
    ))

    var isLoadingDialogShow by mutableStateOf(false)

    private val db = Firebase.firestore

    fun logIn(
        onSuccess: () -> Unit,
    ){
        isLoadingDialogShow = true
        if(checkFields()){
            firebaseAuth(
                onSuccess = {
                    db.collection(Collections.Profiles.name)
                        .whereEqualTo("email", profile.email)
                        .whereEqualTo("password", profile.password)
                        .get()
                        .addOnSuccessListener { documents ->
                            if (documents != null && !documents.isEmpty){
                                val id = documents.first().get("id").toString().toLong()
                                SharedPrefsHelper.saveLong(SharedKeys.ProfileId, id)
                                isLoadingDialogShow = false
                                profile = profile.copy(id = id)
                                onSuccess()
                            } else errorEnd(R.string.check_data_and_again)
                        }

                },
                onError = {
                    errorEnd(R.string.check_internet_connection)
                }
            )
        }
    }

    fun signIn(
        onSuccess: () -> Unit,
    ){
        isLoadingDialogShow = true
        if(checkFields()){
            firebaseAuth(
                onSuccess = {
                    isAccountWithSameEmail {
                        if(!it){
                            db.collection(Collections.Profiles.name)
                                .add(profile.toMap())
                                .addOnSuccessListener {
                                    isLoadingDialogShow = false
                                    SharedPrefsHelper.saveLong(SharedKeys.ProfileId, profile.id)
                                    onSuccess()
                                }
                                .addOnFailureListener {
                                    it.printStackTrace()
                                    errorEnd(R.string.error_try_again)
                                }
                        }
                    }
                },
                onError = {
                    errorEnd(R.string.check_internet_connection)
                }
            )
        }
    }

    private fun isAccountWithSameEmail(
        onEnd: (Boolean) -> Unit
    ){
         db.collection(Collections.Profiles.name)
             .whereEqualTo("email", profile.email)
             .get()
             .addOnSuccessListener { value ->
                 if (value != null){
                     if(value.isEmpty){
                         onEnd(false)
                     } else errorEnd(R.string.duplicate_email)
                 } else errorEnd(R.string.check_internet_connection)
             }
             .addOnFailureListener {
                 errorEnd(R.string.check_internet_connection)
             }
    }

    private fun errorEnd(@StringRes res: Int){
        isLoadingDialogShow = false
        SpendSwiftApp.getCtx().toast(res)
    }

    private fun checkFields(): Boolean {
        val validPass = profile.password.trim().length >= 6
        if(!Patterns.EMAIL_ADDRESS.matcher(profile.email).matches()){
            errorEnd(R.string.invalid_mail)
        } else if (!validPass){
            errorEnd(R.string.invalid_pass)
        } else {
            return true
        }
        return false
    }



}