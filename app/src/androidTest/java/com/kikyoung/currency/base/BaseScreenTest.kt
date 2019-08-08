package com.kikyoung.currency.base

import android.os.AsyncTask
import androidx.test.espresso.IdlingRegistry
import com.jakewharton.espresso.OkHttp3IdlingResource
import com.kikyoung.currency.data.TestMockWebServer
import io.reactivex.schedulers.Schedulers
import org.junit.After
import org.junit.Before
import org.koin.core.qualifier.named
import org.koin.test.KoinTest
import org.koin.test.get
import org.koin.test.mock.declare

open class BaseScreenTest : KoinTest {

    private val okHttp3IdlingResource = OkHttp3IdlingResource.create("OkHttp3", get())
    protected val mockWebServer = TestMockWebServer()

    @Before
    open fun before() {
        IdlingRegistry.getInstance().register(okHttp3IdlingResource)
        declare {
            single(named("io"), override = true) {
                Schedulers.from(AsyncTask.THREAD_POOL_EXECUTOR)
            }
            single(named("baseUrl"), override = true) {
                mockWebServer.getBaseUrl()
            }
        }
    }

    @After
    open fun after() {
        IdlingRegistry.getInstance().unregister(okHttp3IdlingResource)
    }
}