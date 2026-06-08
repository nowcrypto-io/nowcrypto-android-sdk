package io.nowcrypto.library.presentation

sealed class Screen(val route: String) {
    object PaymentScreen: Screen("payment_screen")
    object RegistrationScreen: Screen("registration_screen?message={message}")
    object LoginScreen: Screen("login_screen?message={message}")
}
