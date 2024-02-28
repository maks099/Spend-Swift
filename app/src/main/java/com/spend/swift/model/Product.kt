package com.spend.swift.model

data class Product(
    val name: String,
    val listId: String,
    val addedBy: String,
    val closedBy: String,
    val price: Int = 0,
    val docId: String = ""
){
    fun toMap() = mapOf(
        "name" to name,
        "listId" to listId,
        "addedBy" to addedBy,
        "closedBy" to closedBy,
        "price" to price,
        "docId" to docId,
    )

    companion object {
        fun getTemplate() = Product(
            name = "",
            listId = "",
            closedBy = "",
            addedBy = ""
        )
    }

 }
