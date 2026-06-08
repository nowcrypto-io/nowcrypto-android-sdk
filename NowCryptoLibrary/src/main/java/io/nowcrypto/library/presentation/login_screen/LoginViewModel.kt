package io.nowcrypto.library.presentation.login_screen

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.nowcrypto.library.domain.login.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import io.nowcrypto.library.data.session.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.json.JSONObject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState

    var usernameError by mutableStateOf(false)
    var passwordError by mutableStateOf(false)

    var username by mutableStateOf("")
        private set

    var password by mutableStateOf("")
        private set

    fun onUsernameChange(newValue: String) {
        username = newValue
    }

    fun onPasswordChange(newValue: String) {
        password = newValue
    }

    fun login() {

        usernameError = username.isBlank()
        passwordError = password.isBlank()

        if (usernameError || passwordError) {
            return
        }

        viewModelScope.launch {
            _uiState.value = LoginUiState.LoginLoading

            try {
                val result = loginUseCase.execute(username, password)

                if (result.success && result.token != null) {
                    sessionManager.saveSession(result.token, false, result.userName, result.profilePictureUrl)
                    _uiState.value = LoginUiState.LoginSuccess(result.message)

                } else {
                    // If success is false or token is missing, it's an error
                    val errorMsg = result.message
                    _uiState.value = LoginUiState.LoginError(errorMsg)
                }
            } catch (e: retrofit2.HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                val errorMessage = if (!errorBody.isNullOrEmpty()) {
                    try {
                        // Parse JSON error response from Laravel
                        val json = JSONObject(errorBody)
                        json.optString("message", "Server error")
                    } catch (_: Exception) {
                        "Server error: ${e.message()}"
                    }
                } else {
                    "Server error: ${e.message()}"
                }

                _uiState.value = LoginUiState.LoginError(errorMessage)

            } catch (e: java.io.IOException) {
                _uiState.value = LoginUiState.LoginError("Network error: ${e.localizedMessage}")

            } catch (e: Exception) {
                _uiState.value = LoginUiState.LoginError("Unexpected error: ${e.localizedMessage}")
            }
        }
    }
}