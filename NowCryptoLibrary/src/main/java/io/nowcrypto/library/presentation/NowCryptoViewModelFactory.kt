package io.nowcrypto.library.presentation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.nowcrypto.library.data.di.NowCryptoInternal
import io.nowcrypto.library.presentation.login_screen.LoginViewModel
import io.nowcrypto.library.presentation.payment_screen.PaymentViewModel
import io.nowcrypto.library.presentation.register_screen.RegisterViewModel

internal class NowCryptoViewModelFactory(context: Context) : ViewModelProvider.Factory {
    private val internal = NowCryptoInternal.getInstance(context)

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(PaymentViewModel::class.java) -> {
                PaymentViewModel(
                    deviceIdProvider = internal.deviceIdProvider,
                    sendDeviceIdUseCase = internal.sendDeviceIdUseCase,
                    getUserDetailsUseCase = internal.getUserDetailsUseCase,
                    fetchUserBalanceUseCase = internal.fetchUserBalanceUseCase,
                    payViaCryptoUseCase = internal.payViaCryptoUseCase,
                    payViaTransactionIdUseCase = internal.payViaTransactionIdUseCase,
                    confirmBlockUseCase = internal.confirmBlockUseCase,
                    addFundUseCase = internal.addFundUseCase,
                    sessionManager = internal.sessionManager
                ) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(
                    loginUseCase = internal.loginUseCase,
                    sessionManager = internal.sessionManager
                ) as T
            }
            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> {
                RegisterViewModel(
                    registerUseCase = internal.registerUseCase,
                    sessionManager = internal.sessionManager,
                    deviceIdProvider = internal.deviceIdProvider
                ) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
