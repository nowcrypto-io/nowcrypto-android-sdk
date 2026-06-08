package io.nowcrypto.library.remote.device_id

import androidx.annotation.Keep

@Keep
data class RegisterDeviceResponse(
    val success: Boolean,
    val message: String,
    val supportedCurrencies: List<String>? = emptyList(),
    val walletAddress: String?,
    val qrCode: String?,
    val balance: String?,
    val amount: String?,
    val currency: String?,
    val network: String?,
    val token: String?,
    val isSubscription: Boolean?,
    val period: String?,
    val environment: String,
    val unixTimeStamp: Long,
    val userName: String?,
    val profilePictureUrl: String?
)