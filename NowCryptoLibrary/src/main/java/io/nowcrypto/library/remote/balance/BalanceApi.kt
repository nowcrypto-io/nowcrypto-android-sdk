package io.nowcrypto.library.remote.balance

import retrofit2.http.Body
import retrofit2.http.POST

interface BalanceApi {
    @POST("payment/user/balance")
    suspend fun fetchUserBalance(@Body request: BalanceRequest): BalanceResponse
}
