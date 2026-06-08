package io.nowcrypto.payments

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.nowcrypto.library.NowCrypto
import io.nowcrypto.library.PaymentResultListener
import io.nowcrypto.library.remote.subscription_list.NowCryptoSubscriptionItem
import io.nowcrypto.payments.ui.theme.NowCryptoPaymentsTheme
import java.util.Locale.getDefault

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NowCryptoPaymentsTheme(darkTheme = true) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PaymentRequestScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentRequestScreen(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = viewModel()
) {
    val context = LocalContext.current
    val snackBarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()

    val apiGeneratedToken by viewModel.paymentRequestToken.collectAsState()
    val subscriptions by viewModel.subscriptionList.collectAsState()

    // UI State
    var inputAmount by remember { mutableStateOf("") }
    var selectedCurrency by remember { mutableStateOf("") }
    var isCurrencyExpanded by remember { mutableStateOf(false) }

    // --- Network State ---
    val networks = listOf("TRC20", "ERC20")
    var selectedNetwork by remember { mutableStateOf(networks[0]) }
    var isNetworkExpanded by remember { mutableStateOf(false) }
    // ---------------------------

    val currentTrxId by viewModel.trxId.collectAsState()
    val statusResponse by viewModel.paymentStatus.collectAsState()

    // Subscription State
    var isSubscriptionChecked by remember { mutableStateOf(false) }
    var selectedSubscription by remember { mutableStateOf<NowCryptoSubscriptionItem?>(null) }
    var isSubDropdownExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(isSubscriptionChecked) {
        if (isSubscriptionChecked && subscriptions.isNullOrEmpty()) {
            viewModel.getSubscriptionList(context) {
                isSubscriptionChecked = false
            }
        }
    }

    LaunchedEffect(viewModel.notifMessage) {
        viewModel.notifMessage?.let { message ->
            snackBarHostState.showSnackbar(message = message, duration = SnackbarDuration.Short)
            viewModel.notifMessage = null
        }
    }

    LaunchedEffect(viewModel.isInitialized) {
        if (viewModel.isInitialized && viewModel.supportedCurrencies.isNotEmpty()) {
            selectedCurrency = viewModel.supportedCurrencies.first()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        containerColor = Color.Transparent
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .imePadding()
                .padding(paddingValues)
                .padding(top = 40.dp, start = 20.dp, end = 20.dp, bottom = 20.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Gateway Testing Tool",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    val pubKey = if (viewModel.isTestMode) viewModel.publicTestKey else viewModel.publicLiveKey
                    val secKey = if (viewModel.isTestMode) viewModel.secretTestKey else viewModel.secretLiveKey

                    OutlinedTextField(
                        value = pubKey,
                        onValueChange = {
                            if (viewModel.isTestMode) viewModel.publicTestKey = it else viewModel.publicLiveKey = it
                        },
                        label = { Text(if (viewModel.isTestMode) "Public Test Key" else "Public Live Key") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !viewModel.isInitialized,
                        singleLine = true,
                        textStyle = LocalTextStyle.current.copy(fontSize = 11.sp)
                    )

                    OutlinedTextField(
                        value = secKey,
                        onValueChange = {
                            if (viewModel.isTestMode) viewModel.secretTestKey = it else viewModel.secretLiveKey = it
                        },
                        label = { Text(if (viewModel.isTestMode) "Secret Test Key" else "Secret Live Key") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !viewModel.isInitialized,
                        singleLine = true,
                        textStyle = LocalTextStyle.current.copy(fontSize = 11.sp)
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(enabled = !viewModel.isInitialized) {
                                viewModel.isTestMode = !viewModel.isTestMode
                            }
                            .padding(vertical = 8.dp)
                    ) {
                        Checkbox(
                            checked = viewModel.isTestMode,
                            onCheckedChange = { viewModel.isTestMode = it },
                            enabled = !viewModel.isInitialized
                        )
                        Text(
                            text = "Enable Test Mode",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                if (!viewModel.isInitialized) {
                    Button(
                        onClick = { viewModel.initializeLibrary(context) },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        enabled = !viewModel.isLoading
                    ) {
                        if (viewModel.isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                        } else {
                            val mode = if (viewModel.isTestMode) "TEST" else "LIVE"
                            Text("INITIALIZE $mode LIBRARY")
                        }
                    }
                }

                if (viewModel.isInitialized) {
                    HorizontalDivider(color = Color.DarkGray, thickness = 0.5.dp)

                    // --- Network Selection Dropdown ---
                    ExposedDropdownMenuBox(
                        expanded = isNetworkExpanded,
                        onExpandedChange = { isNetworkExpanded = !isNetworkExpanded },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = selectedNetwork,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Network") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isNetworkExpanded) },
                            modifier = Modifier.menuAnchor(
                                type = ExposedDropdownMenuAnchorType.PrimaryNotEditable,
                                enabled = true
                            ).fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = isNetworkExpanded,
                            onDismissRequest = { isNetworkExpanded = false }
                        ) {
                            networks.forEach { network ->
                                DropdownMenuItem(
                                    text = { Text(network) },
                                    onClick = {
                                        selectedNetwork = network
                                        isNetworkExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Amount and Currency Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ExposedDropdownMenuBox(
                            expanded = isCurrencyExpanded,
                            onExpandedChange = { isCurrencyExpanded = !isCurrencyExpanded },
                            modifier = Modifier.width(120.dp)
                        ) {
                            OutlinedTextField(
                                value = selectedCurrency,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("CCY") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isCurrencyExpanded) },
                                modifier = Modifier.menuAnchor(
                                    type = ExposedDropdownMenuAnchorType.PrimaryNotEditable,
                                    enabled = true
                                )
                            )
                            ExposedDropdownMenu(
                                expanded = isCurrencyExpanded,
                                onDismissRequest = { isCurrencyExpanded = false }
                            ) {
                                viewModel.supportedCurrencies.forEach { ccy ->
                                    DropdownMenuItem(
                                        text = { Text(ccy) },
                                        onClick = { selectedCurrency = ccy; isCurrencyExpanded = false }
                                    )
                                }
                            }
                        }

                        OutlinedTextField(
                            value = inputAmount,
                            onValueChange = { if (it.isEmpty() || it.matches(Regex("""^\d*\.?\d*$"""))) inputAmount = it },
                            label = { Text("Amount") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Checkbox(
                            checked = isSubscriptionChecked,
                            onCheckedChange = { checked ->
                                isSubscriptionChecked = checked
                                if (!checked) {
                                    viewModel.setSubscriptionId(null)
                                    selectedSubscription = null
                                }
                            },
                            enabled = !viewModel.isLoading
                        )
                        Text("Subscription", style = MaterialTheme.typography.bodyMedium)

                        if (viewModel.isLoading && isSubscriptionChecked && subscriptions.isNullOrEmpty()) {
                            Spacer(Modifier.width(8.dp))
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                        }
                    }

                    if (isSubscriptionChecked) {
                        ExposedDropdownMenuBox(
                            expanded = isSubDropdownExpanded,
                            onExpandedChange = { isSubDropdownExpanded = !isSubDropdownExpanded },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = selectedSubscription?.subName ?: "Select a subscription",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Select Plan") },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = isSubDropdownExpanded)
                                },
                                modifier = Modifier.menuAnchor(
                                    type = ExposedDropdownMenuAnchorType.PrimaryNotEditable,
                                    enabled = true
                                )
                            )

                            ExposedDropdownMenu(
                                expanded = isSubDropdownExpanded,
                                onDismissRequest = { isSubDropdownExpanded = false }
                            ) {
                                subscriptions?.forEach { sub ->
                                    DropdownMenuItem(
                                        text = { Text(sub.subName) },
                                        onClick = {
                                            selectedSubscription = sub
                                            isSubDropdownExpanded = false
                                            viewModel.setSubscriptionId(sub.subId)
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Button(
                        onClick = {
                            val amountDouble = inputAmount.toDoubleOrNull() ?: 0.0
                            if (isSubscriptionChecked) {
                                if (viewModel.subId.value == null) {
                                    viewModel.notifMessage = "Please select a subscription plan"
                                } else {
                                    // Passed selectedNetwork here
                                    viewModel.generateSubscriptionRequestToken(context, amountDouble, selectedCurrency, selectedNetwork)
                                }
                            } else {
                                // Passed selectedNetwork here
                                viewModel.generatePaymentRequestToken(context, amountDouble, selectedCurrency, selectedNetwork)
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        enabled = !viewModel.isLoading && inputAmount.isNotEmpty(),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                    ) {
                        if (viewModel.isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        } else {
                            Text(
                                text = if (isSubscriptionChecked) "GENERATE SUBSCRIPTION TOKEN" else "GENERATE TOKEN",
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }

                    OutlinedTextField(
                        value = apiGeneratedToken ?: "",
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("PAYMENT REQUEST TOKEN") },
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = Color.Cyan,
                            unfocusedTextColor = Color.Cyan,
                            focusedContainerColor = Color(0xFF121212),
                            unfocusedContainerColor = Color(0xFF121212)
                        )
                    )

                    if (currentTrxId != null) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
                            border = BorderStroke(0.5.dp, Color.DarkGray)
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Column {
                                    Text("Latest Transaction ID", fontSize = 11.sp, color = Color.Gray)
                                    Text(currentTrxId!!, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }

                                Button(
                                    onClick = { viewModel.getPaymentStatus(context) {} },
                                    modifier = Modifier.fillMaxWidth().height(48.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                                    enabled = !viewModel.isLoading
                                ) {
                                    Text("CHECK PAYMENT STATUS")
                                }

                                statusResponse?.let { res ->
                                    val isSub = res.subscription != null
                                    val rawStatus = (res.subscription?.status ?: res.transaction?.status ?: "unknown").lowercase()
                                    val totalAmount = when {
                                        res.subscription != null -> "${res.subscription?.amount} ${res.subscription?.currency}"
                                        res.transaction != null -> "${res.transaction?.amount} ${res.transaction?.currency}"
                                        else -> "0.00"
                                    }

                                    val statusColor = when(rawStatus.lowercase()) {
                                        "success" -> Color.Green
                                        "pending" -> Color(0xFFFFB300)
                                        "failed", "canceled" -> Color.Red
                                        else -> Color.Gray
                                    }

                                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text("Status: ", fontSize = 14.sp, color = Color.LightGray)
                                            Text(
                                                text = rawStatus.uppercase(getDefault()),
                                                fontSize = 16.sp,
                                                color = statusColor
                                            )
                                        }

                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text("Is Subscription: ", fontSize = 14.sp, color = Color.LightGray)
                                            Text(
                                                text = if (isSub) "YES" else "NO",
                                                fontSize = 16.sp,
                                                color = Color.White
                                            )
                                        }

                                        res.subscription?.expiration?.let {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text("Expires In: ", fontSize = 14.sp, color = Color.LightGray)
                                                Text(
                                                    text = it,
                                                    fontSize = 16.sp,
                                                    color = Color.White
                                                )
                                            }
                                        }

                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text("Total Amount: ", fontSize = 14.sp, color = Color.LightGray)
                                            Text(
                                                text = totalAmount,
                                                fontSize = 16.sp,
                                                color = Color.White
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            if (viewModel.isInitialized) {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        apiGeneratedToken?.let { token ->
                            NowCrypto.launchPayment(
                                context = context,
                                paymentRequestToken = token,
                                listener = object : PaymentResultListener {
                                    override fun onSuccess(transactionId: String) {
                                        viewModel.notifMessage = "Success: $transactionId"
                                        viewModel.setTransactionId(transactionId)
                                    }
                                    override fun onFailure(errorMessage: String) {
                                        viewModel.notifMessage = "Error: $errorMessage"
                                    }
                                    override fun onCancelled() {
                                        viewModel.notifMessage = "Cancelled"
                                    }
                                }
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(64.dp),
                    enabled = !apiGeneratedToken.isNullOrEmpty(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3D5AFE))
                ) {
                    Text("LAUNCH PAYMENT", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
fun PaymentRequestPreview() {
    NowCryptoPaymentsTheme(darkTheme = true) {
        Surface(color = Color.Black) {
            PaymentRequestScreen()
        }
    }
}