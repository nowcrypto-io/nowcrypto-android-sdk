package io.nowcrypto.library.remote.balance

import androidx.annotation.Keep

@Keep
data class BalanceResponse(
    val success: Boolean?,
    val message: String,
    val balance: String?,
)
