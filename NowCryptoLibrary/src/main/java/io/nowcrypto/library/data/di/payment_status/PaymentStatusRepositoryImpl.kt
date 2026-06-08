package io.nowcrypto.library.data.di.payment_status

import io.nowcrypto.library.remote.payment_status.PaymentStatusApi
import io.nowcrypto.library.remote.payment_status.PaymentStatusRequest
import io.nowcrypto.library.remote.payment_status.PaymentStatusResponse

class PaymentStatusRepositoryImpl(
    private val api: PaymentStatusApi
) : PaymentStatusRepository {
    override suspend fun getPaymentStatus(publicKey: String, paymentRequestToken: String?, trxId: String?): PaymentStatusResponse {
        return api.getPaymentStatus(PaymentStatusRequest(publicKey, paymentRequestToken, trxId))
    }
}