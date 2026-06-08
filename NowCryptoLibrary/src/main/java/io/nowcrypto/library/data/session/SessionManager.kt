package io.nowcrypto.library.data.session

import androidx.annotation.Keep
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import javax.inject.Inject

@Keep
data class UserSession(
    val token: String?,
    val isGuest: Boolean,
    val username: String?,
    val profilePictureUrl: String?
)

class SessionManager @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : SessionRepository {

    companion object {
        private val TOKEN_KEY = stringPreferencesKey("auth_token")
        private val IS_GUEST_KEY = booleanPreferencesKey("is_guest")
        // Fixed: These must be stringPreferencesKey
        private val USERNAME_KEY = stringPreferencesKey("username")
        private val PROFILE_PICTURE_URL_KEY = stringPreferencesKey("profile_picture_url")
    }

    override suspend fun saveSession(
        token: String,
        isGuest: Boolean,
        userName: String?,
        profilePictureUrl: String?
    ) {
        dataStore.edit { prefs ->
            prefs[TOKEN_KEY] = token
            prefs[IS_GUEST_KEY] = isGuest

            // "Saving null" = Removing the key
            if (userName != null) {
                prefs[USERNAME_KEY] = userName
            } else {
                prefs.remove(USERNAME_KEY)
            }

            if (profilePictureUrl != null) {
                prefs[PROFILE_PICTURE_URL_KEY] = profilePictureUrl
            } else {
                prefs.remove(PROFILE_PICTURE_URL_KEY)
            }
        }
    }

    override suspend fun getSession(): UserSession {
        val prefs = dataStore.data.first()
        return UserSession(
            token = prefs[TOKEN_KEY],
            isGuest = prefs[IS_GUEST_KEY] ?: true,
            username = prefs[USERNAME_KEY],
            profilePictureUrl = prefs[PROFILE_PICTURE_URL_KEY]
        )
    }

    override suspend fun getToken(): String? = getSession().token
    override suspend fun isGuest(): Boolean = getSession().isGuest
    suspend fun getUsername(): String? = getSession().username
    suspend fun getProfilePictureUrl(): String? = getSession().profilePictureUrl

    override suspend fun clearSession() {
        dataStore.edit { prefs ->
            prefs.remove(TOKEN_KEY)
            prefs.remove(IS_GUEST_KEY)
            prefs.remove(USERNAME_KEY)
            prefs.remove(PROFILE_PICTURE_URL_KEY)
        }
    }
}