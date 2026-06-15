package io.nowcrypto.library.data.di.subscription

import io.nowcrypto.library.remote.subscription.QueryActiveSubscriptionResponse

interface QueryActiveSubscriptionRepository {
    suspend fun queryActiveSubscription(
        publicKey: String,
        identifier: String
    ): QueryActiveSubscriptionResponse
}