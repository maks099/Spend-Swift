package com.spend.swift.model

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
}
