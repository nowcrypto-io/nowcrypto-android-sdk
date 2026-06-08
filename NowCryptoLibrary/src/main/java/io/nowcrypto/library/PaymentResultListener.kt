package io.nowcrypto.library

interface PaymentResultListener {
    fun onSuccess(transactionId: String)
    fun onFailure(errorMessage: String)
    fun onCancelled()
}
