package io.nowcrypto.library.domain.device_id

interface DeviceIdProvider {
    fun getDeviceId(): String
}