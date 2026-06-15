package io.nowcrypto.library.data.di.subscription

import io.nowcrypto.library.remote.subscription.SubscriptionListResponse

interface SubscriptionListRepository {
    suspend fun getSubscriptionList(
        publicKey: String
    ): SubscriptionListResponse
}