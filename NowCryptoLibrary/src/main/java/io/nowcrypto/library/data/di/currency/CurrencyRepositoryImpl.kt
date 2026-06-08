package io.nowcrypto.library.data.di.currency

import io.nowcrypto.library.remote.currency.CurrencyApi
import io.nowcrypto.library.remote.currency.CurrencyRequest
import io.nowcrypto.library.remote.currency.CurrencyResponse

class CurrencyRepositoryImpl(
    private val api: CurrencyApi
) : CurrencyRepository {
    override suspend fun getSupportedCurrencies(publicKey: String): CurrencyResponse {
        return api.getSupportedCurrencies(CurrencyRequest(publicKey))
    }
}
