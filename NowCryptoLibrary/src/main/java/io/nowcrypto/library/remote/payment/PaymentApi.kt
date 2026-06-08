package io.nowcrypto.library.remote.payment

import retrofit2.http.Body
import retrofit2.http.POST

interface PaymentApi {
    @POST("payment/request/crypto")
    suspend fun payViaCrypto(@Body request: PaymentRequest): PaymentResponse
}

interface TransactionIdPaymentApi {
    @POST("payment/transaction-id")
    suspend fun payViaTransactionId(@Body request: TransactionIdPaymentRequest): PaymentResponse
}
