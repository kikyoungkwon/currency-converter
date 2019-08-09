package com.kikyoung.currency.data.repository

import com.kikyoung.currency.data.LocalStorage
import com.kikyoung.currency.data.Resource
import com.kikyoung.currency.data.exception.NetworkException
import com.kikyoung.currency.data.mapper.CurrencyMapper
import com.kikyoung.currency.data.model.CurrencyRates
import com.kikyoung.currency.data.repository.CurrencyRepository.Companion.DEFAULT_BASE_CURRENCY_CODE
import com.kikyoung.currency.data.repository.CurrencyRepository.Companion.DELAY_PULLING_LATEST_RATE
import com.kikyoung.currency.data.repository.CurrencyRepository.Companion.KEY_BASE_CURRENCY_CODE
import com.kikyoung.currency.data.repository.CurrencyRepository.Companion.KEY_LATEST_RATES
import com.kikyoung.currency.data.service.CurrencyService
import com.kikyoung.currency.feature.list.model.CurrencyList
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Single
import io.reactivex.observers.TestObserver
import io.reactivex.schedulers.Schedulers
import io.reactivex.schedulers.TestScheduler
import org.junit.Test
import java.util.concurrent.TimeUnit

class CurrencyRepositoryTest {

    private val currencyCode = DEFAULT_BASE_CURRENCY_CODE

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
    fun `when getting currency list is successful, it should provide the currency list`() {
        val currencyRates = mockk<CurrencyRates>(relaxed = true)
        val currencyList = mockk<CurrencyList>(relaxed = true)
        every { localStorage.get(KEY_LATEST_RATES, CurrencyList::class.java) } returns null
        every { localStorage.get(KEY_BASE_CURRENCY_CODE, String::class.java, any()) } returns currencyCode
        every { currencyService.latest(currencyCode) } returns Single.just(currencyRates)
        every { currencyMapper.toList(currencyRates) } returns currencyList
        CurrencyRepository(localStorage, currencyMapper, currencyService, Schedulers.trampoline())
            .currencyList(currencyCode)
            .test()
            .assertValue(Resource.Success(currencyList))
            .assertComplete()
    }

    @Test
    fun `when getting currency list throws an exception, it should provide the exception`() {
        val exception = NetworkException("network error")
        every { localStorage.get(KEY_LATEST_RATES, CurrencyList::class.java) } returns null
        every { localStorage.get(KEY_BASE_CURRENCY_CODE, String::class.java, any()) } returns currencyCode
        every { currencyService.latest(currencyCode) } returns Single.error(exception)
        CurrencyRepository(localStorage, currencyMapper, currencyService, Schedulers.trampoline())
            .currencyList(currencyCode)
            .test()
            .assertValue(Resource.Error(exception))
            .assertComplete()
    }

    @Test
    fun `when getting currency list is successful and base currency is changed, it should skip the currency list`() {
        val currencyRates = mockk<CurrencyRates>(relaxed = true)
        val currencyList = mockk<CurrencyList>(relaxed = true)
        every { localStorage.get(KEY_LATEST_RATES, CurrencyList::class.java) } returns null
        every { localStorage.get(KEY_BASE_CURRENCY_CODE, String::class.java, any()) } returns "GBP"
        every { currencyService.latest(currencyCode) } returns Single.just(currencyRates)
        every { currencyMapper.toList(currencyRates) } returns currencyList
        CurrencyRepository(localStorage, currencyMapper, currencyService, Schedulers.trampoline())
            .currencyList(currencyCode)
            .test()
            .assertNoValues()
            .assertComplete()
    }

    @Test
    fun `when getting currency list is successful, it should save locally`() {
        val currencyRates = mockk<CurrencyRates>(relaxed = true)
        val currencyList = mockk<CurrencyList>(relaxed = true)
        every { localStorage.get(KEY_LATEST_RATES, CurrencyList::class.java) } returns null
        every { localStorage.get(KEY_BASE_CURRENCY_CODE, String::class.java, any()) } returns currencyCode
        every { currencyService.latest(currencyCode) } returns Single.just(currencyRates)
        every { currencyMapper.toList(currencyRates) } returns currencyList
        CurrencyRepository(localStorage, currencyMapper, currencyService, Schedulers.trampoline())
            .currencyList(currencyCode)
            .test()
        verify { localStorage.put(KEY_LATEST_RATES, currencyList) }
    }

    @Test
    fun `when polling latest rates and if there is a cached currency list, it should provide the cached currency list first`() {
        val currencyRates = mockk<CurrencyRates>(relaxed = true)
        val cachedCurrencyList = mockk<CurrencyList>(relaxed = true)
        val currencyList = mockk<CurrencyList>(relaxed = true)
        every { localStorage.get(KEY_LATEST_RATES, CurrencyList::class.java) } returns cachedCurrencyList
        every { localStorage.get(KEY_BASE_CURRENCY_CODE, String::class.java, any()) } returns currencyCode
        every { currencyService.latest(currencyCode) } returns Single.just(currencyRates)
        every { currencyMapper.toList(currencyRates) } returns currencyList
        val testScheduler = TestScheduler()
        val testObserver = TestObserver<Resource<CurrencyList>>()
        CurrencyRepository(localStorage, currencyMapper, currencyService, testScheduler)
            .pollingLatestRates()
            .subscribe(testObserver)
        testScheduler.advanceTimeBy(DELAY_PULLING_LATEST_RATE, TimeUnit.MILLISECONDS)
        testObserver.assertValueAt(0, Resource.Success(cachedCurrencyList))
        testObserver.assertValueAt(1, Resource.Success(currencyList))
        testObserver.assertNotComplete()
        testObserver.dispose()
    }

    @Test
    fun `when polling latest rates is successful, it should provide the currency list`() {
        val currencyRates = mockk<CurrencyRates>(relaxed = true)
        val currencyList = mockk<CurrencyList>(relaxed = true)
        every { localStorage.get(KEY_LATEST_RATES, CurrencyList::class.java) } returns null
        every { localStorage.get(KEY_BASE_CURRENCY_CODE, String::class.java, any()) } returns currencyCode
        every { currencyService.latest(currencyCode) } returns Single.just(currencyRates)
        every { currencyMapper.toList(currencyRates) } returns currencyList
        val testScheduler = TestScheduler()
        val testObserver = TestObserver<Resource<CurrencyList>>()
        CurrencyRepository(localStorage, currencyMapper, currencyService, testScheduler)
            .pollingLatestRates()
            .subscribe(testObserver)
        testScheduler.advanceTimeBy(DELAY_PULLING_LATEST_RATE, TimeUnit.MILLISECONDS)
        testObserver.assertValueAt(0, Resource.Success(currencyList))
        testObserver.assertNotComplete()
        testObserver.dispose()
    }

    @Test
    fun `when polling latest rates throws an exception, it should provide the exception`() {
        val exception = NetworkException("network error")
        every { localStorage.get(KEY_LATEST_RATES, CurrencyList::class.java) } returns null
        every { localStorage.get(KEY_BASE_CURRENCY_CODE, String::class.java, any()) } returns currencyCode
        every { currencyService.latest(currencyCode) } returns Single.error(exception)
        val testScheduler = TestScheduler()
        val testObserver = TestObserver<Resource<CurrencyList>>()
        CurrencyRepository(localStorage, currencyMapper, currencyService, testScheduler)
            .pollingLatestRates()
            .subscribe(testObserver)
        testScheduler.advanceTimeBy(DELAY_PULLING_LATEST_RATE, TimeUnit.MILLISECONDS)
        testObserver.assertValueAt(0, Resource.Error(exception))
        testObserver.assertNotComplete()
        testObserver.dispose()
    }
}