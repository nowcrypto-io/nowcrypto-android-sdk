package io.nowcrypto.library.remote.subscription

import com.squareup.moshi.Json

data class QueryActiveSubscriptionResponse(
    val success: Boolean,
    val message: String,
    val status: String,

    @field:Json(name = "has_active_subscription")
    val hasActiveSubscription: Boolean,

    val subscriptions: List<NowCryptoSubscription>
)

data class NowCryptoSubscription(
    @field:Json(name = "has_active_subscription")
    val trxId: String,

    val status: String,
    val expiration: String
)