package io.nowcrypto.library.data.di.balance

import io.nowcrypto.library.remote.balance.BalanceResponse

interface BalanceRepository {
    suspend fun fetchUserBalance(apiKey: String, currencyCode: String): BalanceResponse
}