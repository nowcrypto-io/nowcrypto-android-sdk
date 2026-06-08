package io.nowcrypto.library.remote.payment_status

import androidx.annotation.Keep

@Keep
data class PaymentStatusRequest(
    val publicKey: String,
    val paymentRequestToken: String? = null,
    val trxId: String? = null
)