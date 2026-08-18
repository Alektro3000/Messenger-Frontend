package git.alektro3000.messenger.repository.mediator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import git.alektro3000.messenger.exception.ServerException
import git.alektro3000.messenger.local.AppDatabase
import git.alektro3000.messenger.local.dao.ChatMemberWithUser
import git.alektro3000.messenger.network.ChatApi
import git.alektro3000.messenger.network.dto.ApiResult
import git.alektro3000.messenger.repository.toChatEntity
import git.alektro3000.messenger.repository.toChatMemberEntity

@OptIn(ExperimentalPagingApi::class)
class ChatMembersRemoteMediator(
    private val chatId: Long,
    private val api: ChatApi,
    private val db: AppDatabase
) : RemoteMediator<Int, ChatMemberWithUser>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ChatMemberWithUser>
    ): MediatorResult {
        if (loadType != LoadType.REFRESH) {
            return MediatorResult.Success(endOfPaginationReached = true)
        }

        return try {
            when (val response = api.getChatInfo(chatId)) {
                is ApiResult.Success -> {
                    val chat = response.data

                    chat.chatMembers.forEach { member ->
                        db.userDao().upsertPreview(
                            member.user.id,
                            member.user.displayName,
                            member.user.avatarUrl,
                            member.user.lastSeenAt,
                        )
                    }

                    db.chatDao().upsert(chat.toChatEntity())
                    db.chatMemberDao().upsertAll(
                        chat.chatMembers.map { it.toChatMemberEntity(chat.chatId) }
                    )

                    MediatorResult.Success(endOfPaginationReached = true)
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
