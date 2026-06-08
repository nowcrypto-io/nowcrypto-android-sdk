package io.nowcrypto.library.remote.payment

import androidx.annotation.Keep

@Keep
data class PaymentRequest(
    val apiKey: String,
    val paymentRequestToken: String,
)

@Keep
data class TransactionIdPaymentRequest(
    val deviceId: String,
    val publicKey: String,
    val transactionId: String,
    val paymentRequestToken: String
)