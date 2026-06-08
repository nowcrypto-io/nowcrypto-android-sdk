package io.nowcrypto.library.data.di.subscription_list

import io.nowcrypto.library.remote.subscription_list.SubscriptionListApi
import io.nowcrypto.library.remote.subscription_list.SubscriptionListRequest
import io.nowcrypto.library.remote.subscription_list.SubscriptionListResponse

class SubscriptionListRepositoryImpl(
    private val api: SubscriptionListApi
) : SubscriptionListRepository {
    override suspend fun getSubscriptionList(publicKey: String): SubscriptionListResponse {
        return api.getSubscriptionList(SubscriptionListRequest(publicKey))
    }
}