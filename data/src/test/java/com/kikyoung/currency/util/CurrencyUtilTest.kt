package com.kikyoung.currency.util

import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import java.util.*
import kotlin.test.assertEquals

class CurrencyUtilTest {

    @Before
    fun before() {
        Locale.setDefault(Locale.UK)
    }

    @Test
    fun `when formatting currency amount, it should return correctly`() = runBlocking {
        assertEquals("1", CurrencyUtil.format("EUR", 1.0))
        assertEquals("1.25", CurrencyUtil.format("EUR", 1.25))
        assertEquals("1.12", CurrencyUtil.format("EUR", 1.123456))
        assertEquals("123456.1", CurrencyUtil.format("EUR", 123456.1))
    }

    @Test
    fun `when converting to country code, it should return correctly`() = runBlocking {
        assertEquals("eu", CurrencyUtil.countryCode("EUR"))
    }

    @Test
    fun `when getting display name, it should return correctly`() = runBlocking {
        assertEquals("Euro", CurrencyUtil.displayName("EUR"))
    }

    @Test
    fun `when getting flag URL, it should return correctly`() = runBlocking {
        assertEquals("https://www.countryflags.io/eu/shiny/64.png", CurrencyUtil.flagUrl("EUR"))
    }
}