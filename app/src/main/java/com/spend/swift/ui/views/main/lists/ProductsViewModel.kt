package com.spend.swift.ui.views.main.lists

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
import com.spend.swift.model.BasicProduct
import com.spend.swift.model.Product
import com.spend.swift.model.ShoppingList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ProductsViewModel : ViewModel() {

    private val db = Firebase.firestore

    private var _shoppingList = MutableStateFlow<ShoppingList>(ShoppingList.getTemplate())
    val shoppingList = _shoppingList.asStateFlow()

    private var _basicProducts = MutableStateFlow<List<BasicProduct>>(emptyList())
    val basicProducts = _basicProducts.asStateFlow()

    private var _products = MutableStateFlow<List<Product>>(emptyList())
    val products = _products.asStateFlow()

    var showLoadingDialog by mutableStateOf(false)

    private lateinit var listId: String

    fun getShoppingList(listId: String){
        this.listId = listId
        firebaseAuth {
            errorEnd(R.string.check_internet_connection)
        }
        loadProductsByList(listId)
        db.collection(Collections.ShoppingLists.name)
            .document(listId)
            .get()
            .addOnSuccessListener { snapshot ->
                val sList = ShoppingList(
                    name = snapshot.getString("name") ?: "",
                    profileId = snapshot.getString("profileId") ?: "",
                    createdBy = snapshot.getString("createdBy") ?: "",
                    categoryId = snapshot.getString("categoryId") ?: "",
                    completionDate = snapshot.getLong("completionDate") ?: 0,
                    docId = snapshot.getString(listId) ?: "",
                )
                _shoppingList.value = sList
                loadBasicProducts(sList.categoryId)
            }
            .addOnFailureListener {
                errorEnd(R.string.error_try_again)
            }
    }

    private fun loadProductsByList(listId: String) {
        db.collection(Collections.Products.name)
            .whereEqualTo("listId", listId)
            .addSnapshotListener { snapshot, error ->
                snapshot?.let {
                    val products = mutableListOf<Product>()
                    snapshot.documents.forEach { docSnap ->
                        val b = Product(
                            name = docSnap.getString("name") ?: "",
                            listId = docSnap.getString("listId") ?: "",
                            addedBy = docSnap.getString("addedBy") ?: "",
                            closedBy = docSnap.getString("closedBy") ?: "",
                            price = docSnap.getLong("price")?.toInt() ?: 0,
                        )
                        products.add(b)
                    }
                    _products.value = products.sortedBy { it.closedBy.isEmpty() }
                }
            }
    }

    private fun loadBasicProducts(categoryId: String) {
        val docRef = when(categoryId){
            "" -> db.collection(Collections.BasicGoods.name)
            else -> db.collection(Collections.BasicGoods.name)
                .whereEqualTo("categoryId", categoryId)
        }

        docRef
            .get()
            .addOnSuccessListener { snapshot ->
                val basicProductsList = mutableListOf<BasicProduct>()
                snapshot.documents.forEach { docSnap ->
                    val b = BasicProduct(
                        name = docSnap.getString("name") ?: "",
                        categoryId = docSnap.getString("categoryId") ?: "",
                        profileId = docSnap.getString("profileId") ?: "",
                        docId = docSnap.getString("docId") ?: "",
                    )
                    basicProductsList.add(b)
                }
                _basicProducts.value = basicProductsList
            }
            .addOnFailureListener {
                errorEnd(R.string.error_try_again)
            }
    }

    private fun errorEnd(@StringRes res: Int){
        showLoadingDialog = false
        SpendSwiftApp.getCtx().toast(res)
    }

    fun saveProduct(name: String) {
        val product = Product(
            name = name,
            listId = listId,
            addedBy = SharedPrefsHelper.readStr(SharedKeys.Nickname) ?: "",
            closedBy = "",
        )
        showLoadingDialog = true
        firebaseAuth(
            onSuccess = {
                if(products.value.find { c -> c.name == product.name } != null){
                    errorEnd(R.string.duplicate)
                    return@firebaseAuth
                }
                db.collection(Collections.Products.name)
                    .add(product.toMap())
                    .addOnSuccessListener {
                        showLoadingDialog = false
                    }
                    .addOnFailureListener {
                        it.printStackTrace()
                        errorEnd(R.string.error_try_again)
                    }
            },
        ){
            errorEnd(R.string.check_internet_connection)
        }
    }
}