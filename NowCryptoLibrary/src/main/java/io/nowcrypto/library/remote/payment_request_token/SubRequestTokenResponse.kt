package io.nowcrypto.library.remote.payment_request_token

import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@Keep
@JsonClass(generateAdapter = true)
data class SubRequestTokenResponse(
    val success: Boolean,
    val message: String,
    val status: String,
    @field:Json(name = "payment_request_token")
    val paymentRequestToken: String
)