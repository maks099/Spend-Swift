package com.spend.swift.ui.views.main.other

import androidx.annotation.StringRes
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.spend.swift.DEFAULT_ICON_ID
import com.spend.swift.R
import com.spend.swift.SpendSwiftApp
import com.spend.swift.db.Collections
import com.spend.swift.db.firebaseAuth
import com.spend.swift.helpers.SharedKeys
import com.spend.swift.helpers.SharedPrefsHelper
import com.spend.swift.helpers.toast
import com.spend.swift.model.BasicProduct
import com.spend.swift.model.Category
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class BasicProductsViewModel : ViewModel(){

    private val db = Firebase.firestore
    private val profileId = SharedPrefsHelper.readStr(SharedKeys.ProfileId) ?: ""

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    var categories = _categories.asStateFlow()

    private val _basicProducts = MutableStateFlow<List<BasicProduct>>(emptyList())
    var basicProducts = _basicProducts.asStateFlow()

    var showLoadingDialog by mutableStateOf(false)

    init {
        firebaseAuth { errorEnd(R.string.check_internet_connection) }
        loadCategories()
        loadBasicProducts()
    }

    private fun loadBasicProducts() {
        db.collection(Collections.BasicGoods.name)
            .whereEqualTo("profileId", profileId)
            .addSnapshotListener{ querySnapshot, error ->
                if(querySnapshot != null && !querySnapshot.isEmpty){
                    val basicGoodsList = mutableListOf<BasicProduct>()
                    querySnapshot.documents.forEach { docSnapshot ->
                        val basicGoods = BasicProduct(
                            name = (docSnapshot.getString("name") ?: ""),
                            categoryId = (docSnapshot.getString("categoryId") ?: ""),
                            profileId = profileId,
                            docId = docSnapshot.id
                        )
                        basicGoodsList.add(basicGoods)
                    }
                    _basicProducts.value = basicGoodsList
                }
            }
    }

    private fun loadCategories() {
        db.collection(Collections.Categories.name)
            .whereEqualTo("profileId", profileId)
            .addSnapshotListener{ querySnapshot, error ->
                if(querySnapshot != null && !querySnapshot.isEmpty){
                    val catList = mutableListOf<Category>()
                    querySnapshot.documents.forEach { docSnapshot ->
                        val cat = Category(
                            name = (docSnapshot.getString("name") ?: ""),
                            profileId = profileId,
                            iconId = docSnapshot.getLong("iconId")?.toInt() ?: 0,
                            docId = docSnapshot.id,
                        )
                        catList.add(cat)
                    }
                    catList.add(0,
                        Category(SpendSwiftApp.getCtx().getString(R.string.all), DEFAULT_ICON_ID, profileId)
                    )
                    _categories.value = catList
                }
            }
    }

    private fun errorEnd(@StringRes res: Int){
        showLoadingDialog = false
        SpendSwiftApp.getCtx().toast(res)
    }

    fun save(basicProduct: BasicProduct) {
        showLoadingDialog = true
        firebaseAuth(
            onSuccess = {
                if (basicProduct.docId.isEmpty()){ // add
                    if(basicProducts.value.find { c -> c.name == basicProduct.name } != null){
                        errorEnd(R.string.duplicate)
                        return@firebaseAuth
                    }
                    db.collection(Collections.BasicGoods.name)
                        .add(basicProduct.toMap())
                        .addOnSuccessListener {
                            showLoadingDialog = false
                        }
                        .addOnFailureListener {
                            it.printStackTrace()
                            errorEnd(R.string.error_try_again)
                        }
                } else { // edit
                    db.collection(Collections.BasicGoods.name).document(basicProduct.docId)
                        .set(basicProduct.toMap())
                        .addOnSuccessListener { showLoadingDialog = false }
                        .addOnFailureListener { errorEnd(R.string.check_internet_connection) }
                }
            },
        ){
            errorEnd(R.string.check_internet_connection)
        }
    }

    fun delete(basicProduct: BasicProduct) {
        showLoadingDialog = true
        firebaseAuth(
            onSuccess = {
                db.collection(Collections.BasicGoods.name)
                    .document(basicProduct.docId)
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

