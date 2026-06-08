package io.nowcrypto.library.data.di.currency

import io.nowcrypto.library.remote.currency.CurrencyResponse

interface CurrencyRepository {
    suspend fun getSupportedCurrencies(
        publicKey: String
    ): CurrencyResponse
}