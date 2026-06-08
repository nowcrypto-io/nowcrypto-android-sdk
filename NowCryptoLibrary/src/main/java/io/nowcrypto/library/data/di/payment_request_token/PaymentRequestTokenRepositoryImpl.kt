package io.nowcrypto.library.data.di.payment_request_token

import io.nowcrypto.library.remote.payment_request_token.PaymentRequestTokenApi
import io.nowcrypto.library.remote.payment_request_token.PaymentRequestTokenRequest
import io.nowcrypto.library.remote.payment_request_token.PaymentRequestTokenResponse
import javax.inject.Inject

class PaymentRequestTokenRepositoryImpl @Inject constructor(
    private val api: PaymentRequestTokenApi
) : PaymentRequestTokenRepository {

    override suspend fun getPaymentRequestToken(
        secretKey: String,
        amount: Double,
        currency: String,
        network: String
    ): PaymentRequestTokenResponse {
        // Create the body without the secret key
        val requestBody = PaymentRequestTokenRequest(amount, currency, network)

        // Pass the secretKey to the header param handled by your AuthInterceptor
        return api.getPaymentRequestToken(
            secretKey = secretKey,
            request = requestBody
        )
    }
}