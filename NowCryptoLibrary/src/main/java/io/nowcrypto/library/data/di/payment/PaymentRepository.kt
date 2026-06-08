package io.nowcrypto.library.data.di.payment

import io.nowcrypto.library.remote.payment.PaymentResponse

interface PaymentRepository {
    suspend fun payViaCrypto(apiKey: String, paymentRequestToken: String): PaymentResponse
    suspend fun payViaTransactionId(
        deviceId: String,
        publicKey: String,
        transactionId: String,
        paymentRequestToken: String
    ): PaymentResponse
}