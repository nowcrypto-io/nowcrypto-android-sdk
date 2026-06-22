package io.nowcrypto.sdk.remote.confirm_block

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class ConfirmBlockResponse(
    val success: Boolean,
    val confirmations: Int,
    val status: String? = null,
    val message: String? = null
)