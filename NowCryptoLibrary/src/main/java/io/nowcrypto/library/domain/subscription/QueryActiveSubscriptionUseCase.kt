package io.nowcrypto.library.domain.subscription

import io.nowcrypto.library.data.di.subscription.QueryActiveSubscriptionRepository
import io.nowcrypto.library.remote.subscription.QueryActiveSubscriptionResponse

class QueryActiveSubscriptionUseCase(
    private val repository: QueryActiveSubscriptionRepository
) {
    suspend fun execute(publicKey: String, identifier: String): QueryActiveSubscriptionResponse {
        return repository.queryActiveSubscription(publicKey, identifier)
    }
}