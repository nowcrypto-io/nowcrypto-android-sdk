package io.nowcrypto.library.remote.subscription

import androidx.annotation.Keep

@Keep
data class QueryActiveSubscriptionRequest(
    val publicKey: String,
    val identifier: String
)