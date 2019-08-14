package com.kikyoung.currency.data.service

import com.kikyoung.currency.data.api.Api
import com.kikyoung.currency.data.model.CurrencyRates
import com.squareup.moshi.Moshi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class CurrencyService(
    moshi: Moshi,
    private val api: Api,
    private val ioDispatcher: CoroutineDispatcher
) : BaseService(moshi, ioDispatcher) {

    suspend fun latest(currencyCode: String): CurrencyRates = withContext(ioDispatcher) {
        execute { api.latest(currencyCode) }
    }
}