package com.kikyoung.currency.util

import org.joda.money.CurrencyUnit
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*

object CurrencyUtil {

    // Better to be provided from backend
    private const val FORMAT_FLAG_URL = "https://www.countryflags.io/%s/shiny/64.png"

    @JvmStatic
    fun format(code: String, amount: Double): String {
        val format = NumberFormat.getCurrencyInstance()
        format.minimumFractionDigits = 0
        format.currency = Currency.getInstance(code)
        format.isGroupingUsed = false
        val decimalFormatSymbols = (format as DecimalFormat).getDecimalFormatSymbols()
        decimalFormatSymbols.currencySymbol = ""
        (format).decimalFormatSymbols = decimalFormatSymbols
        return format.format(amount)
    }

    /**
     * Returns country code of country flagUrl for https://www.countryflags.io/
     */
    @JvmStatic
    fun countryCode(code: String): String? = code.substring(0, code.length - 1).toLowerCase()

    @JvmStatic
    fun displayName(code: String): String = CurrencyUnit.of(code).toCurrency().displayName

    @JvmStatic
    fun flagUrl(code: String): String = String.format(FORMAT_FLAG_URL, countryCode(code))
}