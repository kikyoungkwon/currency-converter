package com.kikyoung.currency.feature.list

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.kikyoung.currency.data.repository.CurrencyRepository
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

@ExperimentalCoroutinesApi
class CurrencyViewModelTest {

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    private var currencyRepository: CurrencyRepository = mockk(relaxed = true)
    private var viewModel = CurrencyViewModel(currencyRepository, Dispatchers.Unconfined)

    @Test
    fun `when polling currency list, it should show and hide the loading bar`() {
        // TODO Use Coroutines channel
    }

    @Test
    fun `when starts polling currency list, it should start polling it`() {
        viewModel.startPollingLatestRate()
        coVerify { currencyRepository.pollingLatestRates() }
    }

    @Test
    fun `when getting currency list is successful, it should provide it`() {
        // TODO Use Coroutines channel
    }

    @Test
    fun `when getting currency list is unsuccessful with a server error, it should show the error`() {
        // TODO Use Coroutines channel
    }

    @Test
    fun `when getting currency list is unsuccessful with a network error, it should show the error`() {
        // TODO Use Coroutines channel
    }

    @Test
    fun `when setting a base currency code, it should set it`() {
        var currencyCode = "EUR"
        viewModel.setBaseCurrencyCode(currencyCode)
        verify { currencyRepository.setBaseCurrencyCode(currencyCode) }
    }
}