package io.nowcrypto.library.data.di.balance

import io.nowcrypto.library.remote.balance.BalanceApi
import io.nowcrypto.library.remote.balance.BalanceRequest
import io.nowcrypto.library.remote.balance.BalanceResponse

class BalanceRepositoryImpl(
    private val api: BalanceApi
) : BalanceRepository {
    override suspend fun fetchUserBalance(apiKey: String, currencyCode: String): BalanceResponse {
        return api.fetchUserBalance(BalanceRequest(apiKey, currencyCode))
    }
}
