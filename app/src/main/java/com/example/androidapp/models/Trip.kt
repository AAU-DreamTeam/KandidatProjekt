package com.example.androidapp.models

import java.text.SimpleDateFormat
import java.util.*

class Trip(val purchases: MutableList<Purchase>) {
    fun prettyTimestamp(): String {
        val sdf = SimpleDateFormat("EEE 'd'. d MMM yyyy", Locale.getDefault())
        var prettyTimestamp = sdf.format(purchases[0].calendar.time)
        val dayStr = prettyTimestamp.substring(0, 3)
        val monthStr = prettyTimestamp.substring(prettyTimestamp.length - 8, prettyTimestamp.length)

        prettyTimestamp = prettyTimestamp.replace(dayStr, toDanish(dayStr)).replace(monthStr, toDanish(monthStr))

        return prettyTimestamp
    }

    private fun toDanish(str: String): String {
        return when(str) {
            "Mon" -> "Man"
            "Tue" -> "Tir"
            "Wed" -> "Ons"
            "Thu" -> "Tor"
            "Fri" -> "Fre"
            "Sat" -> "Lør"
            "Sun" -> "Søn"
            "May" -> "maj"
            "Oct" -> "okt"
            else -> str
        }
    }
}