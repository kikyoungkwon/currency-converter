package com.kikyoung.currency.di

import com.kikyoung.currency.data.mapper.CurrencyMapper
import org.koin.dsl.module

val mappersModule = module {
    single { CurrencyMapper() }
}