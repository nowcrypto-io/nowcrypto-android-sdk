package io.nowcrypto.library.data.di.confirm_block

import io.nowcrypto.library.remote.confirm_block.ConfirmBlockResponse

interface ConfirmBlockRepository {
    suspend fun getConfirmedBlocksCount(
        transactionId: String
    ): ConfirmBlockResponse
}