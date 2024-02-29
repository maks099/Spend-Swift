package com.spend.swift.helpers

import android.icu.util.Calendar
import com.spend.swift.ui.views.main.lists.TIME
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

fun getTimeByFilterProperty(
    coefficient: Int = 1,
    time: TIME
) = when(time){
    TIME.ALL_TIME -> getTimeByDays(10 * 360 * coefficient)
    TIME.YEAR -> getTimeByDays(365 * coefficient)
    TIME.CVARTAL -> getTimeByDays(90 * coefficient)
    TIME.MONTH -> getTimeByDays(30 * coefficient)
}

private fun getTimeByDays(daysCount: Int): Long{
    val c = Calendar.getInstance()
    c.add(Calendar.DAY_OF_YEAR, daysCount)
    return c.timeInMillis
}