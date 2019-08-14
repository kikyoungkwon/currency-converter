package com.kikyoung.currency.data.mapper

import com.kikyoung.currency.data.model.CurrencyRates
import org.junit.Before
import org.junit.Test
import java.util.*
import kotlin.test.assertEquals

class CurrencyMapperTest {

    private var mapper = CurrencyMapper()

    @Before
    fun before() {
        Locale.setDefault(Locale.UK)
    }

    @Test
    fun `when mapping currency rates to list, it should map correctly`() {
        val baseCode = "GBP"
        val baseRate = CurrencyRates.baseRate
        val code0 = "EUR"
        val rate0 = 2.0
        val code1 = "USD"
        val rate1 = 3.0

        val currencyRates = CurrencyRates(
            baseCode,
            "2018-09-06",
            mapOf(code0 to rate0, code1 to rate1)
        )
        val currencyList = mapper.toList(currencyRates)

        assertEquals(currencyRates.rates.size + 1, currencyList.rates.size)
        assertEquals(currencyRates.base, currencyList.rates[0].code)
        assertEquals(baseRate, currencyList.rates[0].rate)
        assertEquals("British Pound Sterling", currencyList.rates[0].displayName)
        assertEquals(code0, currencyList.rates[1].code)
        assertEquals(currencyRates.rates[code0], currencyList.rates[1].rate)
        assertEquals("Euro", currencyList.rates[1].displayName)
        assertEquals(code1, currencyList.rates[2].code)
        assertEquals(currencyRates.rates[code1], currencyList.rates[2].rate)
        assertEquals("US Dollar", currencyList.rates[2].displayName)
    }
}