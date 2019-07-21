package com.kikyoung.currency.di

import com.kikyoung.currency.data.LocalStorage
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val storageModule = module {
    single { LocalStorage(androidContext(), get()) }
}