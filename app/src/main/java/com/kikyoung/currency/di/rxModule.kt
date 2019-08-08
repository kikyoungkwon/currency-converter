package com.kikyoung.currency.di

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.koin.core.qualifier.named
import org.koin.dsl.module

val rxModule = module {
    single(named("ui")) { AndroidSchedulers.mainThread() }
    single(named("io")) { Schedulers.io() }
}