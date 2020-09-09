package com.example.shareway.utils

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout

suspend fun <T> safeDatabaseCall(
    dispatcher: CoroutineDispatcher,
    cacheCall: suspend () -> T?
//): CacheResult<T?> {
): CacheResult<T?> {
    return withContext(dispatcher) {
        try {
            withTimeout(10000L) {
                CacheResult.Success(cacheCall.invoke())
            }
        } catch (t: Throwable) {
            when (t) {
                is TimeoutCancellationException -> {
                    CacheResult.Error("Timeout error")
                }
                else -> {
                    CacheResult.Error("Unknown error")
                }
            }
        }
    }
}