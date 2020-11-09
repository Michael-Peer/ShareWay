package com.example.shareway.listeners

import com.example.shareway.utils.MessageType
import com.example.shareway.utils.UIComponentType

interface UICommunicationListener {

    fun displayProgressBar(isLoading: Boolean)
    fun displayProgressIndicator(progress: Int)


//    fun onResponseReceived(
//        message: String,
//        uiComponentType: UIComponentType
//    )
//
//    abstract fun displayDialog(message: String)

    fun onResponseReceived(
        response: Response
    )
}

data class Response(
    val title: String? = "INFO",
    val message: String?,
    val uiComponentType: UIComponentType
//    val messageType: MessageType //will ne useful for example different color etc,
)



//
//    fun onResponseReceived(
//        response: Response
//    )

