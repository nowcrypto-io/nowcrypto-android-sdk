package io.nowcrypto.library.domain.balance

import io.nowcrypto.library.data.di.balance.BalanceRepository
import io.nowcrypto.library.remote.balance.BalanceResponse

class FetchUserBalanceUseCase(
    private val repository: BalanceRepository
) {
    suspend fun execute(apiKey: String, currencyCode: String): BalanceResponse {
        return repository.fetchUserBalance(apiKey, currencyCode)
    }
}