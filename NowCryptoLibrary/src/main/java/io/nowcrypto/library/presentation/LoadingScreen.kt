package io.nowcrypto.library.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.nowcrypto.library.R
import io.nowcrypto.library.presentation.ui.theme.PrimaryColor
import io.nowcrypto.library.presentation.ui.theme.TextColor

@Composable
fun LoadingScreen(message: String = "Loading…") {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
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

        Spacer(modifier = Modifier.height(24.dp))

        CircularProgressIndicator(
            color = PrimaryColor
        )

        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge.copy(
                color = TextColor
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PaymentProcessingScreenPreview() {
    MaterialTheme {
        LoadingScreen()
    }
}
