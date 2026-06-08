package io.nowcrypto.library.presentation.payment_screen

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.nowcrypto.library.Environment
import io.nowcrypto.library.data.session.SessionManager
import io.nowcrypto.library.domain.add_fund.AddFundUseCase
import io.nowcrypto.library.domain.device_id.DeviceIdProvider
import io.nowcrypto.library.domain.device_id.GetUserDetailsUseCase
import io.nowcrypto.library.domain.device_id.SendDeviceIdUseCase
import io.nowcrypto.library.domain.payment.PayViaCryptoUseCase
import io.nowcrypto.library.domain.payment.PayViaTransactionIdUseCase
import io.nowcrypto.library.domain.balance.FetchUserBalanceUseCase
import io.nowcrypto.library.domain.confirm_block.ConfirmBlockUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val deviceIdProvider: DeviceIdProvider,
    private val sendDeviceIdUseCase: SendDeviceIdUseCase,
    private val getUserDetailsUseCase: GetUserDetailsUseCase,
    private val fetchUserBalanceUseCase: FetchUserBalanceUseCase,
    private val payViaCryptoUseCase: PayViaCryptoUseCase,
    private val payViaTransactionIdUseCase: PayViaTransactionIdUseCase,
    private val confirmBlockUseCase: ConfirmBlockUseCase,
    private val addFundUseCase: AddFundUseCase,
    private val sessionManager: SessionManager
) : ViewModel() {

    var totalTime = 900 // 15 minutes in seconds
        private set
    private val _timeLeft = MutableStateFlow(totalTime)
    val timeLeft: StateFlow<Int> = _timeLeft.asStateFlow()

    private fun startDatabaseSyncTimer(dbTimestamp: Long) {
        this.unixTimeStamp = dbTimestamp

        viewModelScope.launch {
            while (true) {
                val currentTimeSeconds = System.currentTimeMillis() / 1000
                val expiryTime = unixTimeStamp + totalTime
                val remaining = (expiryTime - currentTimeSeconds).toInt()

                if (remaining <= 0) {
                    _timeLeft.value = 0
                    _paymentUiState.value = PaymentUiState.PaymentExpired("Payment Expired")
                    break // Stop the loop
                } else {
                    _timeLeft.value = remaining
                }

                delay(1000L)
            }
        }
    }

    var transactionId by mutableStateOf("")
        private set

    fun onTransactionIdChange(newValue: String) {
        transactionId = newValue
    }

    fun resetTransactionId() {
        transactionId = ""
    }

    private var registeredOrLoggedIn = false

    private val _paymentUiState = MutableStateFlow<PaymentUiState>(PaymentUiState.Idle)
    val paymentUiState: StateFlow<PaymentUiState> = _paymentUiState

    private val _addFundUiState = MutableStateFlow<AddFundUiState>(AddFundUiState.Idle)
    val addFundUiState: StateFlow<AddFundUiState> = _addFundUiState

    private val _supportedCurrencies = MutableLiveData<List<String>?>()
    val supportedCurrencies: LiveData<List<String>?> = _supportedCurrencies

    private val _balance = MutableLiveData<String?>()
    val balance: LiveData<String?> = _balance

    private val _isGuest = MutableStateFlow(true)
    val isGuest: StateFlow<Boolean> = _isGuest

    private val _userName = MutableStateFlow<String?>(null)
    val userName: MutableStateFlow<String?> = _userName

    private val _profilePictureUrl = MutableStateFlow<String?>(null)
    val profilePictureUrl: MutableStateFlow<String?> = _profilePictureUrl

    var walletAddress: String? = null
    var environment: String = Environment.TEST.value
        private set
    var amount: Double = 0.0
        private set
    var currency: String = ""
        private set
    var network: String = ""
        private set
    var qrCodeUrl: String? = null
        private set

    var unixTimeStamp: Long = 0L
        private set
    var isSubscription: Boolean = false
        private set
    var period: String? = null
        private set
    var paymentRequestToken: String = ""
        private set
    var apiKey: String? = null
       private set
    var deviceId: String? = null
        private set
    var token: String? = null
        private set

    fun setEnvironment(environment: String) {
        this.environment = environment
    }

    fun setPaymentRequestToken(paymentRequestToken: String) {
        this.paymentRequestToken = paymentRequestToken
    }

    fun setApiKey(apiKey: String) {
        this.apiKey = apiKey
    }

    private fun setDeviceId(deviceId: String) {
        this.deviceId = deviceId
    }

    fun resetToRegisterSuccess() {
        registeredOrLoggedIn = false
        registerDeviceOrGetExistingUser()
    }

    fun registerDeviceOrGetExistingUser() {

        viewModelScope.launch {

            if (sessionManager.isGuest()) {
                registerDevice()
            } else {
                getUserDetails()
            }
        }
    }

    fun clearSession() {
        viewModelScope.launch {
            sessionManager.clearSession()
            registeredOrLoggedIn = false
            _isGuest.value = true
            walletAddress = null
            token = null
            _supportedCurrencies.postValue(emptyList())
            _profilePictureUrl.value = null
            _userName.value = null
            registerDevice()
        }
    }

    suspend fun updateGuestStatus() {
        _isGuest.value = sessionManager.isGuest()
        _userName.value = sessionManager.getUsername()
        _profilePictureUrl.value = sessionManager.getProfilePictureUrl()
        startFetchingBalance()
    }

    suspend fun registerDevice() {

        if (registeredOrLoggedIn || apiKey == null) return

        _paymentUiState.value = PaymentUiState.RegisterDeviceLoading
        try {
            val deviceId = deviceIdProvider.getDeviceId()
            val result = sendDeviceIdUseCase.execute(deviceId, apiKey!!, paymentRequestToken)
            _paymentUiState.value = PaymentUiState.RegisterDeviceSuccess(result)

            if (result.walletAddress == null) {
                _paymentUiState.value = PaymentUiState.RegisterDeviceError("Wallet address is null")
            }

            _supportedCurrencies.postValue(result.supportedCurrencies)
            _balance.value = result.balance
            walletAddress = result.walletAddress
            unixTimeStamp = result.unixTimeStamp
            startDatabaseSyncTimer(unixTimeStamp)

            Log.d("PaymentViewModel", result.toString())
            registeredOrLoggedIn = true

            setDeviceId(deviceId)
            setEnvironment(if (result.environment == "LIVE") Environment.LIVE.value else Environment.TEST.value)

            if (result.amount != null && result.currency != null && result.network != null) {
                amount = result.amount.toDouble()
                currency = result.currency
                network = result.network
                qrCodeUrl = result.qrCode
            } else {
                _paymentUiState.value = PaymentUiState.RegisterDeviceError("Price or currency is invalid")
            }

            if (result.isSubscription != null && result.isSubscription && result.period != null) {
                period = result.period
                isSubscription = true
            }

            sessionManager.saveSession(
                "",
                true,
                null,
                null
            )

        } catch (e: retrofit2.HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorMessage = if (!errorBody.isNullOrEmpty()) {
                try {
                    // Parse JSON error response from Laravel
                    val json = JSONObject(errorBody)
                    json.optString("message", "Server error")
                } catch (_: Exception) {
                    "Server error: ${e.message()}"
                }
            } else {
                "Server error: ${e.message()}"
            }

            _paymentUiState.value = PaymentUiState.RegisterDeviceError(errorMessage)

        } catch (e: java.io.IOException) {
            _paymentUiState.value = PaymentUiState.RegisterDeviceError("Network error: ${e.localizedMessage}")

        } catch (e: Exception) {
            _paymentUiState.value = PaymentUiState.RegisterDeviceError("Unexpected error: ${e.localizedMessage}")
        }
    }

    suspend fun getUserDetails() {
        if (registeredOrLoggedIn || apiKey == null) return

        _paymentUiState.value = PaymentUiState.RegisterDeviceLoading
        try {
            val result = getUserDetailsUseCase.execute(apiKey!!, paymentRequestToken)
            _paymentUiState.value = PaymentUiState.RegisterDeviceSuccess(result)

            //Try register device if failed to log in using previous token
            //if (!result.success) {
            //    registerDevice()
            //}

            if (result.walletAddress == null) {
                _paymentUiState.value = PaymentUiState.RegisterDeviceError("Wallet address is null")
            }

            _supportedCurrencies.postValue(result.supportedCurrencies)
            _balance.value = result.balance
            walletAddress = result.walletAddress
            unixTimeStamp = result.unixTimeStamp
            startDatabaseSyncTimer(unixTimeStamp)

            Log.d("PaymentViewModel", result.toString())
            registeredOrLoggedIn = true

            setEnvironment(if (result.environment == "LIVE") Environment.LIVE.value else Environment.TEST.value)

            if (result.amount != null && result.currency != null && result.network != null) {
                amount = result.amount.toDouble()
                currency = result.currency
                network = result.network
                qrCodeUrl = result.qrCode
            } else {
                _paymentUiState.value = PaymentUiState.RegisterDeviceError("Price or currency is invalid")
            }

            if (result.isSubscription != null && result.isSubscription && result.period != null) {
                period = result.period
                isSubscription = true
            }

            if (result.token != null) {
                token = result.token
                sessionManager.saveSession(
                    result.token,
                    false,
                    result.userName,
                    result.profilePictureUrl
                )

                _isGuest.value = sessionManager.isGuest()
                _userName.value = sessionManager.getUsername()
                _profilePictureUrl.value = sessionManager.getProfilePictureUrl()
            }

            startFetchingBalance()

        } catch (e: retrofit2.HttpException) {

            if (e.code() == 401) {
                // Because TokenAuthenticator already ran sessionManager.clearToken(),
                // we just need to update the UI to force a re-login.
                Log.d("PaymentViewModel", "Session Expired")
                _paymentUiState.value = PaymentUiState.SessionExpired("Session expired. Please restart the process.")
                registeredOrLoggedIn = false
                return
            }

            val errorBody = e.response()?.errorBody()?.string()
            val errorMessage = if (!errorBody.isNullOrEmpty()) {
                try {
                    // Parse JSON error response from Laravel
                    val json = JSONObject(errorBody)
                    json.optString("message", "Server error")
                } catch (_: Exception) {
                    "Server error: ${e.message()}"
                }
            } else {
                "Server error: ${e.message()}"
            }

            _paymentUiState.value = PaymentUiState.RegisterDeviceError(errorMessage)

        } catch (e: java.io.IOException) {
            _paymentUiState.value = PaymentUiState.RegisterDeviceError("Network error: ${e.localizedMessage}")

        } catch (e: Exception) {
            _paymentUiState.value = PaymentUiState.RegisterDeviceError("Unexpected error: ${e.localizedMessage}")
        }
    }

    fun startFetchingBalance() {

        if (!registeredOrLoggedIn || apiKey == null || _isGuest.value) return

        viewModelScope.launch {
            try {
                val balanceResponse = fetchUserBalanceUseCase.execute(apiKey!!, currency)

                _balance.value = balanceResponse.balance

                Log.d("PaymentViewModel", "Wallets fetch result: $balanceResponse")

            } catch (e: retrofit2.HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                val errorMessage = if (!errorBody.isNullOrEmpty()) {
                    try {
                        val json = JSONObject(errorBody)
                        json.optString("message", "Server error")
                    } catch (_: Exception) {
                        "Server error: ${e.message()}"
                    }
                } else {
                    "Server error: ${e.message()}"
                }

                _paymentUiState.value = PaymentUiState.WalletError(errorMessage)
            } catch (e: java.io.IOException) {
                Log.d("Network error", "${e.localizedMessage}")
            } catch (e: Exception) {
                Log.d("Unexpected error","${e.localizedMessage}")
            }
        }
    }

    fun openTestPayment(paymentType: String) {
        _paymentUiState.value = PaymentUiState.TestPayment(paymentType)
    }

    fun payViaCrypto() {
        if (apiKey == null) {
            _paymentUiState.value = PaymentUiState.PaymentError("Invalid api key")
            return
        }

        if (paymentRequestToken.isBlank()) {
            _paymentUiState.value = PaymentUiState.PaymentError("Invalid payment request token")
            return
        }

        viewModelScope.launch {
            _paymentUiState.value = PaymentUiState.PaymentLoading
            try {
                val result = payViaCryptoUseCase.execute(apiKey!!,paymentRequestToken)
                Log.d("PaymentViewModel", "Payment fetch result: $result")

                if (result.success) {
                    _paymentUiState.value = PaymentUiState.PaymentSuccess(result)
                }
            } catch (e: retrofit2.HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                val errorMessage = if (!errorBody.isNullOrEmpty()) {
                    try {
                        val json = JSONObject(errorBody)
                        json.optString("message", "Server error")
                    } catch (_: Exception) {
                        "Server error: ${e.message()}"
                    }
                } else {
                    "Server error: ${e.message()}"
                }

                _paymentUiState.value = PaymentUiState.PaymentError(errorMessage)

            } catch (e: java.io.IOException) {
                _paymentUiState.value = PaymentUiState.PaymentError("Network error: ${e.localizedMessage}")

            } catch (e: Exception) {
                _paymentUiState.value = PaymentUiState.PaymentError("Unexpected error: ${e.localizedMessage}")
            }
        }
    }

    fun resetAddFundToIdle() {
        _addFundUiState.value = AddFundUiState.Idle
    }

    fun setAddFundError(message: String) {
        _addFundUiState.value = AddFundUiState.Error(message)
    }

    fun setAddFundSuccess() {
        _addFundUiState.value = AddFundUiState.Success("Test Payment Verified Successfully!")
    }

    fun addFund() {

        if (walletAddress == null)
            return

        viewModelScope.launch {

            _addFundUiState.value = AddFundUiState.Loading
            try {

                var confirmedBlocks = 0
                val maxBlocks = 19

                while (confirmedBlocks < maxBlocks) {
                    val result = confirmBlockUseCase.execute(transactionId)
                    // Assuming result contains the number of confirmed blocks
                    confirmedBlocks = result.confirmations

                    _addFundUiState.value = AddFundUiState.VerifyingBlocks(confirmedBlocks)

                    if (confirmedBlocks >= maxBlocks) break

                    delay(5000) // Poll every 5 seconds
                }

                val result = addFundUseCase.execute(apiKey!!, transactionId, paymentRequestToken, walletAddress!!)

                if (result.success) {
                    _addFundUiState.value = AddFundUiState.Success(result.message)
                } else {
                    _addFundUiState.value = AddFundUiState.Error(result.message)
                    delay(2000)
                    _addFundUiState.value = AddFundUiState.Idle
                }

                Log.d("PaymentViewModel", "$result")

            } catch (e: retrofit2.HttpException) {
                val errorBody = e.response()?.errorBody()?.string()

                if (e.code() == 401) {
                    // Because TokenAuthenticator already ran sessionManager.clearToken(),
                    // we just need to update the UI to force a re-login.
                    Log.d("PaymentViewModel", "Session Expired")
                    _paymentUiState.value = PaymentUiState.SessionExpired("Session expired. Please restart the process.")
                    registeredOrLoggedIn = false
                }

                val errorMessage = if (!errorBody.isNullOrEmpty()) {
                    try {
                        val json = JSONObject(errorBody)
                        json.optString("message", "Server error")
                    } catch (_: Exception) {
                        "Server error: ${e.message()}"
                    }
                } else {
                    "Server error: ${e.message()}"
                }

                Log.e("PaymentViewModel", errorMessage)

                _addFundUiState.value = AddFundUiState.Error(errorMessage)
                delay(2000)
                _addFundUiState.value = AddFundUiState.Idle
            } catch (e: java.io.IOException) {
                Log.e("PaymentViewModel", "Network error: ${e.localizedMessage}")
                _addFundUiState.value = AddFundUiState.Error("Network error: ${e.localizedMessage}")
                delay(2000)
                _addFundUiState.value = AddFundUiState.Idle
            } catch (e: Exception) {
                Log.e("PaymentViewModel", "Unexpected error: ${e.localizedMessage}")
                _addFundUiState.value = AddFundUiState.Error("Unexpected error: ${e.localizedMessage}")
                delay(2000)
                _addFundUiState.value = AddFundUiState.Idle
            }
        }
    }

    fun payViaTransactionId() {

        if (deviceId == null || apiKey == null)
            return

        viewModelScope.launch {

            _addFundUiState.value = AddFundUiState.Loading
            try {

                val isTestMode = environment == Environment.TEST.value

                // If in test mode and the ID is 10 chars, generate/use a dummy ID
                // that matches the backend's expected length/format
                val finalTrxId = if (isTestMode && transactionId.length == 10) {
                    "b7d4a51e62f01987c23a5b6d7e8f90123456789abcdef0123456789abcdef012" //Dummy transaction id
                } else {
                    transactionId
                }

                var confirmedBlocks = 0
                val maxBlocks = 19

                while (confirmedBlocks < maxBlocks) {
                    val result = confirmBlockUseCase.execute(transactionId)
                    // Assuming result contains the number of confirmed blocks
                    confirmedBlocks = result.confirmations

                    _addFundUiState.value = AddFundUiState.VerifyingBlocks(confirmedBlocks)

                    if (confirmedBlocks >= maxBlocks) break

                    delay(5000) // Poll every 5 seconds
                }

                val result = payViaTransactionIdUseCase.execute(deviceId!!,apiKey!!, finalTrxId, paymentRequestToken)

                if (result.success) {
                    if (result.trxId != null) {
                        // In test mode, we will be in test payment screen
                        if (environment == Environment.TEST.value)
                            _paymentUiState.value = PaymentUiState.PaymentSuccess(result)
                        else
                            _addFundUiState.value = AddFundUiState.PaymentSuccess(result.message, result.trxId)
                    }
                    else
                        _addFundUiState.value = AddFundUiState.Error("Transaction ID is null")
                } else {
                    _addFundUiState.value = AddFundUiState.Error(result.message)
                    delay(2000)
                    _addFundUiState.value = AddFundUiState.Idle
                }

                Log.d("PaymentViewModel", "$result")

            } catch (e: retrofit2.HttpException) {
                val errorBody = e.response()?.errorBody()?.string()

                if (e.code() == 401) {
                    // Because TokenAuthenticator already ran sessionManager.clearToken(),
                    // we just need to update the UI to force a re-login.
                    Log.d("PaymentViewModel", "Session Expired")
                    _paymentUiState.value = PaymentUiState.SessionExpired("Session expired. Please restart the process.")
                    registeredOrLoggedIn = false
                }

                val errorMessage = if (!errorBody.isNullOrEmpty()) {
                    try {
                        val json = JSONObject(errorBody)
                        json.optString("message", "Server error")
                    } catch (_: Exception) {
                        "Server error: ${e.message()}"
                    }
                } else {
                    "Server error: ${e.message()}"
                }

                Log.e("PaymentViewModel", errorMessage)

                _addFundUiState.value = AddFundUiState.Error(errorMessage)
                delay(2000)
                _addFundUiState.value = AddFundUiState.Idle
            } catch (e: java.io.IOException) {
                Log.e("PaymentViewModel", "Network error: ${e.localizedMessage}")
                _addFundUiState.value = AddFundUiState.Error("Network error: ${e.localizedMessage}")
                delay(2000)
                _addFundUiState.value = AddFundUiState.Idle
            } catch (e: Exception) {
                Log.e("PaymentViewModel", "Unexpected error: ${e.localizedMessage}")
                _addFundUiState.value = AddFundUiState.Error("Unexpected error: ${e.localizedMessage}")
                delay(2000)
                _addFundUiState.value = AddFundUiState.Idle
            }
        }
    }
}

