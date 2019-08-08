package com.kikyoung.currency.feature.list

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.kikyoung.currency.data.Resource
import com.kikyoung.currency.data.exception.NetworkException
import com.kikyoung.currency.data.exception.ServerException
import com.kikyoung.currency.data.repository.CurrencyRepository
import com.kikyoung.currency.feature.list.model.CurrencyList
import io.mockk.*
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import junit.framework.TestCase
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import java.net.UnknownHostException

class CurrencyViewModelTest {

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    private var currencyRepository: CurrencyRepository = mockk(relaxed = true)
    private var viewModel = CurrencyViewModel(currencyRepository, Schedulers.trampoline())

    @Test
    fun `when polling currency list, it should show and hide the loading bar`() {
        val observable = Observable.just<Resource<CurrencyList>>(mockk())
        every { currencyRepository.pollingLatestRates() } returns observable
        val observer = mockk<Observer<Boolean>>(relaxed = true)
        viewModel.loadingLiveData().observeForever(observer)
        viewModel.startPollingLatestRate()
        verifySequence {
            observer.onChanged(true)
            observer.onChanged(false)
        }
    }

    @Test
    fun `when starts polling currency list, it should start polling it`() {
        viewModel.startPollingLatestRate()
        verify(exactly = 1) { currencyRepository.pollingLatestRates() }
    }

    @Test
    fun `when getting currency list is successful, it should provide it`() {
        val currencyList = mockk<CurrencyList>()
        val observable = Observable.just<Resource<CurrencyList>>(Resource.Success(currencyList))
        every { currencyRepository.pollingLatestRates() } returns observable
        val observer = mockk<Observer<CurrencyList>>(relaxed = true)
        viewModel.currencyListLiveData().observeForever(observer)
        viewModel.startPollingLatestRate()
        verify(exactly = 1) {
            observer.onChanged(currencyList)
        }
    }

    @Test
    fun `when getting currency list is unsuccessful with a server error, it should show the error`() {
        val message = "message"
        val serverException = ServerException(message)
        val observable = Observable.just<Resource<CurrencyList>>(Resource.Error(serverException))
        every { currencyRepository.pollingLatestRates() } returns observable
        val observer = mockk<Observer<ServerException>>(relaxed = true)
        viewModel.serverErrorLiveData().observeForever(observer)
        viewModel.startPollingLatestRate()
        val slot = slot<ServerException>()
        verify(exactly = 1) {
            observer.onChanged(capture(slot))
        }
        TestCase.assertEquals(slot.captured.message, message)
    }

    @Test
    fun `when getting currency list is unsuccessful with a network error, it should show the error`() {
        val message = "unknownHostException"
        val networkException = UnknownHostException(message)
        val observable = Observable.just<Resource<CurrencyList>>(Resource.Error(networkException))
        every { currencyRepository.pollingLatestRates() } returns observable
        val observer = mockk<Observer<NetworkException>>(relaxed = true)
        viewModel.networkErrorLiveData().observeForever(observer)
        viewModel.startPollingLatestRate()
        val slot = slot<NetworkException>()
        verify(exactly = 1) {
            observer.onChanged(capture(slot))
        }
        TestCase.assertEquals(slot.captured.message, message)
    }

    @Test
    fun `when setting a base currency code, it should set it`() {
        val currencyCode = "EUR"
        viewModel.setBaseCurrencyCode(currencyCode)
        verify(exactly = 1) { currencyRepository.setBaseCurrencyCode(currencyCode) }
    }
}