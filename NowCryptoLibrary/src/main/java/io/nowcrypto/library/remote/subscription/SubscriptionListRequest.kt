package io.nowcrypto.library.remote.subscription

import androidx.annotation.Keep

@Keep
data class SubscriptionListRequest(
    val publicKey: String
)