package com.example.datingapp.util

import androidx.room.TypeConverter

class Converters {

    @TypeConverter
    fun fromListOfInt(interestedGender: List<Int>): String{
        return interestedGender.toString()
    }

    @TypeConverter
    fun toListOfInt(interestedGender: String): List<Int>{
        if (interestedGender == "[]") return emptyList()
        return interestedGender
            .drop(1)
            .dropLast(1)
            .replace(" ", "")
            .split(',')
            .map { it.toInt() }
    }

    @TypeConverter
    fun fromListOfDouble(interestedGender: List<Double>): String{
        return interestedGender.toString()
    }

    @TypeConverter
    fun toListOfDouble(interestedGender: String): List<Double>{
        if (interestedGender == "[]") return emptyList()
        return interestedGender
            .drop(1)
            .dropLast(1)
            .replace(" ", "")
            .split(',')
            .map { it.toDouble() }
    }
}