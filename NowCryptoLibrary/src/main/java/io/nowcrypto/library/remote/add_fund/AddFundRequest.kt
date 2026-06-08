package io.nowcrypto.library.remote.add_fund

import androidx.annotation.Keep

@Keep
data class AddFundRequest(
    val publicKey: String,
    val transactionId: String,
    val paymentRequestToken: String,
    val walletAddress: String
)