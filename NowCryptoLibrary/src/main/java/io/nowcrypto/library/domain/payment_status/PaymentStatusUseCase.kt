package io.nowcrypto.library.domain.payment_status

import io.nowcrypto.library.data.di.payment_status.PaymentStatusRepository
import io.nowcrypto.library.remote.payment_status.PaymentStatusResponse

class PaymentStatusUseCase(
    private val repository: PaymentStatusRepository
) {
    suspend fun execute(publicKey: String, paymentRequestToken: String?, trxId: String?): PaymentStatusResponse {
        return repository.getPaymentStatus(publicKey, paymentRequestToken, trxId)
    }
}