package com.kikyoung.currency.data.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.kikyoung.currency.data.LocalStorage
import com.kikyoung.currency.data.Resource
import com.kikyoung.currency.data.exception.NetworkException
import com.kikyoung.currency.data.mapper.CurrencyMapper
import com.kikyoung.currency.data.model.CurrencyRates
import com.kikyoung.currency.data.repository.CurrencyRepository.Companion.DEFAULT_BASE_CURRENCY_CODE
import com.kikyoung.currency.data.repository.CurrencyRepository.Companion.DELAY_PULLING_LATEST_RATE
import com.kikyoung.currency.data.repository.CurrencyRepository.Companion.KEY_BASE_CURRENCY_CODE
import com.kikyoung.currency.data.service.CurrencyService
import com.kikyoung.currency.feature.list.model.CurrencyList
import io.mockk.*
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.*
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
        val currencyRates = mockk<CurrencyRates>(relaxed = true)
        val currencyList = mockk<CurrencyList>(relaxed = true)
        every { localStorage.get(CurrencyRepository.KEY_LATEST_RATES, CurrencyList::class.java) } returns currencyList
        every {
            localStorage.get(
                KEY_BASE_CURRENCY_CODE, String::class.java,
                any()
            )
        } returns DEFAULT_BASE_CURRENCY_CODE
        coEvery { currencyService.latest(currencyCode) } returns currencyRates
        every { currencyMapper.toList(currencyRates) } returns currencyList
        val currencyRepository = CurrencyRepository(localStorage, currencyMapper, currencyService, ioDispatcher)
        val job = GlobalScope.launch {
            currencyRepository.pollingLatestRates()
        }
        delay(DELAY_PULLING_LATEST_RATE * 2)
        job.cancel()
        val observer = mockk<Observer<Resource<CurrencyList>>>(relaxed = true)
        currencyRepository.latestRatesLiveData().observeForever(observer)
        val slot = slot<Resource<CurrencyList>>()
        verify(exactly = 1) {
            observer.onChanged(capture(slot))
        }
        assertEquals((slot.captured as Resource.Success).data, currencyList)
    }

    @Test
    @Ignore // Fix ClassCastException
    fun `when polling latest rates throws an exception, it should throw it`() = runBlocking {
        val exception = NetworkException("network error")
        every { localStorage.get(CurrencyRepository.KEY_LATEST_RATES, CurrencyList::class.java) } returns null
        every {
            localStorage.get(
                KEY_BASE_CURRENCY_CODE, String::class.java,
                any()
            )
        } returns DEFAULT_BASE_CURRENCY_CODE
        coEvery { currencyService.latest(currencyCode) } throws exception
        val currencyRepository = CurrencyRepository(localStorage, currencyMapper, currencyService, ioDispatcher)
        val job = GlobalScope.launch {
            currencyRepository.pollingLatestRates()
        }
        delay(DELAY_PULLING_LATEST_RATE * 2)
        job.cancel()
        val observer = mockk<Observer<Resource<CurrencyList>>>(relaxed = true)
        currencyRepository.latestRatesLiveData().observeForever(observer)
        val slot = slot<Resource<CurrencyList>>()
        verify(exactly = 1) {
            observer.onChanged(capture(slot))
        }
        assertEquals((slot.captured as Resource.Error).e, exception)
    }
}