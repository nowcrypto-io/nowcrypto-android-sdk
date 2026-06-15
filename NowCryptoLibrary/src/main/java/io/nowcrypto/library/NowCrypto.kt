package io.nowcrypto.library

import android.content.Context
import android.content.Intent
import io.nowcrypto.library.data.NowCryptoResult
import io.nowcrypto.library.presentation.MainActivity
import io.nowcrypto.library.remote.payment_status.PaymentStatusResponse
import io.nowcrypto.library.remote.subscription.NowCryptoSubscriptionItem
import io.nowcrypto.library.domain.device_id.DeviceIdProvider
import io.nowcrypto.library.remote.subscription.NowCryptoSubscription
import io.nowcrypto.library.data.di.NowCryptoInternal
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object NowCrypto {
    private var paymentResultListener: PaymentResultListener? = null
    private var _publicKey: String? = null
    private var deviceIdProvider: DeviceIdProvider? = null

    /**
     * Initialize the library once with your public API key.
     * Best called in Application.onCreate() or your main entry Activity.
     */
    fun init(publicKey: String) {
        this._publicKey = publicKey
    }

    internal fun inject(deviceIdProvider: DeviceIdProvider) {
        this.deviceIdProvider = deviceIdProvider
    }

    fun getDeviceId(): String {
        return deviceIdProvider?.getDeviceId()
            ?: throw IllegalStateException("DeviceIdProvider not injected")
    }

    /**
     * Helper to ensure API key exists before performing actions
     */
    private fun getRequiredPublicKey(): String {
        return _publicKey ?: throw IllegalStateException(
            "NowCrypto is not initialized. Please call NowCrypto.init(publicKey) before use."
        )
    }

    /**
     * Launch the library's internal Payment Activity.
     * API Key is retrieved from the stored state.
     */
    fun launchPayment(
        context: Context,
        paymentRequestToken: String,
        listener: PaymentResultListener
    ) {
        val publicKey = getRequiredPublicKey()
        paymentResultListener = listener

        val intent = Intent(context, MainActivity::class.java).apply {
            putExtra(MainActivity.PAYMENT_REQUEST_TOKEN, paymentRequestToken)
            putExtra(MainActivity.API_KEY, publicKey)
        }
        context.startActivity(intent)
    }

    /**
    * Fetch supported currencies using the initialized API Key.
    */
    fun getSupportedCurrencies(
        context: Context,
        onResult: (NowCryptoResult<List<String>>) -> Unit
    ) {
        // Fail fast if the developer forgot to call NowCrypto.init()
        val publicKey = try {
            getRequiredPublicKey()
        } catch (e: IllegalStateException) {
            onResult(NowCryptoResult.Error(message = e.message ?: "NowCrypto not initialized"))
            return
        }

        // Access internal dependencies
        val internal = NowCryptoInternal.getInstance(context)
        val currencyUseCase = internal.currencyUseCase

        // Launch background request
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Passing the stored publicKey to the UseCase
                val response = currencyUseCase.execute(publicKey)

                withContext(Dispatchers.Main) {
                    if (response.success) {
                        onResult(NowCryptoResult.Success(response.supportedCurrencies))
                    } else {
                        onResult(NowCryptoResult.Error(
                            message = response.message,
                            status = response.status
                        ))
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    val errorMessage = if (e is retrofit2.HttpException) {
                        // Parse the JSON error body manually
                        try {
                            val errorJson = e.response()?.errorBody()?.string()
                            val regex = """"message"\s*:\s*"([^"]+)"""".toRegex()
                            regex.find(errorJson ?: "")?.groupValues?.get(1) ?: "Error: ${e.code()}"
                        } catch (parseException: Exception) {
                            "HTTP Error: ${parseException.message}"
                        }
                    } else {
                        e.localizedMessage ?: "Network request failed"
                    }

                    onResult(NowCryptoResult.Error(message = errorMessage, exception = e))
                }
            }
        }
    }

    /**
     * Generate payment request token
     */
    fun getPaymentRequestToken(
        context: Context,
        secretKey: String,
        amount: Double,
        currency: String,
        network: String,
        onResult: (NowCryptoResult<String>) -> Unit
    ) {

        // Access internal dependencies
        val internal = NowCryptoInternal.getInstance(context)
        val paymentRequestTokenUseCase = internal.paymentRequestTokenUseCase

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = paymentRequestTokenUseCase.execute(secretKey, amount, currency, network)

                withContext(Dispatchers.Main) {
                    if (response.success) {
                        onResult(NowCryptoResult.Success(response.paymentRequestToken))
                    } else {
                        onResult(NowCryptoResult.Error(
                            message = response.message,
                            status = response.status
                        ))
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    val errorMessage = if (e is retrofit2.HttpException) {
                        // Parse the JSON error body manually
                        try {
                            val errorJson = e.response()?.errorBody()?.string()
                            val regex = """"message"\s*:\s*"([^"]+)"""".toRegex()
                            regex.find(errorJson ?: "")?.groupValues?.get(1) ?: "Error: ${e.code()}"
                        } catch (parseException: Exception) {
                            "HTTP Error: ${parseException.message}}"
                        }
                    } else {
                        e.localizedMessage ?: "Network request failed"
                    }

                    onResult(NowCryptoResult.Error(message = errorMessage, exception = e))
                }
            }
        }
    }

    /**
     * Generate subscription payment request token
     */
    fun getSubscriptionRequestToken(
        context: Context,
        secretKey: String,
        amount: Double,
        currency: String,
        network: String,
        subId: String,
        onResult: (NowCryptoResult<String>) -> Unit
    ) {

        // Access internal dependencies
        val internal = NowCryptoInternal.getInstance(context)
        val subRequestTokenUseCase = internal.subRequestTokenUseCase

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = subRequestTokenUseCase.execute(secretKey, amount, currency, network, subId)

                withContext(Dispatchers.Main) {
                    if (response.success) {
                        onResult(NowCryptoResult.Success(response.paymentRequestToken))
                    } else {
                        onResult(NowCryptoResult.Error(
                            message = response.message,
                            status = response.status
                        ))
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    val errorMessage = if (e is retrofit2.HttpException) {
                        // Parse the JSON error body manually
                        try {
                            val errorJson = e.response()?.errorBody()?.string()
                            val regex = """"message"\s*:\s*"([^"]+)"""".toRegex()
                            regex.find(errorJson ?: "")?.groupValues?.get(1) ?: "Error: ${e.code()}"
                        } catch (parseException: Exception) {
                            "HTTP Error: ${parseException.message}}"
                        }
                    } else {
                        e.localizedMessage ?: "Network request failed"
                    }

                    onResult(NowCryptoResult.Error(message = errorMessage, exception = e))
                }
            }
        }
    }

    /**
     * Get subscription list
     */
    fun getSubscriptionList(
        context: Context,
        onResult: (NowCryptoResult<List<NowCryptoSubscriptionItem>>) -> Unit
    ) {

        // Fail fast if the developer forgot to call NowCrypto.init()
        val publicKey = try {
            getRequiredPublicKey()
        } catch (e: IllegalStateException) {
            onResult(NowCryptoResult.Error(message = e.message ?: "NowCrypto not initialized"))
            return
        }

        // Access internal dependencies
        val internal = NowCryptoInternal.getInstance(context)
        val subscriptionListUseCase = internal.subscriptionListUseCase

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = subscriptionListUseCase.execute(publicKey)

                withContext(Dispatchers.Main) {
                    if (response.success) {
                        onResult(NowCryptoResult.Success(response.subscriptions))
                    } else {
                        onResult(NowCryptoResult.Error(
                            message = response.message,
                            status = response.status
                        ))
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    val errorMessage = if (e is retrofit2.HttpException) {
                        // Parse the JSON error body manually
                        try {
                            val errorJson = e.response()?.errorBody()?.string()
                            val regex = """"message"\s*:\s*"([^"]+)"""".toRegex()
                            regex.find(errorJson ?: "")?.groupValues?.get(1) ?: "Error: ${e.code()}"
                        } catch (parseException: Exception) {
                            "HTTP Error: ${parseException.message}}"
                        }
                    } else {
                        e.localizedMessage ?: "Network request failed"
                    }

                    onResult(NowCryptoResult.Error(message = errorMessage, exception = e))
                }
            }
        }
    }

    /**
     * Get subscription list
     */
    fun queryActiveSubscription(
        context: Context,
        onResult: (NowCryptoResult<List<NowCryptoSubscription>>) -> Unit
    ) {

        // Fail fast if the developer forgot to call NowCrypto.init()
        val publicKey = try {
            getRequiredPublicKey()
        } catch (e: IllegalStateException) {
            onResult(NowCryptoResult.Error(message = e.message ?: "NowCrypto not initialized"))
            return
        }

        // Access internal dependencies
        val internal = NowCryptoInternal.getInstance(context)
        val getQueryActiveSubscriptionUseCase = internal.queryActiveSubscriptionUseCase

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = getQueryActiveSubscriptionUseCase.execute(publicKey, "")

                withContext(Dispatchers.Main) {
                    if (response.success) {
                        onResult(NowCryptoResult.Success(response.subscriptions))
                    } else {
                        onResult(NowCryptoResult.Error(
                            message = response.message,
                            status = response.status
                        ))
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    val errorMessage = if (e is retrofit2.HttpException) {
                        // Parse the JSON error body manually
                        try {
                            val errorJson = e.response()?.errorBody()?.string()
                            val regex = """"message"\s*:\s*"([^"]+)"""".toRegex()
                            regex.find(errorJson ?: "")?.groupValues?.get(1) ?: "Error: ${e.code()}"
                        } catch (parseException: Exception) {
                            "HTTP Error: ${parseException.message}}"
                        }
                    } else {
                        e.localizedMessage ?: "Network request failed"
                    }

                    onResult(NowCryptoResult.Error(message = errorMessage, exception = e))
                }
            }
        }
    }

    /**
     * Get payment status
     */
    fun getPaymentStatus(
        context: Context,
        paymentRequestToken: String? = null,
        trxId: String? = null,
        onResult: (NowCryptoResult<PaymentStatusResponse>) -> Unit // Return the data class directly
    ) {

        val publicKey = try {
            getRequiredPublicKey()
        } catch (e: IllegalStateException) {
            onResult(NowCryptoResult.Error(message = e.message ?: "NowCrypto not initialized"))
            return
        }

        // Validate that at least one identifier exists
        if (paymentRequestToken.isNullOrBlank() && trxId.isNullOrBlank()) {
            onResult(NowCryptoResult.Error("Either Payment Token or Transaction ID is required"))
            return
        }

        val internal = NowCryptoInternal.getInstance(context)
        val getPaymentStatusUseCase = internal.paymentStatusUseCase

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = getPaymentStatusUseCase.execute(
                    publicKey = publicKey,
                    paymentRequestToken = paymentRequestToken,
                    trxId = trxId
                )

                withContext(Dispatchers.Main) {
                    if (response.success) {
                        onResult(NowCryptoResult.Success(response))
                    } else {
                        onResult(NowCryptoResult.Error(message = response.message))
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    // Handle HTTP errors or Connection issues
                    onResult(NowCryptoResult.Error(message = e.localizedMessage ?: "Failed to fetch status"))
                }
            }
        }
    }

    // Internal Notifiers for Activity Communication
    internal fun notifySuccess(transactionId: String) {
        paymentResultListener?.onSuccess(transactionId)
        paymentResultListener = null
    }

    internal fun notifyFailure(errorMessage: String) {
        paymentResultListener?.onFailure(errorMessage)
        paymentResultListener = null
    }

    internal fun notifyCancelled() {
        paymentResultListener?.onCancelled()
        paymentResultListener = null
    }
}