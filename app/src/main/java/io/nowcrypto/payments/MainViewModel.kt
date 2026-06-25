package io.nowcrypto.payments

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import io.nowcrypto.sdk.NowCrypto
import io.nowcrypto.sdk.NowCryptoListener
import io.nowcrypto.sdk.remote.payment_status.PaymentStatusResponse
import io.nowcrypto.sdk.remote.subscription.ActiveSubscriptionResult
import io.nowcrypto.sdk.remote.subscription.NowCryptoSubscriptionItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MainViewModel : ViewModel() {
    // Live Keys
    var publicLiveKey by mutableStateOf("public_live_wTCU35rAVWDUfBt2IhuRDUYUYMxicTUUUh8xXGGZXCO8mWFZhk380CuItlB2xBMu")
    var secretLiveKey by mutableStateOf("secret_live_DsQ2ZqRdK9IGKnyHs5EdIzsTTGtdX1JJduqnI2Kge8rKoPI2Gd8wfiSOSAgQkRYm")

    // Test Keys
    var publicTestKey by mutableStateOf("public_test_Qa38pWs3maJ48zHvhyQO1f6xJIExRARSyW4aaCYQJ9pszxzfyJCzWHdpGyK6MK8x")
    var secretTestKey by mutableStateOf("secret_test_cWjRSEg9klcoPzeh5GYdURFzAqRTp1L8UN6RgYLrtwCle81Z1VADRTCBytLeDwmr")

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

    private val _activeSubscriptionResult =
        MutableStateFlow<ActiveSubscriptionResult?>(null)

    val activeSubscriptionResult: StateFlow<ActiveSubscriptionResult?> = _activeSubscriptionResult

    fun setSubscriptionId(id: String?) {
        _subId.value = id
    }

    fun initializeLibrary(context: Context) {
        if (activePublicKey.isBlank()) return

        isLoading = true
        notifMessage = null

        // Initialize the library singleton
        NowCrypto.init(activePublicKey)

        // Fetch Currencies using the Listener pattern
        NowCrypto.getSupportedCurrencies(context, object : NowCryptoListener<List<String>> {
            override fun onSuccess(data: List<String>) {
                isLoading = false
                supportedCurrencies.clear()
                supportedCurrencies.addAll(data)
                isInitialized = true
            }

            override fun onError(message: String) {
                isLoading = false
                notifMessage = message
                isInitialized = false
            }
        })
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

        NowCrypto.getPaymentRequestToken(context, activeSecretKey, amount, currency, network, object : NowCryptoListener<String> {
            override fun onSuccess(data: String) {
                isLoading = false
                _paymentRequestToken.value = data
            }

            override fun onError(message: String) {
                isLoading = false
                notifMessage = message
            }
        })
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

        NowCrypto.getSubscriptionRequestToken(context, activeSecretKey, amount, currency, network, _subId.value!!, object : NowCryptoListener<String> {
            override fun onSuccess(data: String) {
                isLoading = false
                _paymentRequestToken.value = data
            }

            override fun onError(message: String) {
                isLoading = false
                notifMessage = message
            }
        })
    }

    fun getSubscriptionList(context: Context, onError: () -> Unit) {
        if (activeSecretKey.isBlank()) return

        isLoading = true
        notifMessage = null

        NowCrypto.getSubscriptionList(context, object : NowCryptoListener<List<NowCryptoSubscriptionItem>> {
            override fun onSuccess(data: List<NowCryptoSubscriptionItem>) {
                isLoading = false
                _subscriptionList.value = data
            }

            override fun onError(message: String) {
                isLoading = false
                notifMessage = message
                _subscriptionList.value = null
                onError()
            }
        })
    }

    fun getPaymentStatus(context: Context, onError: () -> Unit) {
        isLoading = true
        notifMessage = null

        NowCrypto.getPaymentStatus(context, paymentRequestToken.value, _trxId.value, object : NowCryptoListener<PaymentStatusResponse> {
            override fun onSuccess(data: PaymentStatusResponse) {
                isLoading = false
                _paymentStatus.value = data
            }

            override fun onError(message: String) {
                isLoading = false
                notifMessage = message
                _paymentStatus.value = null
                onError()
            }
        })
    }

    fun getActiveSubscription(context: Context, onError: () -> Unit) {
        isLoading = true
        notifMessage = null

        NowCrypto.queryActiveSubscription(context, object : NowCryptoListener<ActiveSubscriptionResult> {
            override fun onSuccess(data: ActiveSubscriptionResult) {
                isLoading = false
                _activeSubscriptionResult.value = data
            }

            override fun onError(message: String) {
                isLoading = false
                notifMessage = message
                _activeSubscriptionResult.value = null
                onError()
            }
        })
    }
}
