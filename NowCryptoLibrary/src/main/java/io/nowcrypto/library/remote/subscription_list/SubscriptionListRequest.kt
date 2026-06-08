package io.nowcrypto.library.remote.subscription_list

import androidx.annotation.Keep

@Keep
data class SubscriptionListRequest(
    val publicKey: String
)