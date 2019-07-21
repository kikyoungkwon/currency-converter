package com.kikyoung.currency.data.model

data class CurrencyRates(
    val base: String,
    // It could be better to provide exact time to display on screen.
    val date: String,
    val rates: Map<String, Double>
    ) {
    companion object {
        // Backend returns currency rates for default currency code with rate 1.0
        const val baseRate: Double = 1.0
    }
}