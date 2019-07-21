package com.kikyoung.currency.base

import android.app.Application
import com.kikyoung.currency.BuildConfig
import com.kikyoung.currency.di.*
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber

class BaseApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        setKoin()
    }

    private fun setKoin() {
        startKoin {
            androidLogger()
            androidContext(this@BaseApplication)
            modules(
                listOf(
                    coroutinesModule,
                    storageModule,
                    networkModule,
                    servicesModule,
                    mappersModule,
                    repositoriesModule,
                    viewModelModule
                )
            )
        }
    }
}