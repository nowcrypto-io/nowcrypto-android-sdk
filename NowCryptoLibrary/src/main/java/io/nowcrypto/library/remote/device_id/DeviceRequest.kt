package io.nowcrypto.library.remote.device_id

import androidx.annotation.Keep

@Keep
data class DeviceRequest(val deviceId: String, val apiKey: String, val paymentRequestToken: String)