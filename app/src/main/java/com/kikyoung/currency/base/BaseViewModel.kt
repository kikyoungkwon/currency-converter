package com.kikyoung.currency.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.kikyoung.currency.data.exception.NetworkException
import com.kikyoung.currency.data.exception.ServerException
import com.kikyoung.currency.util.SingleLiveEvent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import java.net.UnknownHostException
import java.util.*
import java.util.concurrent.TimeoutException
import kotlin.coroutines.CoroutineContext

open class BaseViewModel(private val uiDispatcher: CoroutineDispatcher) : ViewModel(), CoroutineScope {

    private val job = Job()

    override val coroutineContext: CoroutineContext
        get() = (uiDispatcher + job)

    private val liveDataMap = HashMap<LiveData<*>, Observer<*>>()

    private val serverErrorLiveData = SingleLiveEvent<ServerException>()
    private val networkErrorLiveData = SingleLiveEvent<NetworkException>()

    fun handleRepositoryError(e: Exception) {
        when (e) {
            // E.g. No Internet.
            is UnknownHostException, is TimeoutException -> networkErrorLiveData.postValue(NetworkException(e.message))
            is ServerException -> serverErrorLiveData.postValue(e)
            else -> serverErrorLiveData.postValue(ServerException(e.message))
        }
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
        liveDataMap.forEach { (liveData) -> removeObserver(liveData) }
        job.cancel()
        super.onCleared()
    }

    fun serverErrorLiveData(): LiveData<ServerException> = serverErrorLiveData
    fun networkErrorLiveData(): LiveData<NetworkException> = networkErrorLiveData
}