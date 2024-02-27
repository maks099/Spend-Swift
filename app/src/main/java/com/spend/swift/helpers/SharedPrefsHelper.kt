package com.spend.swift.helpers

import android.content.Context
import android.content.SharedPreferences

class SharedPrefsHelper {
    companion object {
        private lateinit var sharedPreferences: SharedPreferences

        fun init(context: Context){
            sharedPreferences = context.getSharedPreferences("sp", Context.MODE_PRIVATE)
        }

        fun saveStr(key: SharedKeys, value: String) = sharedPreferences
            .edit()
            .putString(key.name, value)
            .apply()

        fun readStr(key: SharedKeys) = sharedPreferences
            .getString(key.name, "")

        fun saveLong(key: SharedKeys, value: Long) = sharedPreferences
            .edit()
            .putLong(key.name, value)
            .apply()

        fun readLong(key: SharedKeys) = sharedPreferences
            .getLong(key.name, 0)
    }
}

sealed class SharedKeys(val name: String){
    data object Nickname : SharedKeys("nickname")
    data object ProfileId : SharedKeys("profileId")
}