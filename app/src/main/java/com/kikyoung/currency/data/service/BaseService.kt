package com.kikyoung.currency.data.service

import com.kikyoung.currency.data.exception.ServerException
import com.squareup.moshi.Moshi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import retrofit2.Response

open class BaseService(private val moshi: Moshi, private val ioDispatcher: CoroutineDispatcher) {

    protected suspend fun <T> execute(
        handleError: ((response: Response<T>) -> Unit)? = null,
        serviceCall: suspend () -> Response<T>
    ): T = withContext(ioDispatcher) {
        val response: Response<T> = serviceCall.invoke()
        if (!response.isSuccessful) {
            handleError?.invoke(response)
            throw moshi.adapter(ServerException::class.java).fromJson(response.errorBody().toString())
                ?: ServerException("empty error body")
        }
        response.body() ?: throw ServerException("empty response body")
    }
}