package com.kikyoung.currency.data.service

import com.kikyoung.currency.data.api.Api
import com.kikyoung.currency.data.model.CurrencyRates
import io.reactivex.Single

class CurrencyService(
    private val api: Api
) : BaseService() {

    fun latest(currencyCode: String): Single<CurrencyRates> = api.latest(currencyCode)
}