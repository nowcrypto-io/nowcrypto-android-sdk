package io.nowcrypto.library.domain.add_fund

import io.nowcrypto.library.data.di.add_fund.AddFundRepository
import io.nowcrypto.library.remote.add_fund.AddFundResponse

class AddFundUseCase(
    private val repository: AddFundRepository
) {
    suspend fun execute(
        publicKey: String,
        transactionId: String,
        paymentRequestToken: String,
        walletAddress: String
    ): AddFundResponse {
        return repository.addFund(
            publicKey,
            transactionId,
            paymentRequestToken,
            walletAddress
        )
    }
}