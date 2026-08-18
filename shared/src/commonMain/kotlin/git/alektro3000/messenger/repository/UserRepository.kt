package git.alektro3000.messenger.repository

import git.alektro3000.messenger.actions.PickedAvatar
import git.alektro3000.messenger.exception.ServerException
import git.alektro3000.messenger.local.AppDatabase
import git.alektro3000.messenger.local.SessionStorage
import git.alektro3000.messenger.local.dao.UserEntity
import git.alektro3000.messenger.local.dao.toEntity
import git.alektro3000.messenger.model.user.PickedProfile
import git.alektro3000.messenger.model.user.UserFull
import git.alektro3000.messenger.network.UserApi
import git.alektro3000.messenger.network.dto.ApiResult
import git.alektro3000.messenger.network.dto.UserPreviewResponse
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map

class UserRepository (
    private val api: UserApi,
    private val sessionStorage: SessionStorage,
    private val db: AppDatabase
) {

    suspend fun uploadAvatar(avatar: PickedAvatar)
    {

        when(val response = api.uploadAvatar(avatar))
        {
            is ApiResult.Success -> {
                val url = response.data.url
                db.userDao().updateAvatar(sessionStorage.getUserId(),url)
            }
            is ApiResult.Error -> {
                throw ServerException(response.message)
            }
        }
    }

    suspend fun updateProfile(profile: PickedProfile)
    {
        when(val response = api.updateProfile(profile))
        {
            is ApiResult.Success -> {
                val url = response.data
                return url
            }
            is ApiResult.Error -> {
                throw ServerException(response.message)
            }
        }
    }

    suspend fun getUsers(query: String, page: Int, pageSize: Int) =
        api.getUsers(query, page, pageSize)

    suspend fun cacheUserPreviews(resultResponses: List<UserPreviewResponse>)
    {
        resultResponses.forEach {
            db.userDao()
                .upsertPreview(
                    it.id,
                    it.displayName,
                    it.avatarUrl,
                    it.lastSeenAt,
                )
        }
    }


    fun observeUser(userId: Long): Flow<UserEntity?> =
        db.userDao().observeUser(userId)

    @OptIn(ExperimentalCoroutinesApi::class)
    fun observeMe(): Flow<UserFull?> =
        sessionStorage.userId.flatMapLatest { userId ->
            db.userDao()
                .observeUser(userId)
                .map { it?.toUserFull() }
        }

    suspend fun refreshMe() {
        when(val response = api.me())
        {
            is ApiResult.Success -> {
                val remoteUser = response.data
                val currentUser = db.userDao().getUser(remoteUser.id)
                db.userDao().upsert(
                    remoteUser.toEntity().copy(
                        lastSeenAt = currentUser?.lastSeenAt
                    )
                )
            }
            is ApiResult.Error -> {
                throw ServerException(response.message)
            }
        }
    }
}
