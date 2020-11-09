package com.example.shareway.listeners

import java.time.Instant

interface ReminderListeners {
    fun onSuccess(reminder: Instant, hour: Int, minute: Int, day:Int)
}