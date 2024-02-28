package com.spend.swift.db

sealed class Collections(val name: String) {
    data object Profiles : Collections("profiles")
    data object Categories : Collections("categories")
}