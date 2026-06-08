package io.nowcrypto.library.data.di.payment_request_token

import io.nowcrypto.library.remote.payment_request_token.SubRequestTokenApi
import io.nowcrypto.library.remote.payment_request_token.SubRequestTokenRequest
import io.nowcrypto.library.remote.payment_request_token.SubRequestTokenResponse
import javax.inject.Inject

class SubRequestTokenRepositoryImpl @Inject constructor(
    private val api: SubRequestTokenApi
) : SubRequestTokenRepository {

    override suspend fun getSubscriptionRequestToken(
        secretKey: String,
        amount: Double,
        currency: String,
        network: String,
        subId: String
    ): SubRequestTokenResponse {
        // Create the body without the secret key
        val requestBody = SubRequestTokenRequest(amount, currency, network, subId)

        // Pass the secretKey to the header param handled by your AuthInterceptor
        return api.getSubscriptionRequestToken(
            secretKey = secretKey,
            request = requestBody
        )
    }
}