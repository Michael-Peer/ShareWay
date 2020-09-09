package com.example.shareway.listeners

import com.example.shareway.utils.UIComponentType

interface UICommunicationListener {

    fun displayProgressBar(isLoading: Boolean)

    fun onResponseReceived(
        message: String,
        uiComponentType: UIComponentType
    )
}