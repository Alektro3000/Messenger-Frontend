package git.alektro3000.messenger.repository.mediator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import git.alektro3000.messenger.exception.ServerException
import git.alektro3000.messenger.local.AppDatabase
import git.alektro3000.messenger.local.dao.ChatWithLastMessage
import git.alektro3000.messenger.network.ChatApi
import git.alektro3000.messenger.network.dto.ApiResult
import git.alektro3000.messenger.repository.toChatEntity
import git.alektro3000.messenger.repository.toMessageEntity


@OptIn(ExperimentalPagingApi::class)
class ChatsRemoteMediator(
    private val api: ChatApi,
    private val db: AppDatabase,
    private val query: String,
) : RemoteMediator<Int, ChatWithLastMessage>() {

    override suspend fun load(
        loadType: LoadType, state: PagingState<Int, ChatWithLastMessage>
    ): MediatorResult {
        return try {
            val pageSize = state.config.pageSize

            val page = when (loadType) {
                LoadType.REFRESH -> 0

                LoadType.PREPEND -> {
                    return MediatorResult.Success(endOfPaginationReached = true)
                }

                LoadType.APPEND -> {
                    val loadedCount = state.pages.sumOf { it.data.size }
                    loadedCount / pageSize
                }
            }

            when (val response = api.getChats(page, pageSize, query)) {
                is ApiResult.Success -> {
                    val chats = response.data

                    db.chatDao().insertAll(
                        chats.map { it.toChatEntity() })
                    db.messageDao().insertAll(
                        chats.map { it.toMessageEntity() })
                    chats.forEach {
                        db.userDao().upsertBasic(
                            it.lastMessage.sender.id,
                            it.lastMessage.sender.displayName,
                            it.lastMessage.sender.avatarUrl
                        )
                    }


                    MediatorResult.Success(
                        endOfPaginationReached = chats.size < pageSize
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