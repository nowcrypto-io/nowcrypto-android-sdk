package io.nowcrypto.library.remote.login

import retrofit2.http.Body
import retrofit2.http.POST

interface LoginApi {
    @POST("payment/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse
}