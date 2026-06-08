package io.nowcrypto.library.data.di.device_id

import io.nowcrypto.library.remote.device_id.RegisterDeviceResponse

interface DeviceRepository {
    suspend fun sendDeviceId(deviceId: String, apiKey: String, paymentRequestToken: String): RegisterDeviceResponse

    suspend fun getUserDetails(apiKey: String, paymentRequestToken: String): RegisterDeviceResponse
}