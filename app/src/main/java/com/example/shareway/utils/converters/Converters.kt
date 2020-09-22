package com.example.shareway.utils.converters

import androidx.room.TypeConverter
import java.time.Instant

class Converters {

    @TypeConverter
    fun fromInstant(date: Instant?): Long? = date?.toEpochMilli()

    @TypeConverter
    fun toInstant(millisSinceEpoch: Long?): Instant? = millisSinceEpoch?.let {
        Instant.ofEpochMilli(it)
    }
}