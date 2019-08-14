package com.kikyoung.currency.base

import org.junit.After
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest

open class BaseKoinTest : KoinTest {

    @After
    fun after() {
        stopKoin()
    }
}