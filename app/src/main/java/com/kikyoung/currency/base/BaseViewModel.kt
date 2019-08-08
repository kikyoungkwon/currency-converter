package com.kikyoung.currency.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.kikyoung.currency.data.exception.NetworkException
import com.kikyoung.currency.data.exception.ServerException
import com.kikyoung.currency.util.SingleLiveEvent
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import java.net.UnknownHostException
import java.util.*
import java.util.concurrent.TimeoutException

open class BaseViewModel : ViewModel() {

    private val attachedDisposables = CompositeDisposable()

    private val liveDataMap = HashMap<LiveData<*>, Observer<*>>()

    private val serverErrorLiveData = SingleLiveEvent<ServerException>()
    private val networkErrorLiveData = SingleLiveEvent<NetworkException>()

    fun handleRepositoryError(t: Throwable) {
        when (t) {
            // E.g. No Internet.
            is UnknownHostException, is TimeoutException -> networkErrorLiveData.postValue(NetworkException(t.message))
            else -> serverErrorLiveData.postValue(ServerException(t.message))
        }
    }

    protected fun disposeOnViewDetach(disposable: Disposable) {
        attachedDisposables.add(disposable)
    }

    fun <T> observeUntilCleared(liveData: LiveData<T>, observer: Observer<T>) {
        liveDataMap[liveData] = observer
        liveData.observeForever(observer)
    }

    private fun <T> removeObserver(liveData: LiveData<T>) {
        liveData.removeObserver(liveDataMap[liveData] as Observer<T>)
        liveDataMap.remove(liveData)
    }

    override fun onCleared() {
        attachedDisposables.clear()
        liveDataMap.forEach { (liveData) -> removeObserver(liveData) }
        super.onCleared()
    }

    fun serverErrorLiveData(): LiveData<ServerException> = serverErrorLiveData
    fun networkErrorLiveData(): LiveData<NetworkException> = networkErrorLiveData
}