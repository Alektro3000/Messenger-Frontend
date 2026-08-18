package git.alektro3000.messenger

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import coil3.ImageLoader
import coil3.request.crossfade
import git.alektro3000.messenger.local.AccessToken
import git.alektro3000.messenger.local.AppDatabase
import git.alektro3000.messenger.local.SessionStorage
import git.alektro3000.messenger.network.AuthApi
import git.alektro3000.messenger.network.AuthClient
import git.alektro3000.messenger.network.MainClient
import git.alektro3000.messenger.network.dto.ApiResult
import git.alektro3000.messenger.network.dto.JwtResponse
import git.alektro3000.messenger.network.dto.RefreshRequest
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.http.encodedPath
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

val androidModule = module {

    single { androidContext().sessionDataStore }
    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java, "database-name"
        )
            .fallbackToDestructiveMigration(true)
            .build()
    }


    scope(named("Session")) {
        scoped<HttpClient>(AuthClient) {
            HttpClient(OkHttp) {

                install(WebSockets) {
                    pingIntervalMillis = 15_000
                }
                install(ContentNegotiation) {
                    json(
                        Json {
                            ignoreUnknownKeys = false
                            classDiscriminator = "type"
                        })
                }

                install(HttpTimeout) {
                    connectTimeoutMillis = 5000
                    socketTimeoutMillis = 10000
                    requestTimeoutMillis = 15000
                }

                install(Logging) {
                    logger = object : Logger {
                        override fun log(message: String) {
                            Log.d("KtorAuth", message)
                        }
                    }
                    level = LogLevel.HEADERS
                }
            }
        }

        scoped<HttpClient>(MainClient) {
            val tokenStorage: SessionStorage = get()
            val authApi: AuthApi = get()

            HttpClient(OkHttp) {

                install(WebSockets) {
                    pingIntervalMillis = 15_000
                }
                install(Logging) {
                    logger = object : Logger {
                        override fun log(message: String) {
                            Log.d("KtorMain", message)
                        }
                    }
                    level = LogLevel.HEADERS
                }
                install(HttpTimeout) {
                    connectTimeoutMillis = 5000
                    socketTimeoutMillis = 10000
                    requestTimeoutMillis = 15000
                }
                install(Auth) {
                    bearer {
                        loadTokens {
                            val accessToken = tokenStorage.getAccessToken()
                                ?: return@loadTokens null

                            val refreshToken = tokenStorage.getRefreshToken()
                                ?: return@loadTokens null

                            BearerTokens(
                                accessToken = accessToken,
                                refreshToken = refreshToken
                            )
                        }

                        refreshTokens {
                            val refreshToken = tokenStorage.getRefreshToken()
                                ?: return@refreshTokens null

                            val response = authApi.refresh(
                                refreshToken
                            ) as? ApiResult.Success<JwtResponse>
                                ?: return@refreshTokens null

                            val token = response.data

                            val access = AccessToken(
                                token.accessToken,
                                token.expiresInMinutes
                            )

                            tokenStorage.setAccessToken(access)
                            tokenStorage.setRefreshToken(token.refreshToken)

                            BearerTokens(
                                accessToken = token.accessToken,
                                refreshToken = token.refreshToken
                            )
                        }

                        sendWithoutRequest { request ->
                            !request.url.encodedPath.startsWith("/api/v1/auth")
                        }
                    }
                }

                install(ContentNegotiation) {
                    json(
                        Json {
                            ignoreUnknownKeys = false
                            classDiscriminator = "type"
                        })
                }
            }
        }
    }
    single<ImageLoader> {
        ImageLoader.Builder(androidContext())
            .crossfade(true)
            .build()
    }
}

val Context.sessionDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "tokens"
)