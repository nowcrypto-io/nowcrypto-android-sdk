package io.nowcrypto.library.presentation.payment_screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import io.nowcrypto.library.presentation.LoadingScreen
import kotlinx.coroutines.delay

@Composable
fun PaymentScreen(
    navController: NavController,
    viewModel: PaymentViewModel = hiltViewModel(),
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

//@Composable
//fun NotificationBanner(
//    message: String,
//    onClose: () -> Unit,
//    onClick: () -> Unit
//) {
//    Surface(
//        color = Color(0xFF2196F3),
//        modifier = Modifier
//            .fillMaxWidth()
//            .clickable { onClick() },
//        shadowElevation = 4.dp
//    ) {
//        Row(
//            modifier = Modifier
//                .padding(horizontal = 16.dp, vertical = 8.dp),
//            verticalAlignment = Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.SpaceBetween
//        ) {
//            Text(
//                text = message,
//                color = Color.White,
//                modifier = Modifier.weight(1f)
//            )
//
//            IconButton(onClick = { onClose() }) {
//                Icon(
//                    imageVector = Icons.Default.Close,
//                    contentDescription = "Close",
//                    tint = Color.White
//                )
//            }
//        }
//    }
//}

//@Preview(showBackground = true)
//@Composable
//fun PaymentScreenPreview() {
//    MaterialTheme {
//        PaymentScreen(
//            price = 50.0,
//            currency = "USD",
//            WalletResponse(
//                id = 1,
//                uuid = "uuid1",
//                user_id = "123",
//                currency_code = "BTC",
//                balance = "0.0",
//                currency = CurrencyDetail(1, "Bitcoin", "BTC", "₿")
//            ),
//            walletAddress = "TYAdKeFscwKL3F9AKdgJCfHVgcqfwTTwfP",
//            walletList = listOf(
//                WalletResponse(
//                    id = 1,
//                    uuid = "uuid1",
//                    user_id = "123",
//                    currency_code = "BTC",
//                    balance = "0.0",
//                    currency = CurrencyDetail(1, "Bitcoin", "BTC", "₿")
//                ),
//                WalletResponse(
//                    id = 2,
//                    uuid = "uuid2",
//                    user_id = "123",
//                    currency_code = "USDT",
//                    balance = "0.0",
//                    currency = CurrencyDetail(2, "Tether", "USDT", "$")
//                ),
//                WalletResponse(
//                    id = 3,
//                    uuid = "uuid3",
//                    user_id = "123",
//                    currency_code = "TRX",
//                    balance = "0.0",
//                    currency = CurrencyDetail(3, "Tron", "TRX", "TRX")
//                )
//            )
//        )
//    }
//}


