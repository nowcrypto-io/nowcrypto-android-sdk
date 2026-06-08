package io.nowcrypto.library.remote.device_id

import androidx.annotation.Keep

@Keep
data class DeviceUserDetailsRequest(val apiKey: String, val paymentRequestToken: String)