package com.example.datingapp.util

import android.net.Uri
import androidx.core.net.toUri
import com.example.datingapp.data.local.Gender
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object Extensions {

    fun String.toGender(): Gender {
        if (this == "0") return Gender.MALE
        if (this == "1") return Gender.FEMALE
        return Gender.NONE
    }

    fun String.firebaseTo(): List<String>{
        return this.dropWhile { !it.isDigit() }
            .dropLastWhile { !it.isDigit() }
            .replace("\\s+".toRegex(), "")
            .split(',')
    }

    fun String.firebaseToImageList(): List<Uri>{
        return this.dropWhile { it != 'h' }
            .dropLastWhile { it == ']'}
            .replace("\\s+".toRegex(), "")
            .split(',')
            .map { it.toUri() }
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