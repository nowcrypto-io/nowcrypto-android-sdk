package io.nowcrypto.library.domain.device_id

import io.nowcrypto.library.data.di.device_id.DeviceRepository
import io.nowcrypto.library.remote.device_id.RegisterDeviceResponse

class GetUserDetailsUseCase(
    private val repository: DeviceRepository
) {
    suspend fun execute(apiKey: String, paymentRequestToken: String): RegisterDeviceResponse {
        return repository.getUserDetails(apiKey, paymentRequestToken)
    }
}