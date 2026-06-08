package io.nowcrypto.library.remote.add_fund

import retrofit2.http.Body
import retrofit2.http.POST

interface AddFundApi {
    @POST("payment/add-fund")
    suspend fun addFund(@Body request: AddFundRequest): AddFundResponse
}