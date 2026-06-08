package io.nowcrypto.library.data.di.subscription_list

import io.nowcrypto.library.remote.subscription_list.SubscriptionListResponse

interface SubscriptionListRepository {
    suspend fun getSubscriptionList(
        publicKey: String
    ): SubscriptionListResponse
}