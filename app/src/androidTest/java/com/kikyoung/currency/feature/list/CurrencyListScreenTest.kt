package com.kikyoung.currency.feature.list

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.rule.ActivityTestRule
import com.kikyoung.currency.R
import com.kikyoung.currency.base.BaseScreenTest
import com.kikyoung.currency.feature.MainActivity
import com.kikyoung.currency.test.util.ViewVisibilityIdlingResource
import com.kikyoung.currency.test.util.actionOnItemViewAtPosition
import com.kikyoung.currency.test.util.withRecyclerView
import org.junit.Before
import org.junit.Rule
import org.junit.Test

// NOTE vs BDD, vs Robot pattern
class CurrencyListScreenTest : BaseScreenTest() {

    companion object {
        private const val RESPONSE_FILE_LATEST_SUCCESS = "success.json"
    }

    @get:Rule
    var activityRule: ActivityTestRule<MainActivity> = ActivityTestRule(MainActivity::class.java, true, false)

    @Before
    override fun before() {
        super.before()
        mockWebServer.setLatestCurrencyRatesResponse(RESPONSE_FILE_LATEST_SUCCESS)
        activityRule.launchActivity(null)

        var viewVisibilityIdlingResource = ViewVisibilityIdlingResource(
            activityRule.activity.findViewById<RecyclerView>(R.id.currencyListRecyclerView),
            View.VISIBLE
        )
        IdlingRegistry.getInstance().register(viewVisibilityIdlingResource)
        onView(ViewMatchers.withId(R.id.currencyListRecyclerView)).check(matches(isDisplayed()))
        IdlingRegistry.getInstance().unregister(viewVisibilityIdlingResource)
    }

    @Test
    fun showList() {
        testRecyclerViewItem(0, "EUR", "100")
        testRecyclerViewItem(1, "GBP", "200")
        testRecyclerViewItem(2, "USD", "300")
    }

    @Test
    fun changeBaseRate() {
        val newRate = 200.0
        onView(ViewMatchers.withId(R.id.currencyListRecyclerView)).perform(
            actionOnItemViewAtPosition(
                0, R.id.rateEditText,
                ViewActions.replaceText(newRate.toInt().toString())
            )
        )
        testRecyclerViewItem(0, "EUR", "200")
        testRecyclerViewItem(1, "GBP", "400")
        testRecyclerViewItem(2, "USD", "600")
    }

    @Test
    fun changeBaseCurrency() {
        onView(ViewMatchers.withId(R.id.currencyListRecyclerView)).perform(
            actionOnItemViewAtPosition(
                1, R.id.codeTextView,
                ViewActions.click()
            )
        )
        testRecyclerViewItem(0, "GBP", "200")
        testRecyclerViewItem(1, "EUR", "100")
        testRecyclerViewItem(2, "USD", "300")
    }

    @Test
    fun changeBaseCurrencyAndRate() {
        onView(ViewMatchers.withId(R.id.currencyListRecyclerView)).perform(
            actionOnItemViewAtPosition(
                1, R.id.codeTextView,
                ViewActions.click()
            )
        )
        val newRate = 400.0
        onView(ViewMatchers.withId(R.id.currencyListRecyclerView)).perform(
            actionOnItemViewAtPosition(
                0, R.id.rateEditText,
                ViewActions.replaceText(newRate.toInt().toString())
            )
        )
        testRecyclerViewItem(0, "GBP", "400")
        testRecyclerViewItem(1, "EUR", "200")
        testRecyclerViewItem(2, "USD", "600")
    }

    private fun testRecyclerViewItem(itemIndex: Int, code: String, rate: String) {
        onView(withRecyclerView(R.id.currencyListRecyclerView).atPositionOnView(itemIndex, R.id.codeTextView))
            .check(matches(ViewMatchers.withText(code)))
        onView(withRecyclerView(R.id.currencyListRecyclerView).atPositionOnView(itemIndex, R.id.rateEditText))
            .check(matches(ViewMatchers.withText(rate)))
    }
}