package io.nowcrypto.library.remote.payment_status

import retrofit2.http.Body
import retrofit2.http.POST

interface PaymentStatusApi {
    @POST("payment/request/status")
    suspend fun getPaymentStatus(@Body request: PaymentStatusRequest): PaymentStatusResponse
}