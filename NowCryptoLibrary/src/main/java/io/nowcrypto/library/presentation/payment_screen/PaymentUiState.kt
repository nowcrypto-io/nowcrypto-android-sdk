package io.nowcrypto.library.presentation.payment_screen

import androidx.annotation.Keep
import io.nowcrypto.library.remote.device_id.RegisterDeviceResponse
import io.nowcrypto.library.remote.payment.PaymentResponse

@Keep
sealed class PaymentUiState {

    @Keep
    object Idle : PaymentUiState()

    // Device Registration flow
    @Keep
    object RegisterDeviceLoading : PaymentUiState()

    @Keep
    data class RegisterDeviceSuccess(val response: RegisterDeviceResponse) : PaymentUiState()

    @Keep
    data class RegisterDeviceError(val message: String) : PaymentUiState()

    @Keep
    data class WalletError(val message: String) : PaymentUiState()

    @Keep
    data class SessionExpired(val message: String) : PaymentUiState()

    // Payment flow
    @Keep
    object PaymentLoading : PaymentUiState()

    @Keep
    data class PaymentSuccess(val result: PaymentResponse) : PaymentUiState()

    @Keep
    data class PaymentError(val message: String) : PaymentUiState()

    @Keep
    data class TestPayment(val paymentType: String) : PaymentUiState()

    @Keep
    data class PaymentExpired(val message: String) : PaymentUiState()
}

