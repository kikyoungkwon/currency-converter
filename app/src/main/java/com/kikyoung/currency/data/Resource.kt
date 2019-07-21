package com.kikyoung.currency.data

sealed class Resource<T> {
    data class Success<T>(val data: T) : Resource<T>()
    data class Error<T>(val e: Exception) : Resource<T>()
}