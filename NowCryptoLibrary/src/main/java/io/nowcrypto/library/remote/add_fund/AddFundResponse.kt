package io.nowcrypto.library.remote.add_fund

import androidx.annotation.Keep

@Keep
data class AddFundResponse(
    val success: Boolean,
    val message: String,
    val status: String?,
)