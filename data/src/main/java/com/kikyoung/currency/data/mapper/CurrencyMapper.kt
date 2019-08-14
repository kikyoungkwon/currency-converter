package com.kikyoung.currency.data.mapper

import com.kikyoung.currency.data.model.CurrencyRates
import com.kikyoung.currency.feature.list.model.CurrencyList
import com.kikyoung.currency.feature.list.model.CurrencyRate
import com.kikyoung.currency.util.CurrencyUtil
import org.joda.money.CurrencyUnit
import java.text.SimpleDateFormat
import java.util.*

class CurrencyMapper {

    fun toList(currencyRates: CurrencyRates): CurrencyList {
        val rates = mutableListOf(
            CurrencyRate(
                currencyRates.base,
                CurrencyUtil.displayName(currencyRates.base),
                CurrencyRates.baseRate,
                CurrencyUtil.flagUrl(currencyRates.base)
            )
        )
        rates.addAll(currencyRates.rates.map { (code, rate) ->
            CurrencyRate(
                code,
                CurrencyUnit.of(code).toCurrency().displayName,
                rate,
                CurrencyUtil.flagUrl(code)
            )
        })
        return CurrencyList(
            // TODO Show this in the header of recycler view so that user can know when currency rates are for
            SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date()),
            rates
        )
    }
}