package io.nowcrypto.sdk.remote.device_id

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class RegisterDeviceResponse(
    val success: Boolean,
    val message: String,

    val walletAddress: String? = null,
    val qrCode: String? = null,
    val balance: String? = null,
    val amount: String? = null,
    val currency: String? = null,
    val network: String? = null,
    val environment: String = "",
    val isSubscription: Boolean? = null,
    val period: String? = null,
    val unixTimeStamp: Long = 0,
    val userName: String? = null,
    val profilePictureUrl: String? = null,
    val trxId: String? = null,
    val token: String? = null,

    @SerialName("instruction_message") val instructionMessage: String? = null,
    @SerialName("instruction_text_1") val instructionText1: String? = null,
    @SerialName("instruction_text_2") val instructionText2: String? = null,
    @SerialName("instruction_text_3") val instructionText3: String? = null,
    @SerialName("instruction_link_1") val instructionLink1: String? = null,
    @SerialName("instruction_link_2") val instructionLink2: String? = null,
    @SerialName("instruction_link_3") val instructionLink3: String? = null,
    @SerialName("email_support") val emailSupport: String? = null,
    @SerialName("twitter_link") val twitterLink: String? = null,
    @SerialName("telegram_link") val telegramLink: String? = null,
    @SerialName("merchant_name") val merchantName: String? = null,
    @SerialName("merchant_logo") val merchantLogo: String? = null,
)