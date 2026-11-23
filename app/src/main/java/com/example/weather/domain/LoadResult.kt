package com.example.weather.domain

sealed class LoadResult<out T> {
    data class Success<T>(val data: T) : LoadResult<T>()
    data class Failure(val reason: FailureReason) : LoadResult<Nothing>()
}
