package io.nowcrypto.library.domain.device_id

import io.nowcrypto.library.data.di.device_id.DeviceRepository
import io.nowcrypto.library.remote.device_id.RegisterDeviceResponse

class SendDeviceIdUseCase(
    private val repository: DeviceRepository
) {
    suspend fun execute(deviceId: String, apiKey: String, paymentRequestToken: String): RegisterDeviceResponse {
        return repository.sendDeviceId(deviceId, apiKey, paymentRequestToken)
    }
}