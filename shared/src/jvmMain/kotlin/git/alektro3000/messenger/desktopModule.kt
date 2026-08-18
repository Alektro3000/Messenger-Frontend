package git.alektro3000.messenger

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.room.Room
import androidx.room.RoomDatabaseConstructor
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import coil3.ImageLoader
import coil3.PlatformContext
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
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.http.encodedPath
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import okio.Path.Companion.toPath
import org.koin.core.qualifier.named
import org.koin.dsl.module
import java.io.File


val desktopModule = module {


    scope(named("Session")) {

        scoped<HttpClient>(AuthClient) {
            HttpClient(CIO) {

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
            }
        }

        scoped<HttpClient>(MainClient) {
            val tokenStorage: SessionStorage = get()
            val authApi: AuthApi = get()

            HttpClient(CIO) {

                install(WebSockets) {
                    pingIntervalMillis = 15_000
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

    single<DataStore<Preferences>> {
        PreferenceDataStoreFactory.createWithPath {
            "tokens.preferences_pb".toPath()
        }
    }

    single { SessionStorage(get()) }

    single {
        val dbFile = File("messenger.db")
        Room.databaseBuilder<AppDatabase>(
            name = dbFile.absolutePath,
        )
            .setDriver(BundledSQLiteDriver())
            .fallbackToDestructiveMigration(true)
            .build()
    }


    single<ImageLoader> {
        ImageLoader.Builder(PlatformContext.INSTANCE)
            .crossfade(true)
            .build()
    }
}
