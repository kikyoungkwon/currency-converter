package com.kikyoung.currency.data.api

import com.kikyoung.currency.data.model.CurrencyRates
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface Api {

    @GET("latest")
    suspend fun latest(@Query("base") currencyCode: String): Response<CurrencyRates>
}