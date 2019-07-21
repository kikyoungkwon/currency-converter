package com.kikyoung.currency.feature.list.model

data class CurrencyRate(
    val code: String,
    val displayName: String,
    val rate: Double,
    val flagUrl: String
)