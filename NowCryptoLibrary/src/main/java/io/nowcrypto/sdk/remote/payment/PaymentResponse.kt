package io.nowcrypto.sdk.remote.payment

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class PaymentResponse(
    val success: Boolean,
    val message: String,
    val trxId: String? = null,
    val status: String? = null
)