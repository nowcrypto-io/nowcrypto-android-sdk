package io.nowcrypto.library.remote.balance

import androidx.annotation.Keep

@Keep
data class BalanceRequest(val apiKey: String, val currencyCode: String)