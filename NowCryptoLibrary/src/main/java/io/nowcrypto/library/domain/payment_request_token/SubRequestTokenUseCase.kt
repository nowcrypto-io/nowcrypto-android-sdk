package io.nowcrypto.library.domain.payment_request_token

import io.nowcrypto.library.data.di.payment_request_token.SubRequestTokenRepository
import io.nowcrypto.library.remote.payment_request_token.SubRequestTokenResponse

class SubRequestTokenUseCase(
    private val repository: SubRequestTokenRepository
) {
    suspend fun execute(secretKey: String, amount: Double, currency: String, network: String, subId: String): SubRequestTokenResponse {
        return repository.getSubscriptionRequestToken(secretKey, amount, currency, network, subId)
    }
}