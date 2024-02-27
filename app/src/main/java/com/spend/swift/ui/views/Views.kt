package com.spend.swift.ui.views

sealed class Views(val path: String) {
    data object Registration : Views("registration")
    data object Login : Views("login")
    data object Main: Views("main")
    data object Account: Views("account")
    data object Categories: Views("categories")
    data object BasicGoods: Views("basic_goods")
}