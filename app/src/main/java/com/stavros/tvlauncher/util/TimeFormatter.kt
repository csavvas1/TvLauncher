package com.stavros.tvlauncher.util

import java.text.SimpleDateFormat
import java.util.*

object TimeFormatter {
    fun formatTime(cal: Calendar = Calendar.getInstance(), use24h: Boolean = true): String {
        val pattern = if (use24h) "HH:mm" else "hh:mm"
        return SimpleDateFormat(pattern, Locale.getDefault()).format(cal.time)
    }

    fun formatDate(cal: Calendar = Calendar.getInstance()): String {
        val day = SimpleDateFormat("EEEE", Locale.ENGLISH).format(cal.time)
        val num = cal.get(Calendar.DAY_OF_MONTH)
        val month = SimpleDateFormat("MMMM", Locale.ENGLISH).format(cal.time)
        val year = cal.get(Calendar.YEAR)
        return "$day $num $month $year"
    }
}
