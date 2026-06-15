package io.nowcrypto.library.remote.subscription

import retrofit2.http.Body
import retrofit2.http.POST

interface QueryActiveSubscriptionApi {
    @POST("payment/request/subscription/active")
    suspend fun queryActiveSubscription(@Body request: QueryActiveSubscriptionRequest): QueryActiveSubscriptionResponse
}