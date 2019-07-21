package com.kikyoung.currency.data.service

import com.google.gson.Gson
import com.kikyoung.currency.data.api.Api
import com.kikyoung.currency.data.model.CurrencyRates
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class CurrencyService(
    gson: Gson,
    private val api: Api,
    private val ioDispatcher: CoroutineDispatcher
) : BaseService(gson, ioDispatcher) {

    suspend fun latest(currencyCode: String): CurrencyRates = withContext(ioDispatcher) {
        execute { api.latest(currencyCode) }
    }
}