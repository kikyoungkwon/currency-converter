package com.kikyoung.currency.data

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.kikyoung.currency.base.BaseKoinTest
import com.kikyoung.currency.feature.list.model.CurrencyList
import com.kikyoung.currency.feature.list.model.CurrencyRate
import com.squareup.moshi.Moshi
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LocalStorageTest : BaseKoinTest() {

    private val key = "currencyList"
    private lateinit var localStorage: LocalStorage

    @Before
    fun before() {
        localStorage = LocalStorage(InstrumentationRegistry.getInstrumentation().context, Moshi.Builder().build())
    }

    @Test
    fun `when after saving currency list, it should return a not null value`() {
        val savedCurrencyList = getCurrencyList()
        localStorage.put(key, CurrencyList::class.java, savedCurrencyList)
        val currencyList = localStorage.get(key, CurrencyList::class.java)
        assertEquals(savedCurrencyList.rates.size, currencyList?.rates?.size)
        assertEquals(savedCurrencyList.rates[0].code, currencyList?.rates?.get(0)?.code)
    }

    @Test
    fun `when after clearing currency list, it should return a null value`() {
        val savedCurrencyList = getCurrencyList()
        localStorage.put(key, CurrencyList::class.java, savedCurrencyList)
        localStorage.put(key, String::class.java, null)
        assertNull(localStorage.get(key, CurrencyList::class.java))
    }

    @Test
    fun `when a key does not exist, it should return null`() {
        assertNull(localStorage.get("${System.currentTimeMillis()}", CurrencyList::class.java))
    }

    @Test
    fun `when the data model is changed, it should return null`() {
        localStorage.put(key, CurrencyList::class.java, getCurrencyList())
        assertNull(localStorage.get(key, Int::class.java))
    }

    private fun getCurrencyList(): CurrencyList {
        return CurrencyList(
            "date", listOf(
                CurrencyRate("code1", "displayName1", 1.0, "flagUrl1"),
                CurrencyRate("code2", "displayName2", 2.0, "flagUrl2")
            )
        )
    }
}