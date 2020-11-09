package com.example.shareway.utils.converters

import androidx.room.TypeConverter
import com.example.shareway.utils.modes.FilterMode
import java.time.Instant
import java.time.LocalDateTime

class Converters {

    /**
     *
     * In Database, it's recommended to save date/time as timestamp
     *
     * **/


    @TypeConverter
    fun fromInstant(date: Instant?): Long? = date?.toEpochMilli()

    @TypeConverter
    fun toInstant(millisSinceEpoch: Long?): Instant? = millisSinceEpoch?.let {
        Instant.ofEpochMilli(it)
    }

    @TypeConverter
    fun fromFilterMode(filterMode: FilterMode): Int {
        when (filterMode) {
            FilterMode.ALL -> {
                return 0
            }
            FilterMode.ALREADY_READ -> {
                return 1
            }
            FilterMode.NOT_READ -> {
                return 2
            }
        }
    }

    @TypeConverter
    fun toFilterMode(filterMode: Int): FilterMode {
        when (filterMode) {
            0 -> {
                return FilterMode.ALL
            }
            1 -> {
            return FilterMode.ALREADY_READ
        }
            2 -> {
            return FilterMode.NOT_READ
        }
        }
        return FilterMode.ALL
    }

    @TypeConverter
    fun fromDateTimeToString(date: LocalDateTime?): String? = date?.toString()

    @TypeConverter
    fun fromStringToDateTime(date: String?): LocalDateTime? = LocalDateTime.parse(date)
}