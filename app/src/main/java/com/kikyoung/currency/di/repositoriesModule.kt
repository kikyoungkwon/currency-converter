package com.kikyoung.currency.di

import com.kikyoung.currency.data.repository.CurrencyRepository
import org.koin.core.qualifier.named
import org.koin.dsl.module

val repositoriesModule = module {
    single { CurrencyRepository(get(), get(), get(), get(named("io"))) }
}