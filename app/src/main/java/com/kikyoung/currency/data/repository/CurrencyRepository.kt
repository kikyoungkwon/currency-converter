package com.kikyoung.currency.data.repository

import androidx.annotation.VisibleForTesting
import com.kikyoung.currency.data.LocalStorage
import com.kikyoung.currency.data.Resource
import com.kikyoung.currency.data.mapper.CurrencyMapper
import com.kikyoung.currency.data.service.CurrencyService
import com.kikyoung.currency.feature.list.model.CurrencyList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.concurrent.CancellationException

class CurrencyRepository(
    private val localStorage: LocalStorage,
    private val currencyMapper: CurrencyMapper,
    private val currencyService: CurrencyService,
    private val ioDispatcher: CoroutineDispatcher
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
    suspend fun latestRates(currencyCode: String): CurrencyList = withContext(ioDispatcher) {
        currencyMapper.toList(currencyService.latest(currencyCode))
    }

    fun pollingLatestRates() = flow<Resource<CurrencyList>> {
        getLatestRates()?.let {
            emit(Resource.Success(it))
        }

        while (true) {
            try {
                val baseCurrencyCode = getBaseCurrencyCode()
                val latestRates = latestRates(baseCurrencyCode)
                if (baseCurrencyCode == getBaseCurrencyCode()) {
                    saveLatestRates(latestRates)
                    emit(Resource.Success(getLatestRates()!!))
                } else {
                    Timber.d("Base currency code is changed, so ignore and retry")
                }
                delay(DELAY_PULLING_LATEST_RATE)
            } catch (e: CancellationException) {
                Timber.d("Polling latest rates is cancelled")
                break
            } catch (e: Exception) {
                emit(Resource.Error(e))
            }
        }
    }.flowOn(ioDispatcher)

    fun setBaseCurrencyCode(currencyCode: String) = saveBaseCurrencyCode(currencyCode)

    private fun getLatestRates() = localStorage.get(KEY_LATEST_RATES, CurrencyList::class.java)

    /**
     * Order of list is not saved.
     */
    private fun saveLatestRates(latestRates: CurrencyList) =
        localStorage.put(KEY_LATEST_RATES, CurrencyList::class.java, latestRates)

    private fun getBaseCurrencyCode(): String =
        localStorage.get(KEY_BASE_CURRENCY_CODE, String::class.java, DEFAULT_BASE_CURRENCY_CODE)!!

    private fun saveBaseCurrencyCode(currencyCode: String) =
        localStorage.put(KEY_BASE_CURRENCY_CODE, String::class.java, currencyCode)
}