package com.kikyoung.currency.data

sealed class Resource<out T> {
    data class Success<out T>(val data: T) : Resource<T>()
    data class Error<out T>(val t: Throwable) : Resource<T>()
}