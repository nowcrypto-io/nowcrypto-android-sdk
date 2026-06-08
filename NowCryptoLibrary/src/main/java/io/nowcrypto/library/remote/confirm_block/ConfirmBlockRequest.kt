package io.nowcrypto.library.remote.confirm_block

import androidx.annotation.Keep

@Keep
data class ConfirmBlockRequest(val transactionId: String)