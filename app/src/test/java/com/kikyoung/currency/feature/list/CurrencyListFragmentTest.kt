package com.kikyoung.currency.feature.list

import android.view.View
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.MutableLiveData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kikyoung.currency.R
import com.kikyoung.currency.base.BaseKoinTest
import com.kikyoung.currency.feature.list.CurrencyListAdapter.Companion.DEFAULT_BASE_CURRENCY_RATE
import com.kikyoung.currency.feature.list.model.CurrencyList
import com.kikyoung.currency.feature.list.model.CurrencyRate
import com.kikyoung.currency.test.util.ViewVisibilityIdlingResource
import com.kikyoung.currency.test.util.actionOnItemViewAtPosition
import com.kikyoung.currency.test.util.withRecyclerView
import com.kikyoung.currency.util.CurrencyUtil
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.android.synthetic.main.fragment_currency_list.view.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.test.mock.declare
import org.robolectric.annotation.TextLayoutMode

@RunWith(AndroidJUnit4::class)
@TextLayoutMode(TextLayoutMode.Mode.REALISTIC)
class CurrencyListFragmentTest : BaseKoinTest() {

    private lateinit var currencyListFragment: CurrencyListFragment
    private val currencyListLiveData = MutableLiveData<CurrencyList>()
    private val currencyViewModel = mockk<CurrencyViewModel>(relaxed = true)

    @Before
    fun before() {
        every { currencyViewModel.currencyListLiveData() } returns currencyListLiveData
        declare { viewModel(override = true) { currencyViewModel } }
        launchFragmentInContainer<CurrencyListFragment>().onFragment {
            currencyListFragment = it
        }
    }

    @Test
    fun `when it starts, it should start polling latest rate`() {
        verify { currencyViewModel.startPollingLatestRate() }
    }

    @Test
    fun `when currency list is provided, it should show them`() {
        val currencyList = currencyList()
        currencyListLiveData.postValue(currencyList)
        onView(withId(R.id.currencyListRecyclerView)).check(matches(isDisplayed()))
        testRecyclerViewItem(0, currencyList.rates[0], DEFAULT_BASE_CURRENCY_RATE)
        testRecyclerViewItem(1, currencyList.rates[1], DEFAULT_BASE_CURRENCY_RATE)
        testRecyclerViewItem(2, currencyList.rates[2], DEFAULT_BASE_CURRENCY_RATE)
    }

    @Test
    fun `when changed the base currency rate, it should reflect to others`() {
        val currencyList = currencyList()
        currencyListLiveData.postValue(currencyList)
        val viewVisibilityIdlingResource = ViewVisibilityIdlingResource(
            currencyListFragment.view!!.currencyListRecyclerView,
            View.VISIBLE
        )
        IdlingRegistry.getInstance().register(viewVisibilityIdlingResource)
        val newRate = 200.0
        onView(withId(R.id.currencyListRecyclerView)).perform(
            actionOnItemViewAtPosition(
                0, R.id.rateEditText,
                replaceText(newRate.toInt().toString())
            )
        )
        IdlingRegistry.getInstance().unregister(viewVisibilityIdlingResource)
        testRecyclerViewItem(0, currencyList.rates[0], newRate)
        testRecyclerViewItem(1, currencyList.rates[1], newRate)
        testRecyclerViewItem(2, currencyList.rates[2], newRate)
    }

    @Test
    fun `when changed the base currency, it should show new currency at the top`() {
        val currencyList = currencyList()
        currencyListLiveData.postValue(currencyList)
        onView(withId(R.id.currencyListRecyclerView)).perform(actionOnItemViewAtPosition(1, R.id.codeTextView, click()))
        testRecyclerViewItem(0, currencyList.rates[1], DEFAULT_BASE_CURRENCY_RATE)
        testRecyclerViewItem(1, currencyList.rates[0], DEFAULT_BASE_CURRENCY_RATE)
        testRecyclerViewItem(2, currencyList.rates[2], DEFAULT_BASE_CURRENCY_RATE)
    }

    private fun currencyList(): CurrencyList {
        return CurrencyList(
            "date", listOf(
                CurrencyRate("EUR", "Euro", 1.0, "https://www.countryflags.io/eu/shiny/64.png"),
                CurrencyRate("GBP", "British Pound", 2.0, "https://www.countryflags.io/gb/shiny/64.png"),
                CurrencyRate("USD", "US Dollar", 3.0, "https://www.countryflags.io/us/shiny/64.png")
            )
        )
    }

    private fun testRecyclerViewItem(itemIndex: Int, currencyRate: CurrencyRate, baseCurrencyRate: Double) {
        val code = currencyRate.code
        val name = currencyRate.displayName
        val rate = CurrencyUtil.format(currencyRate.code, currencyRate.rate * baseCurrencyRate)

        onView(withRecyclerView(R.id.currencyListRecyclerView).atPositionOnView(itemIndex, R.id.codeTextView))
            .check(matches(withText(code)))
        onView(withRecyclerView(R.id.currencyListRecyclerView).atPositionOnView(itemIndex, R.id.nameTextView))
            .check(matches(withText(name)))
        onView(withRecyclerView(R.id.currencyListRecyclerView).atPositionOnView(itemIndex, R.id.rateEditText))
            .check(matches(withText(rate)))
    }
}