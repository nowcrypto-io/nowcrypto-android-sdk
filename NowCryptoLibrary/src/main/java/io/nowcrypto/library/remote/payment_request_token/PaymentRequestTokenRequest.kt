package io.nowcrypto.library.remote.payment_request_token

import androidx.annotation.Keep

@Keep
data class PaymentRequestTokenRequest(
    val amount: Double,
    val currency: String,
    val network: String
)