package io.nowcrypto.library.presentation.payment_screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import io.nowcrypto.library.presentation.LoadingScreen
import io.nowcrypto.library.presentation.NowCryptoViewModelFactory
import kotlinx.coroutines.delay

@Composable
fun PaymentScreen(
    navController: NavController,
    viewModel: PaymentViewModel = viewModel(
        factory = NowCryptoViewModelFactory(LocalContext.current)
    ),
    apiKey: String,
    paymentRequestToken: String,
    onSuccess: (String) -> Unit,
    onError: (String) -> Unit
) {

    viewModel.setApiKey(apiKey)
    viewModel.setPaymentRequestToken(paymentRequestToken)

    val paymentUiState by viewModel.paymentUiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.registerDeviceOrGetExistingUser()
    }

    LaunchedEffect(paymentUiState) {
        if (paymentUiState is PaymentUiState.SessionExpired) {
            val expiredMessage = "Your session has expired. Please log in again."

            // Pass the message as a query parameter
            navController.navigate("login_screen?message=$expiredMessage") {
                popUpTo(0) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(Unit) {

        // This block repeats ONLY when the lifecycle is STARTED (app is visible)
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.updateGuestStatus()
            if (!viewModel.isGuest.value) {
                while (true) {
                    viewModel.startFetchingBalance()
                    delay(5000) // Wait 5 seconds before next fetch
                }
            }
        }
    }

    when (paymentUiState) {
        PaymentUiState.RegisterDeviceLoading -> {
            LoadingScreen("Loading…")
        }
        is PaymentUiState.RegisterDeviceSuccess -> {
            PaymentFlowScreen(
                viewModel = viewModel,
                navController = navController,
                onSuccess
            )
        }
        is PaymentUiState.RegisterDeviceError -> {
            onError((paymentUiState as PaymentUiState.RegisterDeviceError).message)
        }
        is PaymentUiState.PaymentExpired -> {
            onError((paymentUiState as PaymentUiState.PaymentExpired).message)
        }
        is PaymentUiState.WalletError -> {
            onError((paymentUiState as PaymentUiState.WalletError).message)
        }
        PaymentUiState.PaymentLoading -> {
            LoadingScreen("Processing payment…")
        }
        is PaymentUiState.PaymentSuccess -> {

            if ((paymentUiState as PaymentUiState.PaymentSuccess).result.trxId != null) {
                onSuccess(
                    (paymentUiState as PaymentUiState.PaymentSuccess).result.trxId!!
                )
                PaymentResultScreen(
                    true,
                    "Payment Successful!"
                ) {
                    onSuccess(
                        (paymentUiState as PaymentUiState.PaymentSuccess).result.trxId!!
                    )
                }
            } else {
                onError("Suspected fraudulent transaction")
            }
        }
        is PaymentUiState.PaymentError -> {
            PaymentResultScreen(
                false,
                (paymentUiState as PaymentUiState.PaymentError).message
            ) {
                viewModel.resetToRegisterSuccess()
            }
        }
        is PaymentUiState.TestPayment -> {

            val paymentType = (paymentUiState as PaymentUiState.TestPayment).paymentType

            TestPaymentScreen { result ->
                if (result == "success") {
                    if (paymentType == "BALANCE_PAYMENT")
                        viewModel.payViaCrypto()
                    else if (paymentType == "TRANSACTION_ID_PAYMENT")
                        viewModel.payViaTransactionId()
                } else {
                    onError(
                        "Test: Payment failed"
                    )
                }
            }
        }
        is PaymentUiState.SessionExpired -> {
            LoadingScreen("Session expired, redirecting…")
        }
        PaymentUiState.Idle -> {}
    }
}
