package com.kikyoung.currency.di

import com.kikyoung.currency.data.service.CurrencyService
import org.koin.dsl.module

val servicesModule = module {
    single { CurrencyService(get()) }
}