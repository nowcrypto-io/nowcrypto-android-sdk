package io.nowcrypto.library.domain.register

import io.nowcrypto.library.data.di.register.RegisterRepository
import io.nowcrypto.library.remote.register.RegisterResponse

class RegisterUseCase(
    private val repository: RegisterRepository
) {
    suspend fun execute(username: String, email: String, password: String, deviceId: String): RegisterResponse {
        return repository.register(username, email, password, deviceId)
    }
}