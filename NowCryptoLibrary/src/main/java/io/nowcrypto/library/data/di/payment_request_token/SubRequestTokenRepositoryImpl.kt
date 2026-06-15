package io.nowcrypto.library.data.di.payment_request_token

import io.nowcrypto.library.remote.payment_request_token.SubRequestTokenApi
import io.nowcrypto.library.remote.payment_request_token.SubRequestTokenRequest
import io.nowcrypto.library.remote.payment_request_token.SubRequestTokenResponse

class SubRequestTokenRepositoryImpl(
    private val api: SubRequestTokenApi
) : SubRequestTokenRepository {

    override suspend fun getSubscriptionRequestToken(
        secretKey: String,
        amount: Double,
        currency: String,
        network: String,
        subId: String
    ): SubRequestTokenResponse {
        val requestBody = SubRequestTokenRequest(amount, currency, network, subId)

        // Pass the secretKey with "Bearer " prefix to the Authorization header
        return api.getSubscriptionRequestToken(
            authHeader = "Bearer $secretKey",
            request = requestBody
        )
    }
}
