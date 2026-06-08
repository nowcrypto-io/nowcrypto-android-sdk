package io.nowcrypto.library.remote.payment

import androidx.annotation.Keep

@Keep
data class PaymentResponse(
    val success: Boolean,
    val message: String,
    val trxId: String?,
    val status: String?
)