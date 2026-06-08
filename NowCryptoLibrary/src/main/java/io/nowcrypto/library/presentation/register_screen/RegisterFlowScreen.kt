package io.nowcrypto.library.presentation.register_screen

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
fun RegisterFlowScreen(
    navController: NavController,
    viewModel: RegisterViewModel,
    message: Pair<String?, String> //Message text, type of message
) {
    val scrollState = rememberScrollState()

    Box(modifier = Modifier.fillMaxSize().imePadding()) {

        var hasPopped by remember { mutableStateOf(false) }

        IconButton(
            onClick = {
                if (!hasPopped) {
                    val popped = navController.popBackStack(
                        route = Screen.PaymentScreen.route,
                        inclusive = false
                    )
                    if (popped) {
                        hasPopped = true // prevent multiple pops
                    }
                }
            },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
                .zIndex(1f) // ensures above the Column
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
                text = "Register",
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
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = LineGray,
                    unfocusedBorderColor = LineGray,
                    disabledBorderColor = LineGray,
                    cursorColor = Color.Black
                ),
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = viewModel.email,
                onValueChange = viewModel::onEmailChange,
                isError = viewModel.emailError.isNotEmpty(),
                supportingText = {
                    if (viewModel.emailError.isNotEmpty()) {
                        Text(
                            text = viewModel.emailError,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                label = { Text("Email") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = LineGray,
                    unfocusedBorderColor = LineGray,
                    disabledBorderColor = LineGray,
                    cursorColor = Color.Black
                ),
            )

            Spacer(modifier = Modifier.height(12.dp))

            var passwordVisible by remember { mutableStateOf(false) }

            OutlinedTextField(
                value = viewModel.password,
                onValueChange = viewModel::onPasswordChange,
                isError = viewModel.passwordError.isNotEmpty(),
                supportingText = {
                    if (viewModel.passwordError.isNotEmpty()) {
                        Text(
                            text = viewModel.passwordError,
                            color = MaterialTheme.colorScheme.error
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

            Spacer(modifier = Modifier.height(12.dp))

            var confirmPasswordVisible by remember { mutableStateOf(false) }

            OutlinedTextField(
                value = viewModel.confirmPassword,
                onValueChange = viewModel::onConfirmPasswordChange,
                isError = viewModel.confirmPasswordError.isNotEmpty(),
                supportingText = {
                    if (viewModel.confirmPasswordError.isNotEmpty()) {
                        Text(
                            text = viewModel.confirmPasswordError,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                },
                label = { Text("Confirm Password") },
                // Toggle transformation based on state
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth(),
                // Add the trailing icon (The Eye)
                trailingIcon = {
                    val image = if (confirmPasswordVisible)
                        Icons.Filled.Visibility
                    else Icons.Filled.VisibilityOff

                    val description = if (confirmPasswordVisible) "Hide password" else "Show password"

                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
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
                    viewModel.register()
                },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor),
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(top = 15.dp, bottom = 15.dp)
            ) {
                Text("Register", fontSize = 19.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Already have an account?",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TextColor
                ),
                fontSize = 17.sp,
            )

            Text(
                text = "Login",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = PrimaryColor,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier
                    .padding(top = 4.dp)
                    .clickable {
                        navController.navigate(
                            Screen.LoginScreen.route)
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
//fun RegisterFlowScreenPreview() {
//    val navController = rememberNavController()
//    MaterialTheme {
//        RegisterFlowScreen(navController)
//    }
//}
