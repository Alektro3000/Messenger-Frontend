package git.alektro3000.messenger.repository.mediator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import git.alektro3000.messenger.exception.ServerException
import git.alektro3000.messenger.local.AppDatabase
import git.alektro3000.messenger.local.dao.MessageWithUser
import git.alektro3000.messenger.network.MessageApi
import git.alektro3000.messenger.network.dto.ApiResult
import git.alektro3000.messenger.repository.toMessageEntity

@OptIn(ExperimentalPagingApi::class)
class MessageRemoteMediator(
    private val chatId: Long?,
    private val api: MessageApi,
    private val db: AppDatabase
) : RemoteMediator<Int, MessageWithUser>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, MessageWithUser>
    ): MediatorResult {
        if (chatId == null) return MediatorResult.Success(true)
        return try {

            val pageSize = state.config.pageSize

            val loadKey = when (loadType) {

                LoadType.REFRESH -> null

                LoadType.PREPEND -> return MediatorResult.Success(
                    endOfPaginationReached = true
                )

                LoadType.APPEND -> {
                    val lastItem = state.lastItemOrNull()
                    lastItem?.message?.serverId
                        ?: return MediatorResult.Success(endOfPaginationReached = true)
                }
            }

            val response = api.getMessages(
                chatId = chatId,
                beforeMessageId = loadKey,
                pageSize = pageSize
            )
            when (response) {
                is ApiResult.Success -> {
                    val messages = response.data

                    messages.forEach {
                        db.userDao()
                            .upsertBasic(
                                it.sender.id,
                                it.sender.displayName,
                                it.sender.avatarUrl
                            )
                    }
                    db.messageDao().insertAll(messages.map { it.toMessageEntity(chatId) })

                    return MediatorResult.Success(
                        endOfPaginationReached = messages.size < pageSize
                    )
                }

                is ApiResult.Error -> {
                    MediatorResult.Error(ServerException(response.message))
                }
            }


        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }
}
