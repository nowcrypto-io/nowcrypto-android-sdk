package io.nowcrypto.library.data.di.interceptor

import io.nowcrypto.library.data.session.SessionManager
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

class TokenAuthenticator @Inject constructor(
    private val sessionManager: SessionManager
) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        // Token is invalid/expired > clear it
        runBlocking {
            sessionManager.clearSession()
        }
        // Returning null means: do not retry automatically
        // The failed response is passed back
        return null
    }
}

