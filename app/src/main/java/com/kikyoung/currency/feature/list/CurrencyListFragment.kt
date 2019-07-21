package com.kikyoung.currency.feature.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.kikyoung.currency.R
import com.kikyoung.currency.base.BaseFragment
import com.kikyoung.currency.feature.list.model.CurrencyRate
import com.kikyoung.currency.util.extension.observeChanges
import com.kikyoung.currency.util.extension.show
import kotlinx.android.synthetic.main.fragment_currency_list.*

class CurrencyListFragment : BaseFragment<CurrencyViewModel>(CurrencyViewModel::class) {

    private val currencyListAdapter = CurrencyListAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_currency_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setActionBar()

        currencyListRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = currencyListAdapter
            addItemDecoration(DividerItemDecoration(context!!, DividerItemDecoration.VERTICAL).apply {
                setDrawable(ContextCompat.getDrawable(context!!, R.drawable.currency_list_divider)!!)
            })
        }

        currencyListAdapter.setOnItemClickListener(object : CurrencyListAdapter.OnItemClickListener {
            override fun onItemClick(currencyCode: String) {
                currencyListRecyclerView.scrollToPosition(0)
                viewModel.setBaseCurrencyCode(currencyCode)
            }
        })

        viewModel.currencyListLiveData().observeChanges(this) { currencyList ->
            // TODO Show date for the rates
            showCurrencyList(currencyList.rates)
        }

        viewModel.startPollingLatestRate()
    }

    private fun setActionBar() {
        setHasOptionsMenu(true)
        try {
            val appCompatActivity = activity as AppCompatActivity
            appCompatActivity.setSupportActionBar(currencyListToolbar)
            appCompatActivity.supportActionBar?.apply {
                setDisplayHomeAsUpEnabled(false)
                setTitle(R.string.currency_list_title)
            }
        } catch (e: ClassCastException) {
            // Ignore for FragmentScenario tests
        }
    }

    private fun showCurrencyList(currencyRates: List<CurrencyRate>) {
        currencyListAdapter.updateCurrencyRates(currencyRates)
        currencyListRecyclerView.show()
    }
}