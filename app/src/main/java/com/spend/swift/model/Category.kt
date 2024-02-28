package com.spend.swift.model

import com.spend.swift.DEFAULT_ICON_ID
import com.spend.swift.R
import com.spend.swift.SpendSwiftApp
import com.spend.swift.helpers.SharedKeys
import com.spend.swift.helpers.SharedPrefsHelper
import java.util.Date

data class Category(
    val name: String,
    val iconId: Int,
    val profileId: String,
    val docId: String = "",
){
    fun toMap() = mapOf(
        "name" to name,
        "iconId" to iconId,
        "profileId" to profileId,
    )

    companion object {
        fun getTemplate() =  Category(
            SpendSwiftApp.getCtx().getString(R.string.all), DEFAULT_ICON_ID, SharedPrefsHelper.readStr(
                SharedKeys.ProfileId) ?: "")
    }
}
