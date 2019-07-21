package com.kikyoung.currency.data.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.kikyoung.currency.data.LocalStorage
import com.kikyoung.currency.data.mapper.CurrencyMapper
import com.kikyoung.currency.data.model.CurrencyRates
import com.kikyoung.currency.data.service.CurrencyService
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

class CurrencyRepositoryTest {

    @Rule
    @JvmField
    var rule: TestRule = InstantTaskExecutorRule()

    private val currencyCode = "GBP"

    private val localStorage = mockk<LocalStorage>(relaxed = true)
    private val currencyMapper = mockk<CurrencyMapper>(relaxed = true)
    private val currencyService = mockk<CurrencyService>(relaxed = true)
    @ExperimentalCoroutinesApi
    private val ioDispatcher = Dispatchers.Unconfined

    @Test
    fun `when getting latest rates, it should map to currency list`() = runBlocking {
        val currencyRates = mockk<CurrencyRates>()
        coEvery { currencyService.latest(currencyCode) } returns currencyRates
        val currencyRepository = CurrencyRepository(localStorage, currencyMapper, currencyService, ioDispatcher)
        currencyRepository.latestRates(currencyCode)
        verify { currencyMapper.toList(currencyRates) }
    }

    @Test
    @Ignore // Fix ClassCastException
    fun `when polling latest rates is successful, it should provide the currency list`() = runBlocking {
        // TODO Use Coroutines channel
    }

    @Test
    @Ignore // Fix ClassCastException
    fun `when polling latest rates throws an exception, it should throw it`() = runBlocking {
        // TODO Use Coroutines channel
    }
}