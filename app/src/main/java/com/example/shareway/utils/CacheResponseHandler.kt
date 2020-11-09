package com.example.shareway.utils

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

//abstract class CacheResponseHandler<ViewState, Data>(
//    private val response: CacheResult<Data?>
//) {
//    suspend fun getResult(): ViewState {
//
//        return when (response) {
//
//            is CacheResult.Error -> {
////                DataState.error(
////                    response = Response(
////                        message = response.errorMessage,
////                        uiComponentType = UIComponentType.Toast
////                    )
////                )
//
//                handleError(
//                    errorMessage = response.errorMessage ?: "UnknownError",
//                    uiComponentType = UIComponentType.Dialog
//                )
//            }
//
//            is CacheResult.Success -> {
//                if (response.value == null) {
////                    DataState.error(
////                        response = Response(
////                            message = "Data is null or empty",
////                            uiComponentType = UIComponentType.Toast
////                        )
////                    )
//                    handleError(
//                        errorMessage = "Data is null or empty",
//                        uiComponentType = UIComponentType.Dialog
//                    )
//                } else {
//                    handleSuccess(resultObj = response.value)
//                }
//            }
//
//        }
//    }
//
//    abstract fun handleError(
//        errorMessage: String,
//        uiComponentType: UIComponentType.Dialog
//    ): ViewState
//
//
//    abstract suspend fun handleSuccess(resultObj: Data): ViewState
//
//}

//abstract class CacheResponseHandler<ViewState, Data>(
//    private val response: CacheResult<Flow<Data?>?>
//) {
//    companion object {
//        private const val TAG = "CacheResponseHandler"
//    }
//
//    suspend fun getResult(): ViewState {
//
//        Log.d(TAG, "getResult: inside fun main")
//
//        return when (response) {
//
//            is CacheResult.Error -> {
//                Log.d(TAG, "getResult: inside fun error")
//
////                DataState.error(
////                    response = Response(
////                        message = response.errorMessage,
////                        uiComponentType = UIComponentType.Toast
////                    )
////                )
//                handleError(
//                    errorMessage = response.errorMessage ?: "UnknownError",
//                    uiComponentType = UIComponentType.Dialog
//                )
//            }
//
//            is CacheResult.Success -> {
////                Log.d(TAG, "getResult: inside fun success")
//
////                var data: Data? = null
////                val value = response.value
//
//
////                value!!.collect {
////                    Log.d(TAG, "getResult: sdata $it")
////                    data = it!!
////                    return@collect
////                }
//
////                Log.d(TAG, "getResult: before")
////                CoroutineScope(Dispatchers.IO).launch{
////                    value?.collect {
////                        it?.let { da ->
////                            data = da
////                        }
////                    }
////                }
////                Log.d(TAG, "getResult: after")
////
////                data?.let {
////                    handleSuccess(data!!)
////                } ?: handleError("affafa", UIC)
//
////                handleSuccess(data!!)
//
////                data?.let {
////                    handleSuccess(data!!)
////                } ?: handleError("adda", UIComponentType.Dialog)
//
//
////                Log.d(TAG, "getResult: oustside collect data $data")
//
//
////                data?.let {
////                    handleSuccess(resultObj = it)
////                } ?: handleError(
////                    errorMessage = "Data is null or empty",
////                    uiComponentType = UIComponentType.Dialog
////                )
//            }
//
//
//        }
//
//    }

//
//    abstract fun handleError(
//        errorMessage: String,
//        uiComponentType: UIComponentType
//    ): ViewState
//
//
//    abstract suspend fun handleSuccess(resultObj: Data): ViewState
//
//}