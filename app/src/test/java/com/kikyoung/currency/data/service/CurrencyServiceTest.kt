package com.kikyoung.currency.data.service

import com.google.gson.Gson
import com.kikyoung.currency.data.api.Api
import com.kikyoung.currency.data.exception.ServerException
import com.kikyoung.currency.data.model.CurrencyRates
import com.kikyoung.currency.data.repository.CurrencyRepository.Companion.DEFAULT_BASE_CURRENCY_CODE
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class CurrencyServiceTest {

    private var apiService: Api = mockk()
    @ExperimentalCoroutinesApi
    private val ioDispatcher = Dispatchers.Unconfined
    private lateinit var currencyService: CurrencyService

    @Before
    fun before() {
        currencyService = CurrencyService(Gson(), apiService, ioDispatcher)
    }

    @Test
    fun `when getting latest rates is successful, it should return the result`() = runBlocking {
        val response = mockk<Response<CurrencyRates>>()
        val responseBody = mockk<CurrencyRates>()
        every { response.isSuccessful } returns true
        every { response.body() } returns responseBody
        coEvery { apiService.latest(any()) } returns response
        TestCase.assertEquals(currencyService.latest(DEFAULT_BASE_CURRENCY_CODE), responseBody)
    }

    @Test(expected = ServerException::class)
    fun `when getting latest rates is unsuccessful, it should throw an exception`() = runBlocking<Unit> {
        val response = mockk<Response<CurrencyRates>>()
        every { response.isSuccessful } returns false
        every { response.code() } returns 500
        every { response.errorBody()?.string() } returns "{\"error\":\"message\"}"
        coEvery { apiService.latest(any()) } returns response
        currencyService.latest(DEFAULT_BASE_CURRENCY_CODE)
    }
}