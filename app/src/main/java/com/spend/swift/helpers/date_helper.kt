package com.spend.swift.helpers

import android.icu.util.Calendar
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun getTimeMillisNextDay(): Long {
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.DAY_OF_YEAR, 1)
    return calendar.timeInMillis
}

fun Long.asDate(): String{
    val d = Date()
    d.time = this
    return SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(d)
}