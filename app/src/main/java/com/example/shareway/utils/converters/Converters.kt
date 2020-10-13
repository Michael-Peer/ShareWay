package com.example.shareway.utils.converters

import androidx.room.TypeConverter
import java.time.Instant
import java.time.LocalDateTime

class Converters {

    @TypeConverter
    fun fromInstant(date: Instant?): Long? = date?.toEpochMilli()

    @TypeConverter
    fun toInstant(millisSinceEpoch: Long?): Instant? = millisSinceEpoch?.let {
        Instant.ofEpochMilli(it)
    }

    @TypeConverter
    fun fromDateTimeToString(date: LocalDateTime?): String? = date?.toString()

    @TypeConverter
    fun fromStringToDateTime(date: String?): LocalDateTime? = LocalDateTime.parse(date)
}