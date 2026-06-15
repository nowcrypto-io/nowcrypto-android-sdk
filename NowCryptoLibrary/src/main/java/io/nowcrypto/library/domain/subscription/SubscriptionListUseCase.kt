package io.nowcrypto.library.domain.subscription

import io.nowcrypto.library.data.di.subscription.SubscriptionListRepository
import io.nowcrypto.library.remote.subscription.SubscriptionListResponse

class SubscriptionListUseCase(
    private val repository: SubscriptionListRepository
) {
    suspend fun execute(publicKey: String): SubscriptionListResponse {
        return repository.getSubscriptionList(publicKey)
    }
}