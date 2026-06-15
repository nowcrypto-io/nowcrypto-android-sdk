package io.nowcrypto.library.data.di.subscription

import io.nowcrypto.library.remote.subscription.SubscriptionListApi
import io.nowcrypto.library.remote.subscription.SubscriptionListRequest
import io.nowcrypto.library.remote.subscription.SubscriptionListResponse

class SubscriptionListRepositoryImpl(
    private val api: SubscriptionListApi
) : SubscriptionListRepository {
    override suspend fun getSubscriptionList(publicKey: String): SubscriptionListResponse {
        return api.getSubscriptionList(SubscriptionListRequest(publicKey))
    }
}