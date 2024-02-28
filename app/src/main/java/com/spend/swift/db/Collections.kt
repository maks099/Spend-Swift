package com.spend.swift.db

sealed class Collections(val name: String) {
    data object Profiles : Collections("profiles")
    data object Categories : Collections("categories")
    data object BasicGoods : Collections("basic_goods")
    data object ShoppingLists : Collections("shopping_lists")
    data object Products : Collections("products")
}