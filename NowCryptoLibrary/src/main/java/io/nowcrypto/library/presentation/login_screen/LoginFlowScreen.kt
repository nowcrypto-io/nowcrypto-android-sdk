package io.nowcrypto.library.presentation.login_screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.zIndex
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import io.nowcrypto.library.presentation.ui.theme.TextColor
import io.nowcrypto.library.R
import io.nowcrypto.library.presentation.Screen
import io.nowcrypto.library.presentation.ui.theme.LineGray
import io.nowcrypto.library.presentation.ui.theme.PrimaryColor

@Composable
fun LoginFlowScreen(
    navController: NavController,
    viewModel: LoginViewModel,
    message: Pair<String?, String> //Message text, type of message
) {
    val scrollState = rememberScrollState()

    Box(modifier = Modifier.fillMaxSize().imePadding()) {
        IconButton(
            onClick = {
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
            },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
                .zIndex(1f)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close"
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 30.dp, vertical = 40.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "NowCrypto Logo",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(45.dp)
                    .padding(bottom = 5.dp),
                contentScale = ContentScale.Fit
            )

            Text(
                text = "Login",
                style = MaterialTheme.typography.headlineSmall.copy(
                    color = TextColor,
                    fontSize = 22.sp
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = viewModel.username,
                onValueChange = viewModel::onUsernameChange,
                isError = viewModel.usernameError,
                supportingText = {
                    if (viewModel.usernameError) {
                        Text(
                            text = "Username cannot be empty",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                },
                label = { Text("Username") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = LineGray,
                    unfocusedBorderColor = LineGray,
                    disabledBorderColor = LineGray,
                    cursorColor = Color.Black
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            var passwordVisible by remember { mutableStateOf(false) }

            OutlinedTextField(
                value = viewModel.password,
                onValueChange = viewModel::onPasswordChange,
                isError = viewModel.passwordError,
                supportingText = {
                    if (viewModel.usernameError) {
                        Text(
                            text = "Password cannot be empty",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                },
                label = { Text("Password") },
                // Toggle transformation based on state
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth(),
                // Add the trailing icon (The Eye)
                trailingIcon = {
                    val image = if (passwordVisible)
                        Icons.Filled.Visibility
                    else Icons.Filled.VisibilityOff

                    val description = if (passwordVisible) "Hide password" else "Show password"

                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = image, contentDescription = description)
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = LineGray,
                    unfocusedBorderColor = LineGray,
                    disabledBorderColor = LineGray,
                    cursorColor = Color.Black
                ),
            )

            Spacer(modifier = Modifier.height(18.dp))

            Button(
                onClick = {
                    viewModel.login()
                },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryColor
                ),
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(top = 15.dp, bottom = 15.dp)
            ) {
                Text(
                    "Login",
                    fontSize = 19.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Don\'t have an account yet?",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TextColor
                ),
                fontSize = 17.sp,
            )

            Text(
                text = "Register",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = PrimaryColor,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier
                    .padding(top = 4.dp)
                    .clickable {
                        navController.navigate(
                            Screen.RegistrationScreen.route)
                    },
                fontSize = 17.sp,
            )

            // Use isNotBlank() to ignore both nulls and empty/whitespace-only strings
            if (!message.first.isNullOrBlank() && message.first != "{message}") {
                Spacer(modifier = Modifier.height(24.dp))

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = if (message.second == "error") Color(0xFFFFDADA) else Color(0xFFFFFDE7),
                    border = BorderStroke(2.dp, if (message.second == "error") Color(0xFFD34242) else Color(0xFFD4C76A))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = if (message.second == "error")  Color(0xFFD34242) else Color(0xFFD4C76A),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = message.first!!, // No need for 'msg' here since we checked 'message'
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = if (message.second == "error") Color(0xFF8C3838) else Color(0xFF5A542E),
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(35.dp))
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun LoginFlowScreenPreview() {
//    val navController = rememberNavController()
//    MaterialTheme {
//        LoginFlowScreen(navController)
//    }
//}
