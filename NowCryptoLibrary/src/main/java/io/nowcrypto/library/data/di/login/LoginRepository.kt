package io.nowcrypto.library.data.di.login

import io.nowcrypto.library.remote.login.LoginResponse

interface LoginRepository {
    suspend fun login(
        username: String,
        password: String
    ): LoginResponse
}