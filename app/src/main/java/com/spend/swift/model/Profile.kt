package com.spend.swift.model

import com.spend.swift.helpers.md5
import java.util.Date
import java.util.UUID

data class Profile(
    val email: String = "",
    val password: String = "",
    val id: String = ""
){
    fun toMap(): MutableMap<String, Any>{
        return mutableMapOf(
            "email" to email,
            "password" to password.md5(),
        )
    }
}
