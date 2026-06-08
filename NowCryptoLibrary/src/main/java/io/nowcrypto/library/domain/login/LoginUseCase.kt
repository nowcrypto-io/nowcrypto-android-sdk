package io.nowcrypto.library.domain.login

import io.nowcrypto.library.data.di.login.LoginRepository
import io.nowcrypto.library.remote.login.LoginResponse

class LoginUseCase(
    private val repository: LoginRepository
) {
    suspend fun execute(username: String, password: String): LoginResponse {
        return repository.login(username, password)
    }
}