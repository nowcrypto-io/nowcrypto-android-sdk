package io.nowcrypto.library.domain.payment_request_token

import io.nowcrypto.library.data.di.payment_request_token.PaymentRequestTokenRepository
import io.nowcrypto.library.remote.payment_request_token.PaymentRequestTokenResponse

class PaymentRequestTokenUseCase(
    private val repository: PaymentRequestTokenRepository
) {
    suspend fun execute(secretKey: String, amount: Double, currency: String, network: String): PaymentRequestTokenResponse {
        return repository.getPaymentRequestToken(secretKey, amount, currency, network)
    }
}