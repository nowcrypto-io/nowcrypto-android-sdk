package io.nowcrypto.library.remote.device_id

import retrofit2.http.Body
import retrofit2.http.POST

interface DeviceApi {
    @POST("payment/device/register")
    suspend fun registerDevice(@Body request: DeviceRequest): RegisterDeviceResponse

    @POST("payment/device/get")
    suspend fun getUserDetails(@Body request: DeviceUserDetailsRequest): RegisterDeviceResponse
}
