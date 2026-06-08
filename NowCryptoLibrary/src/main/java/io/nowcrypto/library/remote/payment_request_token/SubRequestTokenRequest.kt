package io.nowcrypto.library.remote.payment_request_token

import androidx.annotation.Keep

@Keep
data class SubRequestTokenRequest(
    val amount: Double,
    val currency: String,
    val network: String,
    val subId: String
)