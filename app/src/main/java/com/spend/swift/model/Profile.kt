package com.spend.swift.model

import java.util.Date
import java.util.UUID

data class Profile(
    val email: String = "",
    val password: String = "",
    val id: Long = Date().time
){
    fun toMap(): MutableMap<String, Any>{
        return mutableMapOf(
            "email" to email,
            "password" to password,
            "id" to id
        )
    }
}
