package git.alektro3000.messenger.repository

import git.alektro3000.messenger.exception.ServerException
import git.alektro3000.messenger.local.AccessToken
import git.alektro3000.messenger.local.AppDatabase
import git.alektro3000.messenger.local.SessionStorage
import git.alektro3000.messenger.local.SessionStorage.Companion.USER_ID
import git.alektro3000.messenger.network.AuthApi
import git.alektro3000.messenger.network.dto.ApiResult
import git.alektro3000.messenger.viewModel.Notifier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map


class AuthRepository (
    private val api: AuthApi,
    private val db: AppDatabase,
    private val notifier: Notifier,
    private val realtimeHandler: RealtimeHandler,
    private val tokenStorage: SessionStorage
) {
    suspend fun logout()
    {
        val token = tokenStorage.getRefreshToken()
        token?.let {
            when (val response = api.logout(token))
            {
                is ApiResult.Error -> {
                    notifier.show(response.message)
                }
                else -> {

                }
            }

        }
        realtimeHandler.disconnectSocket()
        db.clearData()
        tokenStorage.logout()

    }

    val userId: Flow<Long?> = tokenStorage.userId
    suspend fun getUserIdNullable() = tokenStorage.getUserIdNullable()
    suspend fun login(name: String, password: String)
    {
        when (val response = api.login(name, password)) {
            is ApiResult.Error -> {
                notifier.show(response.message)
            }

            is ApiResult.Success -> {
                val data = response.data

                tokenStorage.setAccessToken(
                    AccessToken(
                        token = data.accessToken,
                        expirationMinutes = data.expiresInMinutes
                    )
                )

                tokenStorage.setRefreshToken(data.refreshToken)

                realtimeHandler.connectSocket()
            }
        }
    }
    suspend fun register(name: String, password: String)
    {
        when (val response = api.register(name, password)) {
            is ApiResult.Error -> {
                notifier.show(response.message)
            }

            is ApiResult.Success -> {
            }
        }
    }
}