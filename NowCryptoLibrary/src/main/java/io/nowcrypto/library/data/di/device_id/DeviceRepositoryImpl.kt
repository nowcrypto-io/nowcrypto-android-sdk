package io.nowcrypto.library.data.di.device_id

import io.nowcrypto.library.remote.device_id.DeviceApi
import io.nowcrypto.library.remote.device_id.DeviceRequest
import io.nowcrypto.library.remote.device_id.DeviceUserDetailsRequest
import io.nowcrypto.library.remote.device_id.RegisterDeviceResponse

class DeviceRepositoryImpl(
    private val api: DeviceApi
) : DeviceRepository {
    override suspend fun sendDeviceId(deviceId: String, apiKey: String, paymentRequestToken: String): RegisterDeviceResponse {
        return api.registerDevice(DeviceRequest(deviceId, apiKey, paymentRequestToken))
    }

    override suspend fun getUserDetails(
        apiKey: String,
        paymentRequestToken: String
    ): RegisterDeviceResponse {
        return api.getUserDetails(DeviceUserDetailsRequest(apiKey, paymentRequestToken))
    }
}
