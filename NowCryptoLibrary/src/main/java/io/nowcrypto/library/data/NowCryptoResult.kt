package io.nowcrypto.library.data

import androidx.annotation.Keep

@Keep
sealed class NowCryptoResult<out T> {
    @Keep
    data class Success<T>(val data: T) : NowCryptoResult<T>()

    @Keep
    data class Error(
        val message: String,
        val status: String? = "error",
        val exception: Throwable? = null
    ) : NowCryptoResult<Nothing>()

    @Keep
    object Loading : NowCryptoResult<Nothing>()
}