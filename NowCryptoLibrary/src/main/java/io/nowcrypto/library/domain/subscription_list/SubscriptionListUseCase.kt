package io.nowcrypto.library.domain.subscription_list

import io.nowcrypto.library.data.di.subscription_list.SubscriptionListRepository
import io.nowcrypto.library.remote.subscription_list.SubscriptionListResponse

class SubscriptionListUseCase(
    private val repository: SubscriptionListRepository
) {
    suspend fun execute(publicKey: String): SubscriptionListResponse {
        return repository.getSubscriptionList(publicKey)
    }
}