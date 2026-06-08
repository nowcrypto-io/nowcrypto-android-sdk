package io.nowcrypto.library.remote.confirm_block

import retrofit2.http.GET
import retrofit2.http.Query

interface ConfirmBlockApi {
    @GET("transaction/confirmed-blocks")
    suspend fun getConfirmedBlocksCount(
        @Query("txID") transactionId: String
    ): ConfirmBlockResponse
}