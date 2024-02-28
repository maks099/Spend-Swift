package com.spend.swift.ui.views.main.other

import androidx.annotation.StringRes
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.spend.swift.R
import com.spend.swift.SpendSwiftApp
import com.spend.swift.db.Collections
import com.spend.swift.db.firebaseAuth
import com.spend.swift.helpers.SharedKeys
import com.spend.swift.helpers.SharedPrefsHelper
import com.spend.swift.helpers.toast
import com.spend.swift.model.Category
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class BasicGoodsViewModel : ViewModel(){

    var showLoadingDialog by mutableStateOf(false)

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    var categories = _categories.asStateFlow()

    init {
        firebaseAuth(
            onSuccess = {
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
        ) { errorEnd(R.string.check_internet_connection) }
    }

    private fun errorEnd(@StringRes res: Int){
        showLoadingDialog = false
        SpendSwiftApp.getCtx().toast(res)
    }
}

