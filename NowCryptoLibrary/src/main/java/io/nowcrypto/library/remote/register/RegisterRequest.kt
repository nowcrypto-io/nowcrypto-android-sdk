package io.nowcrypto.library.remote.register

import androidx.annotation.Keep

@Keep
data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String,
    val deviceId: String,
)