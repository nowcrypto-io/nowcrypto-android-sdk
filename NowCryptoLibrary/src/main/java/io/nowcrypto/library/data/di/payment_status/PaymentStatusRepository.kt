package io.nowcrypto.library.data.di.payment_status

import io.nowcrypto.library.remote.payment_status.PaymentStatusResponse

interface PaymentStatusRepository {
    suspend fun getPaymentStatus(
        publicKey: String,
        paymentRequestToken: String?,
        trxId: String?
    ): PaymentStatusResponse
}