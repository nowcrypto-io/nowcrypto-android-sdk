package io.nowcrypto.library.remote.payment_request_token

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface PaymentRequestTokenApi {
    @POST("payment/request/create")
    suspend fun getPaymentRequestToken(
        @Header("X-Manual-Secret-Key") secretKey: String, // Pass "Bearer $secretKey" here
        @Body request: PaymentRequestTokenRequest
    ): PaymentRequestTokenResponse
}