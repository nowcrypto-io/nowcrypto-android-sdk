package io.nowcrypto.library.remote.login

import androidx.annotation.Keep

@Keep
data class LoginRequest(
    val username: String,
    val password: String,
)