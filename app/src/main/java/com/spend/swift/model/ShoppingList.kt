package com.spend.swift.model

import com.spend.swift.helpers.SharedKeys
import com.spend.swift.helpers.SharedPrefsHelper
import com.spend.swift.helpers.getTimeMillisNextDay

data class ShoppingList(
    val name: String,
    val categoryId: String,
    val profileId: String,
    val createdBy: String,
    val completionDate: Long,
    val docId: String,
){
    companion object{
        fun getTemplate() = ShoppingList(
            "",
            "",
            SharedPrefsHelper.readStr(SharedKeys.ProfileId) ?: "",
            SharedPrefsHelper.readStr(SharedKeys.Nickname) ?: "",
            getTimeMillisNextDay(),
            ""
        )
    }

    fun toMap() = mapOf(
        "name" to name,
        "categoryId" to categoryId,
        "profileId" to profileId,
        "createdBy" to createdBy,
        "completionDate" to completionDate
    )
}
