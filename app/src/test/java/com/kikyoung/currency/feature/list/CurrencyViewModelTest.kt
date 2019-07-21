package com.kikyoung.currency.feature.list

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.kikyoung.currency.data.Resource
import com.kikyoung.currency.data.exception.NetworkException
import com.kikyoung.currency.data.exception.ServerException
import com.kikyoung.currency.data.repository.CurrencyRepository
import com.kikyoung.currency.feature.list.model.CurrencyList
import io.mockk.*
import junit.framework.TestCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import java.net.UnknownHostException

@ExperimentalCoroutinesApi
class CurrencyViewModelTest {

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    private var currencyRepository: CurrencyRepository = mockk(relaxed = true)
    private var viewModel = CurrencyViewModel(currencyRepository, Dispatchers.Unconfined)

    @Test
    fun `when polling currency list, it should show and hide the loading bar`() {
        val latestRatesLiveData = MutableLiveData<Resource<CurrencyList>>()
        every { currencyRepository.latestRatesLiveData() } returns latestRatesLiveData
        val observer = mockk<Observer<Boolean>>(relaxed = true)
        viewModel.loadingLiveData().observeForever(observer)
        viewModel.startPollingLatestRate()
        latestRatesLiveData.postValue(Resource.Success(mockk()))
        verifySequence {
            observer.onChanged(true)
            observer.onChanged(false)
        }
    }

    @Test
    fun `when starts polling currency list, it should start polling it`() {
        viewModel.startPollingLatestRate()
        coVerify { currencyRepository.pollingLatestRates() }
    }

    @Test
    fun `when getting currency list is successful, it should provide it`() {
        val latestRatesLiveData = MutableLiveData<Resource<CurrencyList>>()
        every { currencyRepository.latestRatesLiveData() } returns latestRatesLiveData
        val observer = mockk<Observer<CurrencyList>>(relaxed = true)
        viewModel.currencyListLiveData().observeForever(observer)
        viewModel.startPollingLatestRate()
        val currencyList = mockk<CurrencyList>()
        latestRatesLiveData.postValue(Resource.Success(currencyList))
        verify {
            observer.onChanged(currencyList)
        }
    }

    @Test
    fun `when getting currency list is unsuccessful with a server error, it should show the error`() {
        val latestRatesLiveData = MutableLiveData<Resource<CurrencyList>>()
        every { currencyRepository.latestRatesLiveData() } returns latestRatesLiveData
        val observer = mockk<Observer<ServerException>>(relaxed = true)
        viewModel.serverErrorLiveData().observeForever(observer)
        viewModel.startPollingLatestRate()
        val serverException = mockk<ServerException>()
        latestRatesLiveData.postValue(Resource.Error(serverException))
        verify {
            observer.onChanged(serverException)
        }
    }

    @Test
    fun `when getting currency list is unsuccessful with a network error, it should show the error`() {
        val latestRatesLiveData = MutableLiveData<Resource<CurrencyList>>()
        every { currencyRepository.latestRatesLiveData() } returns latestRatesLiveData
        val observer = mockk<Observer<NetworkException>>(relaxed = true)
        viewModel.networkErrorLiveData().observeForever(observer)
        viewModel.startPollingLatestRate()
        val message = "unknownHostException"
        val networkException = UnknownHostException(message)
        latestRatesLiveData.postValue(Resource.Error(networkException))
        val slot = slot<NetworkException>()
        verify {
            observer.onChanged(capture(slot))
        }
        TestCase.assertEquals(slot.captured.message, message)
    }

    @Test
    fun `when setting a base currency code, it should set it`() {
        var currencyCode = "EUR"
        viewModel.setBaseCurrencyCode(currencyCode)
        verify { currencyRepository.setBaseCurrencyCode(currencyCode) }
    }
}