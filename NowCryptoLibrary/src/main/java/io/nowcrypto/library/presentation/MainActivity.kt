package io.nowcrypto.library.presentation

import android.os.Bundle
import android.os.Handler
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.material3.MaterialTheme
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import io.nowcrypto.library.NowCrypto
import io.nowcrypto.library.presentation.payment_screen.PaymentScreen
import io.nowcrypto.library.presentation.register_screen.RegistrationScreen
import io.nowcrypto.library.presentation.login_screen.LoginScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val paymentRequestToken = intent.getStringExtra(PAYMENT_REQUEST_TOKEN) ?: ""
        val apiKey = intent.getStringExtra(API_KEY) ?: ""


        if (paymentRequestToken.isBlank()) {
            NowCrypto.notifyFailure("Missing payment request token")
            finish()
            return
        }

        if (apiKey.isBlank()) {
            NowCrypto.notifyFailure("Missing public api key")
            finish()
            return
        }

        setContent {
            MaterialTheme {

                Surface(color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = Screen.PaymentScreen.route
                    ) {

                        composable(
                            route = Screen.PaymentScreen.route
                        ) {
                            BackHandler { handlePaymentCancelled() }

                            PaymentScreen(
                                navController = navController,
                                apiKey = apiKey,
                                paymentRequestToken = paymentRequestToken,
                                onError = { message ->
                                    handlePaymentFailure(message)
                                },
                                onSuccess = { message ->
                                    handlePaymentSuccess(message)
                                }
                            )
                        }

                        composable(
                            route = Screen.RegistrationScreen.route,
                            arguments = listOf(
                                navArgument("message") {
                                    type = NavType.StringType
                                    nullable = true
                                    defaultValue = null
                                }
                            )
                        ) { backStackEntry ->
                            val message = backStackEntry.arguments?.getString("message")
                            RegistrationScreen(
                                navController = navController,
                                message = message
                            )
                        }

                        composable(
                            route = Screen.LoginScreen.route,
                            arguments = listOf(
                                navArgument("message") {
                                    type = NavType.StringType
                                    nullable = true
                                    defaultValue = null
                                }
                            )
                        ) { backStackEntry ->
                            val message = backStackEntry.arguments?.getString("message")
                            LoginScreen(
                                navController = navController,
                                message = message
                            )
                        }
                    }
                }
            }
        }
    }

    private fun handlePaymentSuccess(txId: String) {
        NowCrypto.notifySuccess(txId)

        Handler(mainLooper).postDelayed({
            finish()
        }, 2000)
    }

    private fun handlePaymentFailure(error: String) {
        NowCrypto.notifyFailure(error)
        finish()
    }

    private fun handlePaymentCancelled() {
        NowCrypto.notifyCancelled()
        finish()
    }

    companion object {
        const val PAYMENT_REQUEST_TOKEN = "payment_request_token"
        const val API_KEY = "api_key"
    }
}
