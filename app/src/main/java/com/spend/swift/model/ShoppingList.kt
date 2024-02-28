package com.spend.swift.model

import com.spend.swift.helpers.SharedKeys
import com.spend.swift.helpers.SharedPrefsHelper

data class ShoppingList(
    val name: String,
    val categoryId: String,
    val profileId: String,
    val completionDate: Long,
    val docId: String,
){
    companion object{
        fun getTemplate() = ShoppingList(
            "",
            "",
            SharedPrefsHelper.readStr(SharedKeys.ProfileId) ?: "",
            0,
            ""
        )
    }

    fun toMap() = mapOf(
        "name" to name,
        "categoryId" to categoryId,
        "profileId" to profileId,
        "completionDate" to completionDate
    )
}
