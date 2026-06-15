package io.nowcrypto.library.remote.subscription
import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@Keep
@JsonClass(generateAdapter = true)
data class SubscriptionListResponse(
    @field:Json(name = "success") val success: Boolean,
    @field:Json(name = "message") val message: String,
    @field:Json(name = "status") val status: String,
    @field:Json(name = "subscriptions") val subscriptions: List<NowCryptoSubscriptionItem>
)

@Keep
@JsonClass(generateAdapter = true)
data class NowCryptoSubscriptionItem(
    @field:Json(name = "sub_id") val subId: String,
    @field:Json(name = "sub_name") val subName: String,
    @field:Json(name = "period") val period: String
)