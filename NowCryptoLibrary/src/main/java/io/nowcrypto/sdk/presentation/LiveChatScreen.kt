package io.nowcrypto.sdk.presentation

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import io.nowcrypto.sdk.data.ApiConstants.BASE_URL
import io.nowcrypto.sdk.presentation.ui.theme.BorderGray
import io.nowcrypto.sdk.presentation.ui.theme.TextColor

@Composable
fun LiveChatScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
    ) {
        // App Bar / Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.LightGray.copy(alpha = 0.15f))
                .statusBarsPadding()
                .padding(vertical = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Live Chat",
                style = MaterialTheme.typography.headlineSmall.copy(
                    color = TextColor,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            )

            // Back Button (Uniform style)
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 24.dp)
                    .size(38.dp)
                    .clip(CircleShape)
                    .zIndex(1f)
                    .background(Color.White)
                    .border(
                        1.dp,
                        BorderGray,
                        CircleShape
                    )
                    .clickable {
                        val popped = navController.popBackStack(
                            route = Screen.PaymentScreen.route,
                            inclusive = false
                        )

                        if (!popped) {
                            navController.navigate(Screen.PaymentScreen.route) {
                                popUpTo(0)
                            }
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "Back",
                    tint = TextColor,
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        AndroidView(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            factory = { context ->
                WebView(context).apply {
                    webViewClient = WebViewClient()
                    settings.javaScriptEnabled = true
                    loadUrl("$BASE_URL/live-chat")
                }
            }
        )
    }
}
