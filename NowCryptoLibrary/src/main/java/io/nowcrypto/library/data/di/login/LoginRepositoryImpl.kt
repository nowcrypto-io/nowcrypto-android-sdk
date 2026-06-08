package io.nowcrypto.library.data.di.login

import io.nowcrypto.library.remote.login.LoginApi
import io.nowcrypto.library.remote.login.LoginRequest
import io.nowcrypto.library.remote.login.LoginResponse

class LoginRepositoryImpl(
    private val api: LoginApi
) : LoginRepository {
    override suspend fun login(username: String, password: String): LoginResponse {
        return api.login(LoginRequest(username, password))
    }
}
