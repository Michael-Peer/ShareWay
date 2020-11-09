package com.example.shareway.utils

import com.example.shareway.listeners.ReminderDialogCallbacks
import com.example.shareway.utils.extensions.AreYouSureCallback


data class StateMessage(val response: Response)

data class Response(
    val message: String?,
    val uiComponentType: UIComponentType
)

sealed class UIComponentType{

    object Toast: UIComponentType()

    object Dialog: UIComponentType()

    class ReminderDialog(
        val callbacks: ReminderDialogCallbacks
    ): UIComponentType()

    class AreYouSureDialog(
        val callback: AreYouSureCallback
    ): UIComponentType()

    object None: UIComponentType()
}

sealed class MessageType{

    object Success: MessageType()

    object Error: MessageType()

    object Info: MessageType()

    object None: MessageType()
}


interface StateMessageCallback{

    fun removeMessageFromStack()
}