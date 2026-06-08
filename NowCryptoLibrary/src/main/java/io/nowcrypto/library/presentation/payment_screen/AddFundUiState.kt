package io.nowcrypto.library.presentation.payment_screen

import androidx.annotation.Keep

@Keep
sealed class AddFundUiState {

    @Keep
    object Idle : AddFundUiState()

    @Keep
    object Loading : AddFundUiState()

    @Keep
    data class Success(val message: String) : AddFundUiState()

    @Keep
    data class Error(val message: String) : AddFundUiState()

    @Keep
    data class PaymentSuccess(val message: String, val trxId: String?) : AddFundUiState()

    @Keep
    data class VerifyingBlocks(val blocks: Int) : AddFundUiState()
}
