package com.kikyoung.currency.feature.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.kikyoung.currency.base.BaseViewModel
import com.kikyoung.currency.data.Resource
import com.kikyoung.currency.data.repository.CurrencyRepository
import com.kikyoung.currency.feature.list.model.CurrencyList
import io.reactivex.Scheduler

class CurrencyViewModel(
    private val currencyRepository: CurrencyRepository,
    private val uiScheduler: Scheduler
) : BaseViewModel() {

    private val loadingLiveData = MutableLiveData<Boolean>()
    private val currencyListLiveData = MutableLiveData<CurrencyList>()

    fun startPollingLatestRate() {
        loadingLiveData.postValue(true)
        disposeOnViewDetach(currencyRepository.pollingLatestRates()
            .observeOn(uiScheduler)
            .subscribe { resource ->
            when (resource) {
                is Resource.Success -> currencyListLiveData.postValue(resource.data)
                is Resource.Error<CurrencyList> -> handleRepositoryError(resource.t)
            }
            loadingLiveData.postValue(false)
        })
    }

    fun setBaseCurrencyCode(currencyCode: String) = currencyRepository.setBaseCurrencyCode(currencyCode)

    fun loadingLiveData(): LiveData<Boolean> = loadingLiveData
    fun currencyListLiveData(): LiveData<CurrencyList> = currencyListLiveData
}