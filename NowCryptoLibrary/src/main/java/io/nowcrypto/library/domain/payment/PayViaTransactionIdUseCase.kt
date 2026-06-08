package io.nowcrypto.library.domain.payment

import io.nowcrypto.library.data.di.payment.PaymentRepository
import io.nowcrypto.library.remote.payment.PaymentResponse

class PayViaTransactionIdUseCase(
    private val repository: PaymentRepository
) {
    suspend fun execute(
        deviceId: String,
        publicKey: String,
        transactionId: String,
        paymentRequestToken: String
    ): PaymentResponse {
        return repository.payViaTransactionId(
            deviceId,
            publicKey,
            transactionId,
            paymentRequestToken
        )
    }
}