package io.nowcrypto.library.presentation.register_screen

import android.widget.Toast
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import io.nowcrypto.library.presentation.LoadingScreen

@Composable
fun RegistrationScreen(
    viewModel: RegisterViewModel = hiltViewModel(),
    navController: NavController,
    message: String? = null
) {
    val registerUiState by viewModel.registerUiState.collectAsState()
    val context = LocalContext.current

    when (registerUiState) {
        is RegisterUiState.RegisterLoading -> {
            LoadingScreen("Signing Up...")
        }
        is RegisterUiState.RegisterSuccess -> {
            RegisterSuccessScreen(
                navController
            )
        }
        is RegisterUiState.RegisterError -> {
            val error = (registerUiState as RegisterUiState.RegisterError).message
            Toast.makeText(context, error, Toast.LENGTH_SHORT).show()

            RegisterFlowScreen(
                navController,
                viewModel,
                Pair(error, "error")
            )
        }
        else -> {
            RegisterFlowScreen(
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
