package io.nowcrypto.library.data.session

interface SessionRepository {
    suspend fun saveSession(token: String, isGuest: Boolean, userName: String?, profilePictureUrl: String?)
    suspend fun getToken(): String?
    suspend fun isGuest(): Boolean
    suspend fun getSession(): UserSession
    suspend fun clearSession()
}