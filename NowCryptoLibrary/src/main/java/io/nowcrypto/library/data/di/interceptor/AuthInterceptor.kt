package io.nowcrypto.library.data.di.interceptor

import android.util.Log
import io.nowcrypto.library.data.session.SessionManager
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val sessionManager: SessionManager
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val requestBuilder = originalRequest.newBuilder()
            .addHeader("Accept", "application/json")
            .addHeader("Content-Type", "application/json")

        // 1. Check if we manually passed a Secret Key in the Retrofit Header
        val manualKey = originalRequest.header("X-Manual-Secret-Key")

        if (!manualKey.isNullOrBlank()) {
            Log.d("AuthInterceptor", "Using manual secret key for this request")
            // Swap the manual key into the Bearer header
            requestBuilder.header("Authorization", "Bearer $manualKey")
            // Remove the dummy header so the server doesn't see it
            requestBuilder.removeHeader("X-Manual-Secret-Key")
        } else {
            // 2. Normal Flow: Get the token from SessionManager
            val token = runBlocking { sessionManager.getToken() }
            if (!token.isNullOrBlank()) {
                Log.d("AuthInterceptor", "Token is valid from SessionManager")
                requestBuilder.header("Authorization", "Bearer $token")
            } else {
                Log.d("AuthInterceptor", "Token is null in SessionManager")
            }
        }

        return chain.proceed(requestBuilder.build())
    }
}