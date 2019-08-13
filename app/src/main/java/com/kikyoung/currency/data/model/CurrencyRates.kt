package com.kikyoung.currency.data.model

import com.squareup.moshi.Json

data class CurrencyRates(
    @field:Json(name = "base")
    val base: String,
    // It could be better to provide exact time to display on screen.
    @field:Json(name = "date")
    val date: String,
    @field:Json(name = "rates")
    val rates: Map<String, Double>
    ) {
    companion object {
        // Backend returns currency rates for default currency code with rate 1.0
        const val baseRate: Double = 1.0
    }
}