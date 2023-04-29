package com.example.datingapp.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object Extensions {

    fun Long.calculateDate(): String?{
        val now = System.currentTimeMillis()
        val difference = now - this

        if (difference / (1000 * 3600 * 24) > 7)return null
        if (difference / (1000 * 3600 * 24) > 1) return "active recently"
        if (difference / (1000 * 3600) > 1) return "active ${difference / (1000 * 3600)} hour ago"
        return "active less than hour ago"
    }

    fun String.calculateAge(): String {
        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val date = formatter.parse(this)
        val calendarBirth = Calendar.getInstance()
        calendarBirth.time = date!!
        val calendarNow = Calendar.getInstance()
        var age = calendarNow.get(Calendar.YEAR) - calendarBirth.get(Calendar.YEAR)
        if (calendarNow.get(Calendar.DAY_OF_YEAR) < calendarBirth.get(Calendar.DAY_OF_YEAR)) {
            age--
        }
        return age.toString()
    }

    fun String.fixName(): String =
        this.lowercase()
            .replaceFirstChar { it.uppercase() }
}