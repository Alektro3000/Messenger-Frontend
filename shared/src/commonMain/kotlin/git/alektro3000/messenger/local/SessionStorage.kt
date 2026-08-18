package git.alektro3000.messenger.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.longPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.io.encoding.Base64
import kotlin.time.Clock
import kotlin.time.Duration.Companion.minutes

data class AccessToken (
    val token: String,
    val expirationMinutes: Int,
)

class SessionStorage (
    private val dataStore: DataStore<Preferences>
) {
    fun extractUid(accessToken: String): Long? {
        return try {
            val payload = accessToken.split(".")[1]

            val normalizedPayload = when (payload.length % 4) {
                2 -> "$payload=="
                3 -> "$payload="
                else -> payload
            }

            val json =
                Base64.UrlSafe
                .decode(normalizedPayload)
                .decodeToString()


            Json.parseToJsonElement(json)
                .jsonObject["uid"]
                ?.jsonPrimitive
                ?.content
                ?.toLong()
        } catch (ex: Exception) {
            null
        }
    }
    suspend fun getAccessToken() : String? {
        return dataStore.data.first()[ACCESS_TOKEN]
    }

    suspend fun setAccessToken(token: AccessToken){
        dataStore.edit { prefs ->
            extractUid(token.token)?.let {
                prefs[USER_ID] = it
            }
            prefs[ACCESS_TOKEN] = token.token
            prefs[ACCESS_TOKEN_TIME] = Clock.System.now()
                .plus(token.expirationMinutes.minutes)
                .toEpochMilliseconds()

        }
    }
    val userId: Flow<Long?> =
        dataStore.data.map { prefs ->
            prefs[USER_ID]
        }

    suspend fun getUserId() : Long {
        return dataStore.data.first()[USER_ID] ?: throw Exception("User Is Not Logged In")
    }
    suspend fun getUserIdNullable() : Long? {
        return dataStore.data.first()[USER_ID]
    }

    suspend fun isAccessTokenExpired(): Boolean? {
        val current = dataStore.data.first()[ACCESS_TOKEN_TIME] ?: return null
        return current > Clock.System.now().toEpochMilliseconds()
    }

    suspend fun getRefreshToken() : String? {
        return dataStore.data.first()[REFRESH_TOKEN]
    }

    suspend fun setRefreshToken(token: String)  {
        dataStore.edit { prefs ->
            prefs[REFRESH_TOKEN] = token
        }
    }
    suspend fun logout()
    {
        dataStore.edit { prefs ->
            prefs.clear()
        }
    }

    companion object {
        val ACCESS_TOKEN =
            stringPreferencesKey("access_token")

        val USER_ID =
            longPreferencesKey("user_id")
        val ACCESS_TOKEN_TIME =
            longPreferencesKey("access_token_time")
        val REFRESH_TOKEN =
            stringPreferencesKey("refresh_token")
    }
}
