package io.nowcrypto.library.presentation.login_screen

import androidx.annotation.Keep

@Keep
sealed class LoginUiState {

    @Keep
    object Idle : LoginUiState()

    @Keep
    object LoginLoading : LoginUiState()

    @Keep
    data class LoginSuccess(val message: String) : LoginUiState()

    @Keep
    data class LoginError(val message: String) : LoginUiState()
}

