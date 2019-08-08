package com.kikyoung.currency.data.repository

import androidx.annotation.VisibleForTesting
import com.kikyoung.currency.data.LocalStorage
import com.kikyoung.currency.data.Resource
import com.kikyoung.currency.data.mapper.CurrencyMapper
import com.kikyoung.currency.data.service.CurrencyService
import com.kikyoung.currency.feature.list.model.CurrencyList
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.Single
import java.util.concurrent.TimeUnit

class CurrencyRepository(
    private val localStorage: LocalStorage,
    private val currencyMapper: CurrencyMapper,
    private val currencyService: CurrencyService,
    private val ioScheduler: Scheduler
) {

    companion object {
        @VisibleForTesting
        // Refresh every second is too much, every 60 seconds could be good.
        const val DELAY_PULLING_LATEST_RATE = 1000L

        @VisibleForTesting
        const val DEFAULT_BASE_CURRENCY_CODE = "EUR"
        @VisibleForTesting
        const val KEY_LATEST_RATES = "latest_rates"
        @VisibleForTesting
        const val KEY_BASE_CURRENCY_CODE = "base_currency_code"
    }

    @VisibleForTesting
    fun latestRates(currencyCode: String): Single<CurrencyList> {
        return currencyService.latest(currencyCode)
            .map {
                currencyMapper.toList(it)
            }
    }

    private fun currencyList(): Observable<Resource<CurrencyList>> {
        val baseCurrencyCode = getBaseCurrencyCode()
        return latestRates(baseCurrencyCode).toObservable()
            // If base currency code is changed, ignore and retry
            .filter {
                baseCurrencyCode == getBaseCurrencyCode()
            }
            .doOnNext { currencyList ->
                saveLatestRates(currencyList)
            }
            .map<Resource<CurrencyList>> { currencyList ->
                Resource.Success(currencyList)
            }
            .onErrorResumeNext { t: Throwable ->
                Observable.just(Resource.Error(t))
            }
    }

    fun pollingLatestRates(): Observable<Resource<CurrencyList>> {
        return Observable.interval(DELAY_PULLING_LATEST_RATE, TimeUnit.MILLISECONDS)
            .flatMap {
                currencyList()
            }
            .startWith(cachedCurrencyListObservable())
            .subscribeOn(ioScheduler)
    }

    private fun cachedCurrencyListObservable(): Observable<Resource<CurrencyList>> {
        val latestRates = getLatestRates()
        return if (latestRates != null)
            Observable.just(Resource.Success(latestRates))
        else Observable.empty()
    }

    fun setBaseCurrencyCode(currencyCode: String) = saveBaseCurrencyCode(currencyCode)

    private fun getLatestRates() = localStorage.get(KEY_LATEST_RATES, CurrencyList::class.java)

    /**
     * Order of list is not saved.
     */
    private fun saveLatestRates(latestRates: CurrencyList) = localStorage.put(KEY_LATEST_RATES, latestRates)

    private fun getBaseCurrencyCode(): String =
        localStorage.get(KEY_BASE_CURRENCY_CODE, String::class.java, DEFAULT_BASE_CURRENCY_CODE)!!

    private fun saveBaseCurrencyCode(currencyCode: String) = localStorage.put(KEY_BASE_CURRENCY_CODE, currencyCode)
}