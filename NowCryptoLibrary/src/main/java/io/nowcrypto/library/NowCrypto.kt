package io.nowcrypto.library

import android.content.Context
import android.content.Intent
import io.nowcrypto.library.data.NowCryptoResult
import io.nowcrypto.library.domain.currency.CurrencyUseCase
import io.nowcrypto.library.domain.payment_request_token.PaymentRequestTokenUseCase
import io.nowcrypto.library.domain.payment_request_token.SubRequestTokenUseCase
import io.nowcrypto.library.domain.payment_status.PaymentStatusUseCase
import io.nowcrypto.library.domain.subscription_list.SubscriptionListUseCase
import io.nowcrypto.library.presentation.MainActivity
import io.nowcrypto.library.remote.payment_status.PaymentStatusResponse
import io.nowcrypto.library.remote.subscription_list.NowCryptoSubscriptionItem
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object NowCrypto {
    private var paymentResultListener: PaymentResultListener? = null
    private var _publicKey: String? = null

    /**
     * Initialize the library once with your public API key.
     * Best called in Application.onCreate() or your main entry Activity.
     */
    fun init(publicKey: String) {
        this._publicKey = publicKey
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

        // Access internal Hilt dependencies
        val entryPoint = EntryPointAccessors.fromApplication(
            context.applicationContext,
            LibraryEntryPoint::class.java
        )
        val currencyUseCase = entryPoint.getCurrencyUseCase()

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
                            // Use Moshi to extract the "message" field from the raw JSON string
                            // (Assuming you have a Moshi instance available or just use a simple regex for speed)
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

        // Access internal Hilt dependencies
        val entryPoint = EntryPointAccessors.fromApplication(
            context.applicationContext,
            LibraryEntryPoint::class.java
        )
        val paymentRequestTokenUseCase = entryPoint.getPaymentRequestTokenUseCase()

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
                            // Use Moshi to extract the "message" field from the raw JSON string
                            // (Assuming you have a Moshi instance available or just use a simple regex for speed)
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

        // Access internal Hilt dependencies
        val entryPoint = EntryPointAccessors.fromApplication(
            context.applicationContext,
            LibraryEntryPoint::class.java
        )
        val subRequestTokenUseCase = entryPoint.getSubRequestTokenUseCase()

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
                            // Use Moshi to extract the "message" field from the raw JSON string
                            // (Assuming you have a Moshi instance available or just use a simple regex for speed)
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

        // Access internal Hilt dependencies
        val entryPoint = EntryPointAccessors.fromApplication(
            context.applicationContext,
            LibraryEntryPoint::class.java
        )
        val subscriptionListUseCase = entryPoint.getSubscriptionListUseCase()

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
                            // Use Moshi to extract the "message" field from the raw JSON string
                            // (Assuming you have a Moshi instance available or just use a simple regex for speed)
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

        val entryPoint = EntryPointAccessors.fromApplication(
            context.applicationContext,
            LibraryEntryPoint::class.java
        )
        val getPaymentStatusUseCase = entryPoint.getPaymentStatusUseCase()

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

    @dagger.hilt.EntryPoint
    @dagger.hilt.InstallIn(SingletonComponent::class)
    interface LibraryEntryPoint {
        fun getCurrencyUseCase(): CurrencyUseCase
        fun getPaymentRequestTokenUseCase(): PaymentRequestTokenUseCase
        fun getSubRequestTokenUseCase(): SubRequestTokenUseCase
        fun getSubscriptionListUseCase(): SubscriptionListUseCase
        fun getPaymentStatusUseCase(): PaymentStatusUseCase
    }
}