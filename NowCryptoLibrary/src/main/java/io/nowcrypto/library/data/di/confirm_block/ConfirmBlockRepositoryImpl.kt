package io.nowcrypto.library.data.di.confirm_block

import io.nowcrypto.library.remote.confirm_block.ConfirmBlockApi
import io.nowcrypto.library.remote.confirm_block.ConfirmBlockResponse
import javax.inject.Inject

class ConfirmBlockRepositoryImpl @Inject constructor(
    private val api: ConfirmBlockApi
) : ConfirmBlockRepository {

    override suspend fun getConfirmedBlocksCount(
        transactionId: String,
    ): ConfirmBlockResponse {
        return api.getConfirmedBlocksCount(transactionId)
    }
}