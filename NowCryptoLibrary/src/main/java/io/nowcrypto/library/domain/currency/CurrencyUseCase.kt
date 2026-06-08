package io.nowcrypto.library.domain.currency

import io.nowcrypto.library.data.di.currency.CurrencyRepository
import io.nowcrypto.library.remote.currency.CurrencyResponse

class CurrencyUseCase(
    private val repository: CurrencyRepository
) {
    suspend fun execute(
        publicKey: String
    ): CurrencyResponse {
        return repository.getSupportedCurrencies(
            publicKey
        )
    }
}