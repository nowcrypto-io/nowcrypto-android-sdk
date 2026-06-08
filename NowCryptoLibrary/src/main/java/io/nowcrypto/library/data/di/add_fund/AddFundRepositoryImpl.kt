package io.nowcrypto.library.data.di.add_fund

import io.nowcrypto.library.remote.add_fund.AddFundApi
import io.nowcrypto.library.remote.add_fund.AddFundRequest
import io.nowcrypto.library.remote.add_fund.AddFundResponse

class AddFundRepositoryImpl(
    private val api: AddFundApi
) : AddFundRepository {
    override suspend fun addFund(
        publicKey: String,
        transactionId: String,
        paymentRequestToken: String,
        walletAddress: String
    ): AddFundResponse {
        return api.addFund(AddFundRequest(
            publicKey,
            transactionId,
            paymentRequestToken,
            walletAddress
        ))
    }
}
