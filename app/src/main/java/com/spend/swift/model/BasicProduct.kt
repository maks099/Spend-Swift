package com.spend.swift.model

data class BasicProduct(
    val name: String,
    val categoryId: String,
    val profileId: String,
    val docId: String
) {
    fun toMap() = mapOf(
        "name" to name,
        "categoryId" to categoryId,
        "profileId" to profileId,
    )
}
