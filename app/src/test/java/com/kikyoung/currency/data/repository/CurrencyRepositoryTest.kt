package com.kikyoung.currency.data.repository

import com.kikyoung.currency.data.LocalStorage
import com.kikyoung.currency.data.Resource
import com.kikyoung.currency.data.exception.NetworkException
import com.kikyoung.currency.data.mapper.CurrencyMapper
import com.kikyoung.currency.data.model.CurrencyRates
import com.kikyoung.currency.data.repository.CurrencyRepository.Companion.DEFAULT_BASE_CURRENCY_CODE
import com.kikyoung.currency.data.repository.CurrencyRepository.Companion.KEY_BASE_CURRENCY_CODE
import com.kikyoung.currency.data.repository.CurrencyRepository.Companion.KEY_LATEST_RATES
import com.kikyoung.currency.data.service.CurrencyService
import com.kikyoung.currency.feature.list.model.CurrencyList
import com.kikyoung.currency.util.RxImmediateSchedulerRule
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.junit.Rule
import org.junit.Test

class CurrencyRepositoryTest {

    @Rule @JvmField var testSchedulerRule = RxImmediateSchedulerRule()

    private val currencyCode = "GBP"

    private val localStorage = mockk<LocalStorage>(relaxed = true)
    private val currencyMapper = mockk<CurrencyMapper>(relaxed = true)
    private val currencyService = mockk<CurrencyService>(relaxed = true)

    @Test
    fun `when getting latest rates, it should map to currency list`() {
        val currencyRates = mockk<CurrencyRates>()
        every { currencyService.latest(currencyCode) } returns Single.just(currencyRates)
        CurrencyRepository(localStorage, currencyMapper, currencyService, Schedulers.trampoline())
            .latestRates(currencyCode).subscribe()
        verify { currencyMapper.toList(currencyRates) }
    }

    @Test
    fun `when polling latest rates is successful, it should provide the currency list`() {
        val currencyRates = mockk<CurrencyRates>(relaxed = true)
        val currencyList = mockk<CurrencyList>(relaxed = true)
        every { localStorage.get(KEY_LATEST_RATES, CurrencyList::class.java) } returns null
        every { localStorage.get(KEY_BASE_CURRENCY_CODE, String::class.java, any()) } returns DEFAULT_BASE_CURRENCY_CODE
        every { currencyService.latest(currencyCode) } returns Single.just(currencyRates)
        every { currencyMapper.toList(currencyRates) } returns currencyList
        CurrencyRepository(localStorage, currencyMapper, currencyService, Schedulers.trampoline())
            .pollingLatestRates()
            .test()
            .awaitCount(1)
            .assertValue(Resource.Success(currencyList))
            .assertNotComplete()
    }

    @Test
    fun `when polling latest rates throws an exception, it should provide the exception`() {
        val exception = NetworkException("network error")
        every { localStorage.get(KEY_LATEST_RATES, CurrencyList::class.java) } returns null
        every { localStorage.get(KEY_BASE_CURRENCY_CODE, String::class.java, any()) } returns DEFAULT_BASE_CURRENCY_CODE
        every { currencyService.latest(currencyCode) } returns Single.error(exception)
        CurrencyRepository(localStorage, currencyMapper, currencyService, Schedulers.trampoline())
            .pollingLatestRates()
            .test()
            .awaitCount(1)
            .assertValue(Resource.Error(exception))
            .assertNotComplete()
    }
}