package io.nowcrypto.library.presentation.register_screen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.nowcrypto.library.data.session.SessionManager
import io.nowcrypto.library.domain.device_id.DeviceIdProvider
import io.nowcrypto.library.domain.register.RegisterUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject

class RegisterViewModel(
    private val registerUseCase: RegisterUseCase,
    private val sessionManager: SessionManager,
    private val deviceIdProvider: DeviceIdProvider,
) : ViewModel() {

    private val _registerUiState = MutableStateFlow<RegisterUiState>(RegisterUiState.Idle)
    val registerUiState: StateFlow<RegisterUiState> = _registerUiState

    var usernameError by mutableStateOf(false)
    var emailError by mutableStateOf("")
    var passwordError by mutableStateOf("")
    var confirmPasswordError by mutableStateOf("")

    var username by mutableStateOf("")
        private set

    var email by mutableStateOf("")
        private set

    var password by mutableStateOf("")
        private set

    var confirmPassword by mutableStateOf("")
        private set

    fun onUsernameChange(newValue: String) {
        username = newValue
    }

    fun onEmailChange(newValue: String) {
        email = newValue
    }

    fun onPasswordChange(newValue: String) {
        password = newValue
    }

    fun onConfirmPasswordChange(newValue: String) {
        confirmPassword = newValue
    }

    fun register() {
        usernameError = username.isBlank()

        emailError = when {
            email.isBlank() -> "Email cannot be empty"
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Invalid email format"
            else -> ""
        }

        passwordError = when {
            password.isBlank() -> "Password cannot be empty"
            password.length < 8 -> "Password must be at least 8 characters"
            !password.any { it.isDigit() } -> "Must contain at least one number"
            !password.any { it.isUpperCase() } -> "Must contain one uppercase letter"
            !password.any { it.isLowerCase() } -> "Must contain one lowercase letter"
            !password.any { !it.isLetterOrDigit() } -> "Must contain one special character"
            else -> ""
        }

        confirmPasswordError = when {
            confirmPassword.isBlank() -> "Please confirm your password"
            confirmPassword != password -> "Passwords do not match"
            else -> ""
        }

        val isFormInvalid = usernameError ||
                emailError.isNotEmpty() ||
                passwordError.isNotEmpty() ||
                confirmPasswordError.isNotEmpty()

        if (isFormInvalid) return

        viewModelScope.launch {
            _registerUiState.value = RegisterUiState.RegisterLoading

            try {
                val deviceId = deviceIdProvider.getDeviceId()
                val result = registerUseCase.execute(username, email, password, deviceId)

                if (result.success && result.token != null) {
                    sessionManager.saveSession(result.token, false, result.userName, result.profilePictureUrl)
                    _registerUiState.value = RegisterUiState.RegisterSuccess(result.message)
                } else {
                    val errorMsg = result.message
                    _registerUiState.value = RegisterUiState.RegisterError(errorMsg)
                }
            } catch (e: retrofit2.HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                val errorMessage = if (!errorBody.isNullOrEmpty()) {
                    try {
                        val json = JSONObject(errorBody)
                        json.optString("message", "Server error")
                    } catch (_: Exception) {
                        "Server error: ${e.message()}"
                    }
                } else {
                    "Server error: ${e.message()}"
                }

                _registerUiState.value = RegisterUiState.RegisterError(errorMessage)

            } catch (e: java.io.IOException) {
                _registerUiState.value = RegisterUiState.RegisterError("Network error: ${e.localizedMessage}")

            } catch (e: Exception) {
                _registerUiState.value = RegisterUiState.RegisterError("Unexpected error: ${e.localizedMessage}")
            }
        }
    }
}
