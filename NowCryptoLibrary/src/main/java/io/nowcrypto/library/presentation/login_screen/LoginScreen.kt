package io.nowcrypto.library.presentation.login_screen

import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import io.nowcrypto.library.presentation.LoadingScreen
import io.nowcrypto.library.presentation.Screen

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    navController: NavController,
    message: String? = null
) {
    val uiState by viewModel.uiState.collectAsState()

    when (uiState) {
        is LoginUiState.LoginLoading -> {
            LoadingScreen("Logging In...")
        }
        is LoginUiState.LoginSuccess -> {
            // Try to go back to the existing PaymentScreen first
            val popped = navController.popBackStack(
                route = Screen.PaymentScreen.route,
                inclusive = false
            )

            // If it failed (PaymentScreen wasn't in the history),
            // just navigate there directly.
            if (!popped) {
                navController.navigate(Screen.PaymentScreen.route) {
                    // This prevents creating a loop if they are already "at the start"
                    popUpTo(0)
                }
            }
        }
        is LoginUiState.LoginError -> {
            val error = (uiState as LoginUiState.LoginError).message

            LoginFlowScreen(
                navController,
                viewModel,
                Pair(error, "error")
            )
        }
        else -> {
            LoginFlowScreen(
                navController,
                viewModel,
                Pair(message, "notification")
            )
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun RegistrationScreenPreview() {
//    val navController = rememberNavController()
//    MaterialTheme {
//        RegistrationScreen(navController)
//    }
//}
