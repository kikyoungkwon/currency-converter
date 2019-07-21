package com.kikyoung.currency.di

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.core.qualifier.named
import org.koin.dsl.module

val coroutinesModule = module {
    single<CoroutineDispatcher>(named("ui")) { Dispatchers.Main }
    single(named("io")) { Dispatchers.IO }
}