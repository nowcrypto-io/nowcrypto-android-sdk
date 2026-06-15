package io.nowcrypto.library.data.di.payment_request_token

import io.nowcrypto.library.remote.payment_request_token.PaymentRequestTokenApi
import io.nowcrypto.library.remote.payment_request_token.PaymentRequestTokenRequest
import io.nowcrypto.library.remote.payment_request_token.PaymentRequestTokenResponse

class PaymentRequestTokenRepositoryImpl(
    private val api: PaymentRequestTokenApi
) : PaymentRequestTokenRepository {

    override suspend fun getPaymentRequestToken(
        secretKey: String,
        amount: Double,
        currency: String,
        network: String
    ): PaymentRequestTokenResponse {
        val requestBody = PaymentRequestTokenRequest(amount, currency, network)

        return api.getPaymentRequestToken(
            authHeader = "Bearer $secretKey",
            request = requestBody
        )
    }
}
