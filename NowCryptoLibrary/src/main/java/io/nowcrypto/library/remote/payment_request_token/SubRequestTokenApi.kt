package io.nowcrypto.library.remote.payment_request_token

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface SubRequestTokenApi {
    @POST("payment/request/subscription/create")
    suspend fun getSubscriptionRequestToken(
        @Header("X-Manual-Secret-Key") secretKey: String, // Pass "Bearer $secretKey" here
        @Body request: SubRequestTokenRequest
    ): SubRequestTokenResponse
}