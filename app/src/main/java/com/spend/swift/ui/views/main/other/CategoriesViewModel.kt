package com.spend.swift.ui.views.main.other

import android.util.Log
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
import com.spend.swift.model.Category
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

class CategoriesViewModel : ViewModel(){

    var showLoadingDialog by mutableStateOf(false)
    private val db = Firebase.firestore

    private var _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories = _categories.asStateFlow()

    init {
        firebaseAuth {
            errorEnd(R.string.check_internet_connection)
        }
        db.collection(Collections.Categories.name)
            .whereEqualTo("profileId", SharedPrefsHelper.readStr(SharedKeys.ProfileId))
            .addSnapshotListener{ querySnapshot, error ->
                if(querySnapshot != null && !querySnapshot.isEmpty){
                    val catList = mutableListOf<Category>()
                    querySnapshot.documents.forEach { docSnapshot ->
                        val cat = Category(
                            name = docSnapshot.getString("name") ?: "",
                            profileId = docSnapshot.getString("profileId") ?: "",
                            iconId = docSnapshot.getLong("iconId")?.toInt() ?: 0,
                            docId = docSnapshot.id,
                        )
                        catList.add(cat)
                    }
                    _categories.value = catList
                }
            }
    }

    fun save(newCategory: Category) {
        showLoadingDialog = true
        firebaseAuth(
            onSuccess = {
                if (newCategory.docId.isEmpty()){ // add
                    if(categories.value.find { c -> c.name == newCategory.name } != null){
                        errorEnd(R.string.duplicate_category)
                        return@firebaseAuth
                    }
                    db.collection(Collections.Categories.name)
                        .add(newCategory.toMap())
                        .addOnSuccessListener {
                            showLoadingDialog = false
                        }
                        .addOnFailureListener {
                            it.printStackTrace()
                            errorEnd(R.string.error_try_again)
                        }
                } else { // edit
                    db.collection(Collections.Categories.name).document(newCategory.docId)
                        .set(newCategory.toMap())
                        .addOnSuccessListener { showLoadingDialog = false }
                        .addOnFailureListener { errorEnd(R.string.check_internet_connection) }
                }
            },
        ){
            errorEnd(R.string.check_internet_connection)
        }

    }

    private fun errorEnd(@StringRes res: Int){
        showLoadingDialog = false
        SpendSwiftApp.getCtx().toast(res)
    }

    fun delete(category: Category) {
        showLoadingDialog = true
        firebaseAuth(
            onSuccess = {
                db.collection(Collections.Categories.name)
                    .document(category.docId)
                    .delete()
                    .addOnSuccessListener {
                        showLoadingDialog = false
                    }
                    .addOnFailureListener{
                        errorEnd(R.string.error_try_again)
                    }
            }
        ){ errorEnd(R.string.check_internet_connection) }
    }


}