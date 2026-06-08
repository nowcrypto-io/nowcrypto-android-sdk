package io.nowcrypto.library.remote.subscription_list

import retrofit2.http.Body
import retrofit2.http.POST

interface SubscriptionListApi {
    @POST("payment/request/subscription/list")
    suspend fun getSubscriptionList(@Body request: SubscriptionListRequest): SubscriptionListResponse
}