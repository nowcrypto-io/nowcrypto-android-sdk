package io.nowcrypto.payments

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import io.nowcrypto.library.NowCrypto
import io.nowcrypto.library.data.NowCryptoResult
import io.nowcrypto.library.remote.payment_status.PaymentStatusResponse
import io.nowcrypto.library.remote.subscription.NowCryptoSubscriptionItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MainViewModel : ViewModel() {
    // Live Keys
    var publicLiveKey by mutableStateOf("public_live_qU0r64zl5kd579YZEA6ZIy4cfkzAY1VccWlFu99TiWSM1CxZLATvVT3G8xQ9lLyq")
    var secretLiveKey by mutableStateOf("secret_live_vnzaL5aMF13sertJdGcmDVaKLmKxoOB2Y4vM9bZQlNhr6tsN2RcmWBwYFa9RZMkd")

    // Test Keys
    var publicTestKey by mutableStateOf("public_test_qG4ia6iAfso8phhJ3WCzSurcBoXxvY33hjU62nUWz5qp4PPI7fGoVKyf4TVwE2bW")
    var secretTestKey by mutableStateOf("secret_test_9DcyorTdgzkQ3p0kw0TduGvm4XtB1Qry6ec0WbKwvPZFslw7oAehghh5Yvl9Ac8h")

    var isTestMode by mutableStateOf(false)

    // These are the keys the Library will actually receive
    val activePublicKey get() = if (isTestMode) publicTestKey else publicLiveKey
    val activeSecretKey get() = if (isTestMode) secretTestKey else secretLiveKey

    var isInitialized by mutableStateOf(false)
    var isLoading by mutableStateOf(false)
    var supportedCurrencies = mutableStateListOf<String>()
    var notifMessage by mutableStateOf<String?>(null)

    private var _paymentRequestToken = MutableStateFlow<String?>(null)
    val paymentRequestToken: StateFlow<String?> = _paymentRequestToken

    private var _trxId = MutableStateFlow<String?>(null)
    val trxId: StateFlow<String?> = _trxId

    private var _paymentStatus = MutableStateFlow<PaymentStatusResponse?>(null)
    val paymentStatus: StateFlow<PaymentStatusResponse?> = _paymentStatus

    fun setTransactionId(id: String?) {
        _trxId.value = id
    }

    private var _subId = MutableStateFlow<String?>(null)
    val subId: StateFlow<String?> = _subId

    private var _subscriptionList = MutableStateFlow<List<NowCryptoSubscriptionItem>?>(null)
    val subscriptionList: StateFlow<List<NowCryptoSubscriptionItem>?> = _subscriptionList

    fun setSubscriptionId(id: String?) {
        _subId.value = id
    }

    fun initializeLibrary(context: Context) {
        if (activePublicKey.isBlank()) return

        isLoading = true
        notifMessage = null

        // Initialize the library singleton
        NowCrypto.init(activePublicKey)

        // Fetch Currencies
        NowCrypto.getSupportedCurrencies(context) { result ->
            isLoading = false
            when (result) {
                is NowCryptoResult.Success -> {
                    supportedCurrencies.clear()
                    supportedCurrencies.addAll(result.data)
                    isInitialized = true
                }
                is NowCryptoResult.Error -> {
                    notifMessage = result.message
                    isInitialized = false
                }

                is NowCryptoResult.Loading -> {}
            }
        }
    }

    fun generatePaymentRequestToken(
        context: Context,
        amount: Double,
        currency: String,
        network: String
    ) {
        if (activeSecretKey.isBlank()) return

        isLoading = true
        notifMessage = null

        // Fetch Currencies
        NowCrypto.getPaymentRequestToken(context, activeSecretKey, amount, currency, network) { result ->
            isLoading = false
            when (result) {
                is NowCryptoResult.Success -> {
                    _paymentRequestToken.value = result.data
                }
                is NowCryptoResult.Error -> {
                    notifMessage = result.message
                    isInitialized = false
                }

                is NowCryptoResult.Loading -> {}
            }
        }
    }

    fun generateSubscriptionRequestToken(
        context: Context,
        amount: Double,
        currency: String,
        network: String
    ) {
        if (activeSecretKey.isBlank() || _subId.value == null) return

        isLoading = true
        notifMessage = null

        // Fetch Currencies
        NowCrypto.getSubscriptionRequestToken(context, activeSecretKey, amount, currency, network, _subId.value!!) { result ->
            isLoading = false
            when (result) {
                is NowCryptoResult.Success -> {
                    _paymentRequestToken.value = result.data
                }
                is NowCryptoResult.Error -> {
                    notifMessage = result.message
                    isInitialized = false
                }

                is NowCryptoResult.Loading -> {}
            }
        }
    }

    fun getSubscriptionList(context: Context, onError: () -> Unit) {
        if (activeSecretKey.isBlank()) return

        isLoading = true
        notifMessage = null

        NowCrypto.getSubscriptionList(context) { result ->
            isLoading = false
            when (result) {
                is NowCryptoResult.Success -> {
                    _subscriptionList.value = result.data
                }
                is NowCryptoResult.Error -> {
                    // Set the error message for the SnackBar
                    notifMessage = result.message
                    _subscriptionList.value = null
                    // Trigger the callback to uncheck the box in the UI
                    onError()
                }
                is NowCryptoResult.Loading -> {}
            }
        }
    }

    fun getPaymentStatus(context: Context, onError: () -> Unit) {

        isLoading = true
        notifMessage = null

        // We pass both, the API will handle whichever is available
        NowCrypto.getPaymentStatus(context, paymentRequestToken.value, _trxId.value) { result ->
            isLoading = false
            when (result) {
                is NowCryptoResult.Success -> {
                    _paymentStatus.value = result.data
                }
                is NowCryptoResult.Error -> {
                    notifMessage = result.message
                    _paymentStatus.value = null
                    onError()
                }
                is NowCryptoResult.Loading -> {}
            }
        }
    }
}