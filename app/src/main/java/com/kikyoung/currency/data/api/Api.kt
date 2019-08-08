package com.kikyoung.currency.data.api

import com.kikyoung.currency.data.model.CurrencyRates
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface Api {

    @GET("latest")
    fun latest(@Query("base") currencyCode: String): Single<CurrencyRates>
}