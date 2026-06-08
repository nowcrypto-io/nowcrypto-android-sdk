package io.nowcrypto.library.remote.currency

import retrofit2.http.Body
import retrofit2.http.POST

interface CurrencyApi {
    @POST("payment/supported-currencies")
    suspend fun getSupportedCurrencies(@Body request: CurrencyRequest): CurrencyResponse
}