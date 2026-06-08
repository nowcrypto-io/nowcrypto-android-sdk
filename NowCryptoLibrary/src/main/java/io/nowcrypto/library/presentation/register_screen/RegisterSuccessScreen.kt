package io.nowcrypto.library.presentation.register_screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import io.nowcrypto.library.presentation.Screen
import io.nowcrypto.library.presentation.ui.theme.PrimaryColor
import io.nowcrypto.library.presentation.ui.theme.TextColor
import kotlinx.coroutines.delay

@Composable
fun RegisterSuccessScreen(
    navController: NavController
) {
    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        delay(2000) // Wait for 2 seconds
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(start = 20.dp, end = 20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = "Success",
            tint = Color(0xFF4CAF50),
            modifier = Modifier.size(120.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Successfully Signed Up!",
            style = MaterialTheme.typography.headlineSmall.copy(
                color = TextColor
            ),
            textAlign = TextAlign.Center,
            fontSize = 24.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        var hasPopped by remember { mutableStateOf(false) }

        Button(
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
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryColor
            ),
            shape = RoundedCornerShape(10.dp),
            contentPadding = PaddingValues(top = 12.dp, bottom = 12.dp)
        ) {
            Text(
                "Back",
                fontSize = 17.sp
            )
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun RegisterSuccessScreenPreview() {
//    MaterialTheme {
//        RegisterSuccessScreen()
//    }
//}

