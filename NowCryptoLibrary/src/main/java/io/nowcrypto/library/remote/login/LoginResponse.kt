package io.nowcrypto.library.remote.login

import androidx.annotation.Keep

@Keep
data class LoginResponse(
    val success: Boolean,
    val message: String,
    val status: String?,
    val token: String?,
    val userName: String?,
    val profilePictureUrl: String?
)