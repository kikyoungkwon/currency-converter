package com.kikyoung.currency.base

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.MutableLiveData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kikyoung.currency.R
import com.kikyoung.currency.data.exception.NetworkException
import com.kikyoung.currency.data.exception.ServerException
import com.kikyoung.currency.feature.list.CurrencyListFragment
import com.kikyoung.currency.feature.list.CurrencyViewModel
import io.mockk.every
import io.mockk.mockk
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.test.mock.declare

@RunWith(AndroidJUnit4::class)
class BaseFragmentTest : BaseKoinTest() {

    private var serverErrorLiveData = MutableLiveData<ServerException>()
    private var networkErrorLiveData = MutableLiveData<NetworkException>()

    @Before
    fun before() {
        val currencyViewModel = mockk<CurrencyViewModel>(relaxed = true)
        every { currencyViewModel.serverErrorLiveData() } returns serverErrorLiveData
        every { currencyViewModel.networkErrorLiveData() } returns networkErrorLiveData
        declare { viewModel(override = true) { currencyViewModel } }
        launchFragmentInContainer<CurrencyListFragment>()
    }

    @Test
    fun `when a server error is occurred, it should show a sever error message`() {
        serverErrorLiveData.postValue(ServerException("message"))
        onView(withText(R.string.common_error_server)).check(matches(isDisplayed()))
    }

    @Test
    fun `when a network error is occurred, it should show a network error message`() {
        networkErrorLiveData.postValue(NetworkException("message"))
        onView(withText(R.string.common_error_network)).check(matches(isDisplayed()))
    }
}