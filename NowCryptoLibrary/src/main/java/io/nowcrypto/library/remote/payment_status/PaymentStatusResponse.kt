package io.nowcrypto.library.remote.payment_status

import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@Keep
@JsonClass(generateAdapter = true)
data class PaymentStatusResponse(
    val success: Boolean,
    val message: String,
    val status: String,
    val subscription: SubscriptionData? = null,
    val transaction: TransactionData? = null
)

@Keep
@JsonClass(generateAdapter = true)
data class SubscriptionData(
    @field:Json(name = "trx_id") val trxId: String,
    val status: String,
    val expiration: String?,
    val amount: String,
    val currency: String
)

@Keep
@JsonClass(generateAdapter = true)
data class TransactionData(
    @field:Json(name = "trx_id") val trxId: String,
    val status: String,
    val amount: String,
    val currency: String
)