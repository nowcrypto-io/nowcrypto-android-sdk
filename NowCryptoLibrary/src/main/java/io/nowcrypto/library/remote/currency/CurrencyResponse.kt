package io.nowcrypto.library.remote.currency

import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@Keep
@JsonClass(generateAdapter = true)
data class CurrencyResponse(
    val success: Boolean,
    val message: String,
    val status: String,
    @field:Json(name = "supported_currencies")
    val supportedCurrencies: List<String>
)