package com.kikyoung.currency.feature

import android.os.Bundle
import com.kikyoung.currency.R
import com.kikyoung.currency.base.BaseActivity
import com.kikyoung.currency.feature.list.CurrencyViewModel
import com.kikyoung.currency.util.extension.hide
import com.kikyoung.currency.util.extension.observeChanges
import com.kikyoung.currency.util.extension.show
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : BaseActivity() {

    private val currencyViewModel: CurrencyViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        currencyViewModel.loadingLiveData().observeChanges(this) { visible ->
            // NOTE vs Data Binding
            progressBarViewGroup.apply { if (visible) show() else hide() }
        }
    }
}
