package io.nowcrypto.sdk.presentation.payment_screen

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import io.nowcrypto.sdk.Environment
import io.nowcrypto.sdk.R
import io.nowcrypto.sdk.presentation.Screen
import io.nowcrypto.sdk.presentation.ui.theme.Black
import io.nowcrypto.sdk.presentation.ui.theme.LineGray
import io.nowcrypto.sdk.presentation.ui.theme.SubtleGray
import io.nowcrypto.sdk.presentation.ui.theme.BorderGray
import io.nowcrypto.sdk.presentation.ui.theme.PrimaryColor
import io.nowcrypto.sdk.presentation.ui.theme.SecondaryTextColor
import io.nowcrypto.sdk.presentation.ui.theme.TertiaryTextColor
import io.nowcrypto.sdk.presentation.ui.theme.White
import io.nowcrypto.sdk.presentation.ui.theme.TextColor
import kotlinx.coroutines.delay
import java.util.Locale
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.HorizontalDivider
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import io.nowcrypto.sdk.data.ApiConstants.BASE_URL

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PaymentFlowScreen(
    viewModel: PaymentViewModel,
    navController: NavController,
    onSuccess: (String) -> Unit,
) {
    val balance by viewModel.balance.observeAsState()
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    val isGuest by viewModel.isGuest.collectAsState()
    val userName by viewModel.userName.collectAsState()
    val profilePictureUrl by viewModel.profilePictureUrl.collectAsState()
    val trxId by viewModel.trxId.collectAsState()

    // --- Instruction Messages ---
    val instructionMessage by viewModel.instructionMessage.collectAsState()
    val instructionText1 by viewModel.instructionText1.collectAsState()
    val instructionText2 by viewModel.instructionText2.collectAsState()
    val instructionText3 by viewModel.instructionText3.collectAsState()
    val instructionLink1 by viewModel.instructionLink1.collectAsState()
    val instructionLink2 by viewModel.instructionLink2.collectAsState()
    val instructionLink3 by viewModel.instructionLink3.collectAsState()

    // --- Support & Social Media Links ---
    val emailSupport by viewModel.emailSupport.collectAsState()
    val twitterLink by viewModel.twitterLink.collectAsState()
    val telegramLink by viewModel.telegramLink.collectAsState()

    val merchantName by viewModel.merchantName.collectAsState()
    val merchantLogo by viewModel.merchantLogo.collectAsState()

    val clipboardManager = LocalClipboardManager.current
    var showDialog by remember { mutableStateOf(false) }
    var showQrDialog by remember { mutableStateOf(false) }

    AddFundDialog(
        showDialog = showDialog,
        onDismissRequest = {
            viewModel.resetAddFundToIdle()
            showDialog = false
        },
        viewModel = viewModel,
        onSuccess = onSuccess,
        isGuest = isGuest
    )

    QrCodeDialog(
        showDialog = showQrDialog,
        qrCodeUrl = viewModel.qrCodeUrl,
        onDismiss = { showQrDialog = false }
    )

    Scaffold { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(top = 10.dp, start = 30.dp, end = 30.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            var showMenu by remember { mutableStateOf(false) }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                // =========================
                // Left: Merchant Info
                // =========================
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    if (!merchantLogo.isNullOrBlank()) {
                        // Display API Logo if available
                        AsyncImage(
                            model = merchantLogo,
                            contentDescription = "$merchantName",
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .border(
                                    1.dp,
                                    BorderGray,
                                    CircleShape
                                ),
                            contentScale = ContentScale.Crop,
                            placeholder = painterResource(R.drawable.nowcrypto_logo),
                            error = painterResource(R.drawable.nowcrypto_logo)
                        )
                    } else {
                        // Fallback: Perfectly matching circular placeholder box
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(PrimaryColor)
                                .border(
                                    1.dp,
                                    BorderGray,
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Store,
                                contentDescription = "$merchantName",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = merchantName ?: "Merchant",
                        modifier = Modifier
                            .padding(start = 4.dp)
                            .weight(1f),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // =========================
                // Right: Profile + Back
                // =========================
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Box {

                        AsyncImage(
                            model = profilePictureUrl,
                            contentDescription = "Profile",
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .border(
                                    1.dp,
                                    BorderGray,
                                    CircleShape
                                )
                                .clickable {
                                    showMenu = true
                                },
                            contentScale = ContentScale.Crop,
                            placeholder = painterResource(R.drawable.ic_profile_placeholder),
                            error = painterResource(R.drawable.ic_profile_placeholder)
                        )

                        var showLogoutDialog by remember { mutableStateOf(false) }

                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {

                            // Show username header if they are logged in and userName is available
                            if (!isGuest && !userName.isNullOrBlank()) {
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(
                                                text = "Signed in as",
                                                fontSize = 11.sp,
                                                color = TertiaryTextColor // Or your equivalent muted text color
                                            )
                                            Text(
                                                text = userName!!,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = TextColor,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    },
                                    enabled = false, // Makes it a non-clickable header item
                                    onClick = {}
                                )

                                HorizontalDivider(
                                    modifier = Modifier.padding(vertical = 4.dp),
                                    color = BorderGray.copy(alpha = 0.5f)
                                )
                            }

                            if (isGuest) {
                                DropdownMenuItem(
                                    text = { Text("Login", color = TextColor) },
                                    onClick = {
                                        navController.navigate(Screen.LoginScreen.route)
                                        showMenu = false
                                    },
                                    leadingIcon = {
                                        Icon(
                                            Icons.AutoMirrored.Filled.Login,
                                            contentDescription = null,
                                            tint = TextColor
                                        )
                                    }
                                )
                            } else {
                                DropdownMenuItem(
                                    text = { Text("Logout", color = TextColor) },
                                    onClick = {
                                        showMenu = false
                                        showLogoutDialog = true
                                    },
                                    leadingIcon = {
                                        Icon(
                                            Icons.AutoMirrored.Filled.Logout,
                                            contentDescription = null,
                                            tint = TextColor
                                        )
                                    }
                                )
                            }
                        }

                        if (showLogoutDialog) {
                            AlertDialog(
                                onDismissRequest = { showLogoutDialog = false },
                                title = {
                                    Text("Confirm Logout")
                                },
                                text = {
                                    Text("Are you sure you want to log out? You will need to sign in again to access your account.")
                                },
                                confirmButton = {
                                    TextButton(
                                        onClick = {
                                            viewModel.clearSession()
                                            showLogoutDialog = false
                                        }
                                    ) {
                                        Text(
                                            "Logout",
                                            color = Color.Red
                                        )
                                    }
                                },
                                dismissButton = {
                                    TextButton(
                                        onClick = {
                                            showLogoutDialog = false
                                        }
                                    ) {
                                        Text(
                                            "Cancel",
                                            color = TextColor
                                        )
                                    }
                                },
                                shape = RoundedCornerShape(16.dp),
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .border(
                                1.dp,
                                BorderGray,
                                CircleShape
                            )
                            .clickable {
                                navController.popBackStack()
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
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {

                // Invisible placeholder
                IconButton(
                    onClick = {},
                    enabled = false
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = Color.Transparent
                    )
                }

                Text(
                    text = buildAnnotatedString {
                        append("${viewModel.amount} ")

                        withStyle(style = SpanStyle(color = PrimaryColor)) {
                            append(viewModel.currency)
                        }
                    },
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 35.sp,
                        color = TextColor
                    )
                )

                IconButton(
                    onClick = {
                        clipboardManager.setText(
                            AnnotatedString(viewModel.amount.toString())
                        )
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy Amount",
                        modifier = Modifier.size(20.dp),
                        tint = SubtleGray
                    )
                }
            }

            if (viewModel.isSubscription) {
                Text(
                    text = "${viewModel.period}",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextColor,
                        letterSpacing = 0.1.em,
                        fontSize = 18.sp
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(top = 2.dp)
                )
            } else {
                Text(
                    text = "Amount to Send",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = TextColor,
                        fontSize = 16.sp
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(top = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(CircleShape)
                    .background(Color.LightGray.copy(alpha = 0.08f))
                    .border(
                        1.dp,
                        BorderGray,
                        CircleShape
                    )
                    .padding(start = 12.dp, end = 4.dp, top = 2.4.dp, bottom = 2.4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = buildAnnotatedString {
                            withStyle(
                                SpanStyle(
                                    fontWeight = FontWeight.SemiBold,
                                    color = TertiaryTextColor
                                )
                            ) {
                                append("ID: ")
                            }

                            withStyle(
                                SpanStyle(
                                    fontWeight = FontWeight.SemiBold,
                                    color = SecondaryTextColor
                                )
                            ) {
                                append("$trxId")
                            }
                        },
                        modifier = Modifier.weight(1f),
                        fontSize = 13.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    IconButton(
                        onClick = {
                            clipboardManager.setText(
                                AnnotatedString(viewModel.transactionId)
                            )
                        },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy Transaction ID",
                            modifier = Modifier.size(16.dp),
                            tint = SubtleGray
                        )
                    }
                }
            }

            if (viewModel.environment == Environment.TEST.value) {
                Text(
                    text = "Test Mode",
                    fontSize = 14.sp,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color(0xFF18881C)
                    ),
                    modifier = Modifier
                        .padding(top = 7.dp)
                        .background(
                            color = Color(0xFFABE0AD),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .border(
                            width = 1.dp,
                            color = Color(0xFF2E8F32),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (viewModel.qrCodeUrl != null) {
                Base64Image(
                    base64String = viewModel.qrCodeUrl!!,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp)
            ) {
                Text(
                    text = "Wallet Address",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium,
                        color = TextColor
                    ),
                    fontSize = 18.sp,
                    modifier = Modifier.weight(1f)
                )

//                // QR Code Button
//                IconButton(
//                    onClick = { showQrDialog = true },
//                    modifier = Modifier.size(32.dp)
//                ) {
//                    Icon(
//                        imageVector = Icons.Filled.QrCode,
//                        contentDescription = "Show QR code",
//                        tint = Color.Gray,
//                        modifier = Modifier.size(20.dp)
//                    )
//                }

                Spacer(modifier = Modifier.width(8.dp))

                // Copy Button
                IconButton(
                    onClick = {
                        viewModel.walletAddress?.let { address ->

                            clipboardManager.setText(
                                AnnotatedString(address)
                            )
                        }
                    },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.ContentCopy,
                        contentDescription = "Copy Wallet Address",
                        tint = Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(7.dp))

            viewModel.walletAddress?.let {
                OutlinedTextField(
                    value = it,
                    onValueChange = {},
                    readOnly = true,
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth(),
                    textStyle = androidx.compose.ui.text.TextStyle(
                        fontSize = 16.sp
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = LineGray,
                        unfocusedBorderColor = LineGray,
                        disabledBorderColor = LineGray
                    ),
                )
            }

            Spacer(modifier = Modifier.height(7.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = buildAnnotatedString {
                        append("Please ")

                        withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = TextColor)) {
                            append("TRANSFER THE EXACT AMOUNT")
                        }

                        append(" shown above to the wallet address provided via the ")

                        withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = TextColor)) {
                            append(viewModel.network)
                        }

                        append(" network.")
                    },
                    textAlign = TextAlign.Start,
                    fontSize = 13.sp,
                    color = SecondaryTextColor,
                    modifier = Modifier.weight(1f)
                )

                if (!isGuest) {
                    Spacer(modifier = Modifier.width(7.dp))
                    Button(
                        onClick = {
                            showDialog = true
                            viewModel.resetTransactionId()
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Black),
                        contentPadding = PaddingValues(top = 13.dp, bottom = 13.dp, start = 20.dp, end = 20.dp)
                    ) {
                        Text(
                            text = "I have sent",
                            fontSize = 16.sp,
                            fontStyle = FontStyle.Italic
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            if (!isGuest) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Current Balance:",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Medium,
                            color = TextColor
                        ),
                        textAlign = TextAlign.Start,
                        fontSize = 18.sp,
                    )

                    val displayValue = if (viewModel.environment == Environment.TEST.value) {
                        "500000.00000000"
                    } else {
                        "$balance"
                    }

                    Text(
                        text = buildAnnotatedString {
                            append("$displayValue ")

                            withStyle(style = SpanStyle(color = PrimaryColor)) {
                                append(viewModel.currency)
                            }
                        },
                        textAlign = TextAlign.Start,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = TextColor
                        ),
                        modifier = Modifier.padding(start = 10.dp),
                        fontSize = 18.sp
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            CountdownTimer()

            Spacer(modifier = Modifier.height(16.dp))

            if (isGuest) {
                Button(
                    onClick = {
                        showDialog = true
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryColor
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(top = 15.dp, bottom = 15.dp)
                ) {
                    Text(
                        text = "I Have Sent",
                        fontSize = 19.sp
                    )
                }
            } else {
                Button(
                    onClick = {

                        val balance: Double = balance?.toDoubleOrNull() ?: 0.0

                        if (viewModel.environment == Environment.LIVE.value) {
                            if (viewModel.amount > balance) {
                                Toast.makeText(context, "Insufficient balance", Toast.LENGTH_SHORT).show()
                            } else {
                                viewModel.payViaCrypto()
                            }
                        } else
                            viewModel.openTestPayment("BALANCE_PAYMENT")
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryColor
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(top = 15.dp, bottom = 15.dp)
                ) {
                    Text(
                        text = "Pay via NowCrypto",
                        fontSize = 19.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            HowToPaySection(
                instructionMessage,
                instructionText1,
                instructionText2,
                instructionText3,
                instructionLink1,
                instructionLink2,
                instructionLink3
            )

            if (isGuest) {
                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Want to use your account balance?",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextColor
                    ),
                    fontSize = 17.sp,
                )

                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Text(
                        text = "Register",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = PrimaryColor,
                            fontWeight = FontWeight.Bold
                        ),
                        fontSize = 17.sp,
                        modifier = Modifier.clickable {
                            navController.navigate(Screen.RegistrationScreen.route)
                        }
                    )

                    Spacer(modifier = Modifier.width(7.dp))

                    Text(
                        text = "or",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextColor
                        ),
                        fontSize = 17.sp,
                    )

                    Spacer(modifier = Modifier.width(7.dp))

                    Text(
                        text = "Login",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = PrimaryColor,
                            fontWeight = FontWeight.Bold
                        ),
                        fontSize = 17.sp,
                        modifier = Modifier.clickable {
                            navController.navigate(Screen.LoginScreen.route)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            AppFooter(
                emailSupport = emailSupport,
                twitterLink = twitterLink,
                telegramLink = telegramLink
            )

            Spacer(modifier = Modifier.height(8.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 15.dp),
                horizontalAlignment =  Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Powered by",
                    style = MaterialTheme.typography.displayMedium.copy(
                        color = TertiaryTextColor,
                        lineHeight = 12.sp
                    ),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                Image(
                    painter = painterResource(id = R.drawable.nowcrypto_logo),
                    contentDescription = "NowCrypto Logo",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp)
                        .padding(bottom = 15.dp),
                    contentScale = ContentScale.Fit
                )
            }
        }
    }
}

@Composable
fun AddFundDialog(
    showDialog: Boolean,
    viewModel: PaymentViewModel = viewModel(),
    onDismissRequest: () -> Unit,
    onSuccess: (String) -> Unit,
    isGuest: Boolean
    ) {

    if (!showDialog) return

    val addFundUiState by viewModel.addFundUiState.collectAsState()
    val currentState = addFundUiState

    var transactionIdError by remember { mutableStateOf(false) }
    val timeLeft by viewModel.timeLeft.collectAsState()
    val focusRequester = remember { FocusRequester() }

    val minutes = timeLeft / 60
    val seconds = timeLeft % 60
    val formatted = String.format(Locale.US, "%02d:%02d", minutes, seconds)

    val totalTime = viewModel.totalTime.toFloat()
    val progress = (timeLeft / totalTime).coerceIn(0f, 1f)

    val isProcessing = addFundUiState is AddFundUiState.VerifyingBlocks

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Dialog(
        onDismissRequest = {
            if (!isProcessing) onDismissRequest() // Only close if NOT verifying blocks
        },
        properties = DialogProperties(
            dismissOnBackPress = !isProcessing, // Disable back button during verification
            dismissOnClickOutside = !isProcessing // Disable outside tap during verification
        )
    ) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color.White,
            tonalElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 600.dp)
        ) {

            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when(currentState) {
                    is AddFundUiState.VerifyingBlocks -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(
                                progress = { currentState.blocks.toFloat() / 19f },
                                modifier = Modifier.size(80.dp),
                                color = PrimaryColor,
                                strokeWidth = 6.dp,
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Confirming Transaction",
                                fontWeight = FontWeight.Bold,
                                color = TextColor
                            )
                            Text(
                                // 3. Use 'currentState' here as well
                                text = "Block ${currentState.blocks} of 19 confirmed...",
                                color = SecondaryTextColor
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Please do not close or leave this screen.",
                                color = Color.Red,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    AddFundUiState.Loading -> {
                        CircularProgressIndicator(
                            color = PrimaryColor
                        )
                        Text(
                            text = "Please wait...",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = TextColor
                            ),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 16.dp)
                        )
                    }
                    is AddFundUiState.Error -> {
                        LaunchedEffect(addFundUiState) {
                            delay(3000)
                            viewModel.resetAddFundToIdle()
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Error,
                                contentDescription = "Error",
                                tint = Color(0xFFF44336),
                                modifier = Modifier.size(80.dp)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = (addFundUiState as AddFundUiState.Error).message,
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    color = TextColor
                                ),
                                textAlign = TextAlign.Center,
                                fontSize = 18.sp
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            Button(
                                onClick = { viewModel.resetAddFundToIdle() },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor),
                                modifier = Modifier.fillMaxWidth(),
                                contentPadding = PaddingValues(vertical = 12.dp)
                            ) {
                                Text("Okay", color = Color.White)
                            }
                        }
                    }
                    is AddFundUiState.Success -> {
                        LaunchedEffect(addFundUiState) {
                            delay(2000)
                            onDismissRequest()
                        }

                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Success",
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(80.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = (addFundUiState as AddFundUiState.Success).message,
                            style = MaterialTheme.typography.headlineSmall.copy(
                                color = TextColor
                            ),
                            textAlign = TextAlign.Center,
                            fontSize = 18.sp
                        )
                    }
                    is AddFundUiState.PaymentSuccess -> {
                        if ((addFundUiState as AddFundUiState.PaymentSuccess).trxId != null) {
                            onSuccess(
                                (addFundUiState as AddFundUiState.PaymentSuccess).trxId!!
                            )
                            PaymentResultScreen(
                                true,
                                "Payment Successful!"
                            ) {
                                onSuccess(
                                    (addFundUiState as AddFundUiState.PaymentSuccess).trxId!!
                                )
                            }
                        } else {
                            Icon(
                                imageVector = Icons.Default.Error,
                                contentDescription = "Error",
                                tint = Color(0xFF4CAF50),
                                modifier = Modifier.size(80.dp)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "Transaction ID is null",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    color = TextColor
                                ),
                                textAlign = TextAlign.Center,
                                fontSize = 18.sp
                            )
                        }
                    }
                    AddFundUiState.Idle -> {
                        Text(
                            text = "Verify Payment",
                            fontSize = 19.sp,
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextColor
                            ),
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    width = 1.dp,
                                    color = LineGray,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(15.dp) // inner spacing so text & bar don’t touch the border
                        ) {
                            Column {
                                Text(
                                    text = "Time remaining: $formatted",
                                    fontSize = 14.sp,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = TextColor
                                    ),
                                    modifier = Modifier.align(Alignment.Start)
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                LinearProgressIndicator(
                                    progress = { progress },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(4.dp),
                                    color = Color(0xFF7BC739),
                                    trackColor = LineGray
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        val isTestMode = viewModel.environment == Environment.TEST.value

                        Text(
                            text = if (isTestMode) {
                                "TEST MODE: You can enter any 10 characters as the Transaction ID to simulate a successful payment."
                            } else {
                                "Please paste the TRON transaction ID from your ${viewModel.currency} wallet."
                            },
                            textAlign = TextAlign.Start,
                            fontSize = 15.sp,
                            color = if (isTestMode) PrimaryColor else SecondaryTextColor,
                            fontWeight = if (isTestMode) FontWeight.Bold else FontWeight.Normal
                        )

                        Spacer(modifier = Modifier.height(15.dp))

                        OutlinedTextField(
                            value = viewModel.transactionId,
                            onValueChange = viewModel::onTransactionIdChange,
                            isError = transactionIdError,
                            label = { Text("Transaction ID") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .focusRequester(focusRequester),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Next
                            ),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Black,
                                unfocusedBorderColor = Black,
                                disabledBorderColor = Black,
                                cursorColor = Color.Black,
                                focusedLabelColor = PrimaryColor
                            )
                        )

                        Spacer(modifier = Modifier.height(15.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Button(
                                onClick = { onDismissRequest() },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = White),
                                contentPadding = PaddingValues(top = 10.dp, bottom = 10.dp, start = 20.dp, end = 20.dp),
                                border = BorderStroke(1.dp, LineGray)
                            ) {
                                Text(
                                    "Cancel",
                                    color = TextColor
                                )
                            }

                            val isTestMode = viewModel.environment == Environment.TEST.value

                            Button(
                                onClick = {
                                    if (isTestMode && viewModel.transactionId.length != 10) {
                                        viewModel.setAddFundError("Test Mode: ID must be exactly 10 characters.")
                                    } else if (isTestMode) {
                                        if (isGuest) {
                                            // Guests still go to the dedicated test screen
                                            viewModel.openTestPayment("TRANSACTION_ID_PAYMENT")
                                        } else {
                                            // Logged-in users trigger the simulation directly
                                            viewModel.setAddFundSuccess()
                                        }
                                    } else {
                                        // Normal Live Logic
                                        if (isGuest) {
                                            viewModel.payViaTransactionId()
                                        } else {
                                            viewModel.addFund()
                                        }
                                    }
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor),
                                contentPadding = PaddingValues(vertical = 10.dp, horizontal = 20.dp)
                            ) {
                                Text("Submit")
                            }
                        }
                    }
                }
            }
        }
    }

}

@Composable
fun CountdownTimer(viewModel: PaymentViewModel = viewModel()) {
    val timeLeft by viewModel.timeLeft.collectAsState()
    val minutes = timeLeft / 60
    val seconds = timeLeft % 60
    val formatted = String.format(Locale.US, "%02d:%02d", minutes, seconds)

    val totalTime = viewModel.totalTime.toFloat()
    val progress = (timeLeft / totalTime).coerceIn(0f, 1f)

    Column(modifier = Modifier.fillMaxWidth()) {
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp),
            color = Color(0xFF7BC739),
            trackColor = LineGray
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = formatted,
            fontSize = 20.sp,
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.Bold,
                color = TextColor
            ),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
fun QrCodeDialog(
    showDialog: Boolean,
    qrCodeUrl: String?,
    onDismiss: () -> Unit
) {
    if (showDialog && qrCodeUrl != null) {

        Dialog(
            onDismissRequest = onDismiss
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = White,
                modifier = Modifier.padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Wallet QR Code",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextColor
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Base64Image(
                        base64String = qrCodeUrl,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Close", color = White)
                    }
                }
            }
        }
    }
}

@Composable
fun Base64Image(base64String: String, modifier: Modifier = Modifier) {
    val bitmap = decodeBase64ToBitmap(base64String)
    bitmap?.let {
        Image(
            bitmap = it.asImageBitmap(),
            contentDescription = "Receiving wallet qr code",
            modifier = modifier
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HowToPaySection(
    instructionMessage: String?,
    instructionText1: String?,
    instructionText2: String?,
    instructionText3: String?,
    instructionLink1: String?,
    instructionLink2: String?,
    instructionLink3: String?
) {
    val uriHandler = LocalUriHandler.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.LightGray.copy(alpha = 0.15f))
            .border(1.dp, BorderGray, RoundedCornerShape(12.dp))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally // Centers elements horizontally inside Column
    ) {
        Text(
            text = instructionMessage ?: "How to pay?",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = TextColor,
            textAlign = TextAlign.Center, // Centers text string alignment
            modifier = Modifier.fillMaxWidth()
        )

        // FlowRow automatically wraps items into 2+ lines if screen space runs out
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Pill 1
            if (!instructionText1.isNullOrBlank() && !instructionLink1.isNullOrBlank()) {
                PayMethodPill(
                    text = instructionText1,
                    modifier = Modifier.padding(horizontal = 4.dp) // This will work now!
                ) {
                    uriHandler.openUri(instructionLink1)
                }
            }

            // Pill 2
            if (!instructionText2.isNullOrBlank() && !instructionLink2.isNullOrBlank()) {
                PayMethodPill(
                    text = instructionText2,
                    modifier = Modifier.padding(horizontal = 4.dp)
                ) {
                    uriHandler.openUri(instructionLink2)
                }
            }

            // Pill 3
            if (!instructionText3.isNullOrBlank() && !instructionLink3.isNullOrBlank()) {
                PayMethodPill(
                    text = instructionText3,
                    modifier = Modifier.padding(horizontal = 4.dp)
                ) {
                    uriHandler.openUri(instructionLink3)
                }
            }
        }
    }
}

@Composable
fun PayMethodPill(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = Color.White,
        border = BorderStroke(
            1.dp,
            PrimaryColor
        ),
        modifier = modifier.clip(CircleShape)
    ) {
        Text(
            text = text,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = PrimaryColor,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AppFooter(
    emailSupport: String?,
    twitterLink: String?,
    telegramLink: String?
) {
    // Fetch the UriHandler instance
    val uriHandler = LocalUriHandler.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            thickness = 1.dp,
            color = BorderGray
        )

        // Underlined Legal / Nav Links Row
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Open the URI directly on click
            FooterLink(text = "Privacy Policy") { uriHandler.openUri("$BASE_URL/privacy-policy") }
            FooterBullet()
            FooterLink(text = "Terms of Service") { uriHandler.openUri("$BASE_URL/terms-conditions-policy") }
            FooterBullet()
            FooterLink(text = "Contact Us") { uriHandler.openUri("$BASE_URL/contact") }
            FooterBullet()
            FooterLink(text = "Create Ticket") { uriHandler.openUri("$BASE_URL/user/ticket") }
        }

        // Muted helper message
        Text(
            text = "Please contact us if you have any questions.",
            fontSize = 14.sp,
            color = SecondaryTextColor,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 0.dp)
        )

        // Social / Contact Tray
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (!emailSupport.isNullOrBlank()) {
                SocialCircularButton(icon = ImageVector.vectorResource(id = R.drawable.email), contentDescription = "Email Support") {
                    uriHandler.openUri("mailto:$emailSupport")
                }
            }
            if (!twitterLink.isNullOrBlank()) {
                SocialCircularButton(icon = ImageVector.vectorResource(id = R.drawable.x), contentDescription = "Twitter") {
                    uriHandler.openUri(twitterLink)
                }
            }
            if (!telegramLink.isNullOrBlank()) {
                SocialCircularButton(icon = ImageVector.vectorResource(id = R.drawable.telegram), contentDescription = "Telegram") {
                    uriHandler.openUri(telegramLink)
                }
            }
        }
    }
}

@Composable
fun FooterLink(
    text: String,
    onClick: () -> Unit
) {
    Text(
        text = text,
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        color = SecondaryTextColor,
        textDecoration = TextDecoration.Underline,
        modifier = Modifier
            .clip(CircleShape)
            .clickable { onClick() }
            .padding(horizontal = 4.dp)
    )
}

@Composable
fun FooterBullet() {
    Text(
        text = "•",
        fontSize = 12.sp,
        color = SecondaryTextColor.copy(alpha = 0.6f),
        modifier = Modifier.padding(horizontal = 4.dp)
    )
}

@Composable
fun SocialCircularButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(Color(0xFF2D3748)) // Sleek dark gray color canvas background
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = Color.White, // Enforced pure white asset tint
            modifier = Modifier.size(18.dp)
        )
    }
}

private fun decodeBase64ToBitmap(base64String: String): android.graphics.Bitmap? {
    return try {
        // Remove the "data:image/png;base64," prefix if present
        val cleanBase64 = base64String.substringAfter(",")
        val imageBytes = Base64.decode(cleanBase64, Base64.DEFAULT)
        BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}


