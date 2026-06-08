package io.nowcrypto.library.remote.confirm_block

import androidx.annotation.Keep

@Keep
data class ConfirmBlockResponse(
    val success: Boolean,
    val confirmations: Int,
    val status: String?,
    val message: String?
)