package com.kikyoung.currency.feature.list.model

data class CurrencyList(
    val date: String,
    val rates: List<CurrencyRate>
)