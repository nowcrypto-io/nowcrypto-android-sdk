package io.nowcrypto.sdk.remote.register

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class RegisterResponse(
    val success: Boolean,
    val message: String,
    val status: String? = null,
    val token: String? = null,
    val userName: String? = null,
    val profilePictureUrl: String? = null
)