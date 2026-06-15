package io.nowcrypto.library.data.di.subscription

import io.nowcrypto.library.remote.subscription.QueryActiveSubscriptionApi
import io.nowcrypto.library.remote.subscription.QueryActiveSubscriptionRequest
import io.nowcrypto.library.remote.subscription.QueryActiveSubscriptionResponse


class QueryActiveSubscriptionRepositoryImpl(
    private val api: QueryActiveSubscriptionApi
) : QueryActiveSubscriptionRepository {
    override suspend fun queryActiveSubscription(publicKey: String, identifier: String): QueryActiveSubscriptionResponse {
        return api.queryActiveSubscription(QueryActiveSubscriptionRequest(publicKey, identifier))
    }
}