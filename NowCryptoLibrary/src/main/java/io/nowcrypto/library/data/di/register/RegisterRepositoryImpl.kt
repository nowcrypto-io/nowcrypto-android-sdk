package io.nowcrypto.library.data.di.register

import io.nowcrypto.library.remote.register.RegisterApi
import io.nowcrypto.library.remote.register.RegisterRequest
import io.nowcrypto.library.remote.register.RegisterResponse

class RegisterRepositoryImpl(
    private val api: RegisterApi
) : RegisterRepository {
    override suspend fun register(username: String, email: String, password: String, deviceId: String): RegisterResponse {
        return api.register(RegisterRequest(username, email, password, deviceId))
    }
}
