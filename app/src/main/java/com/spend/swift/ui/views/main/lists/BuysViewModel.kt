package com.spend.swift.ui.views.main.lists

import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.DocumentReference
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
import com.spend.swift.model.Category
import com.spend.swift.model.Product
import com.spend.swift.model.ShoppingList
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class BuysViewModel : ViewModel() {

    private val db = Firebase.firestore
    private val profileId = SharedPrefsHelper.readStr(SharedKeys.ProfileId) ?: ""

    var showLoadingDialog by mutableStateOf(false)

    private var _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories = _categories.asStateFlow()

    private var _shoppingLists = MutableStateFlow<List<ShoppingList>>(emptyList())
    val shoppingLists = _shoppingLists.asStateFlow()

    private var _productsLists = MutableStateFlow<Map<String, List<Product>>>(emptyMap())
    val productsLists = _productsLists.asStateFlow()
    private val resMap = mutableMapOf<String, List<Product>>()

    init {
        loadAll()
    }

    fun loadAll() {
        viewModelScope.launch {
            firebaseAuth {
                errorEnd(R.string.check_internet_connection)
            }
            loadProductsList()
            delay(1000)
            loadCategories()
            loadShoppingLists()
        }
    }

    private fun loadProductsList(){
        Log.d("DDD", "load product list")
        viewModelScope.launch {
            shoppingLists.collectLatest { spLists ->
                showLoadingDialog = true
                spLists.forEach{ spList ->
                    db.collection(Collections.Products.name)
                        .whereEqualTo("listId", spList.docId)
                        .addSnapshotListener{ value, error ->
                            value?.let { querySnapshot ->
                                val products = mutableListOf<Product>()
                                querySnapshot.documents.forEach { docSnap ->
                                    val b = Product(
                                        name = docSnap.getString("name") ?: "",
                                        listId = docSnap.getString("listId") ?: "",
                                        addedBy = docSnap.getString("addedBy") ?: "",
                                        closedBy = docSnap.getString("closedBy") ?: "",
                                        price = docSnap.getLong("price")?.toInt() ?: 0,
                                        docId = docSnap.id
                                    )
                                    products.add(b)
                                }
                                resMap[spList.docId] = products.sortedBy { it.closedBy.isNotEmpty() }
                            }
                            _productsLists.value = resMap


                        }
                }
                showLoadingDialog = false
            }
        }
    }

    private fun loadShoppingLists() {
        db.collection(Collections.ShoppingLists.name)
            .whereEqualTo("profileId", SharedPrefsHelper.readStr(SharedKeys.ProfileId))
            .addSnapshotListener{ querySnapshot, error ->

                if(querySnapshot != null && !querySnapshot.isEmpty){
                    Log.d("DDD", "collect shopping list")
                    val shopLists = mutableListOf<ShoppingList>()
                    querySnapshot.documents.forEach { docSnapshot ->
                        val shopList = ShoppingList(
                            name = docSnapshot.getString("name") ?: "",
                            profileId = docSnapshot.getString("profileId") ?: "",
                            categoryId = docSnapshot.getString("categoryId") ?: "",
                            createdBy = docSnapshot.getString("createdBy") ?: "",
                            completionDate = docSnapshot.getLong("completionDate") ?: 0,
                            docId = docSnapshot.id,
                        )
                        shopLists.add(shopList)
                    }
                    _shoppingLists.value = shopLists

                }
            }
    }

    private fun loadCategories() {
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

    fun save(shoppingList: ShoppingList) {
        showLoadingDialog = true
        firebaseAuth(
            onSuccess = {
                if (shoppingList.docId.isEmpty()){ // add
                    if(shoppingLists.value.find { c -> c.name == shoppingList.name } != null){
                        errorEnd(R.string.duplicate)
                        return@firebaseAuth
                    }
                    db.collection(Collections.ShoppingLists.name)
                        .add(shoppingList.toMap())
                        .addOnSuccessListener {
                            showLoadingDialog = false
                        }
                        .addOnFailureListener {
                            it.printStackTrace()
                            errorEnd(R.string.error_try_again)
                        }
                } else { // edit
                    db.collection(Collections.ShoppingLists.name).document(shoppingList.docId)
                        .set(shoppingList.toMap())
                        .addOnSuccessListener { showLoadingDialog = false }
                        .addOnFailureListener { errorEnd(R.string.check_internet_connection) }
                }
            },
        ){
            errorEnd(R.string.check_internet_connection)
        }
    }

    fun delete(shoppingList: ShoppingList) {
        showLoadingDialog = true
        firebaseAuth(
            onSuccess = {
                db.collection(Collections.ShoppingLists.name)
                    .document(shoppingList.docId)
                    .delete()
                    .addOnSuccessListener {
                        showLoadingDialog = false
                    }
                    .addOnFailureListener {
                        errorEnd(R.string.error_try_again)
                    }
                productsLists.value[shoppingList.docId]?.let { list ->
                    list.forEach { product ->
                        db.collection(Collections.Products.name)
                            .document(product.docId)
                            .delete()
                    }
                }
            }
        ){ errorEnd(R.string.check_internet_connection) }
    }

    fun closeProduct(product: Product) {
        showLoadingDialog = true
        val p = product.copy(
            closedBy = SharedPrefsHelper.readStr(SharedKeys.Nickname) ?: "",
        )
        updateProduct(p)
    }

    fun openProduct(product: Product) {
        showLoadingDialog = true
        val p = product.copy(
            closedBy = "",
            price = 0
        )
        updateProduct(p)
    }

    private fun updateProduct(product: Product){
        db.collection(Collections.Products.name)
            .document(product.docId)
            .set(product.toMap())
            .addOnSuccessListener {
                _shoppingLists.value = shoppingLists.value
                showLoadingDialog = false
            }
            .addOnFailureListener{
                errorEnd(R.string.error_try_again)
            }
    }

    fun reSaveList(shoppingList: ShoppingList, products: List<Product>?) {
        db.collection(Collections.ShoppingLists.name)
            .add(shoppingList.toMap())
            .addOnSuccessListener { docRef ->
                products?.let {
                    fillListByProducts(docRef.id, products)
                }
            }
            .addOnFailureListener {
                it.printStackTrace()
                errorEnd(R.string.error_try_again)
            }
    }

    private fun fillListByProducts(id: String, products: List<Product>){
        products.forEach { product ->
            db.collection(Collections.Products.name)
                .add(product.copy(listId = id, price = 0, closedBy = "").toMap())
        }
        SpendSwiftApp.getCtx().toast(R.string.list_duplicate_toast)
    }
}