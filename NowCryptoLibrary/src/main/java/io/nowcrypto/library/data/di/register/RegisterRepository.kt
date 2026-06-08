package io.nowcrypto.library.data.di.register

import io.nowcrypto.library.remote.register.RegisterResponse

interface RegisterRepository {
    suspend fun register(
        username: String,
        email: String,
        password: String,
        deviceId: String
    ): RegisterResponse
}