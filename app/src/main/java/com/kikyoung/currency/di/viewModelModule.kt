package com.kikyoung.currency.di

import com.kikyoung.currency.feature.list.CurrencyViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { CurrencyViewModel(get(), get(named("ui"))) }
}