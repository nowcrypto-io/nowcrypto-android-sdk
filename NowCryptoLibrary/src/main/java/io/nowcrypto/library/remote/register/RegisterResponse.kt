package io.nowcrypto.library.remote.register

import androidx.annotation.Keep

@Keep
data class RegisterResponse(
    val success: Boolean,
    val message: String,
    val status: String?,
    val token: String?,
    val userName: String?,
    val profilePictureUrl: String?
)