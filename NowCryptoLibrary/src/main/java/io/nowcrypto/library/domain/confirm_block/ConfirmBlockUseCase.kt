package io.nowcrypto.library.domain.confirm_block

import io.nowcrypto.library.data.di.confirm_block.ConfirmBlockRepository
import io.nowcrypto.library.remote.confirm_block.ConfirmBlockResponse

class ConfirmBlockUseCase(
    private val repository: ConfirmBlockRepository
) {
    suspend fun execute(transactionId: String): ConfirmBlockResponse {
        return repository.getConfirmedBlocksCount(transactionId)
    }
}