package com.kikyoung.currency.feature.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.kikyoung.currency.base.BaseViewModel
import com.kikyoung.currency.data.Resource
import com.kikyoung.currency.data.repository.CurrencyRepository
import com.kikyoung.currency.feature.list.model.CurrencyList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch

class CurrencyViewModel(
    private val currencyRepository: CurrencyRepository,
    uiDispatcher: CoroutineDispatcher
) : BaseViewModel(uiDispatcher) {

    private val loadingLiveData = MutableLiveData<Boolean>()
    private val currencyListLiveData = MutableLiveData<CurrencyList>()

    fun startPollingLatestRate() {
        launch {
            loadingLiveData.postValue(true)
            currencyRepository.pollingLatestRates()
        }

        observeUntilCleared(currencyRepository.latestRatesLiveData(), Observer {
            when(it) {
                is Resource.Success -> currencyListLiveData.postValue(it.data)
                is Resource.Error -> handleRepositoryError(it.e)
            }
            loadingLiveData.postValue(false)
        })
    }

    fun setBaseCurrencyCode(currencyCode: String) = currencyRepository.setBaseCurrencyCode(currencyCode)

    fun loadingLiveData(): LiveData<Boolean> = loadingLiveData
    fun currencyListLiveData(): LiveData<CurrencyList> = currencyListLiveData
}