package com.kikyoung.currency.di

import com.kikyoung.currency.data.service.CurrencyService
import org.koin.core.qualifier.named
import org.koin.dsl.module

val servicesModule = module {
    single { CurrencyService(get(), get(), get(named("io"))) }
}