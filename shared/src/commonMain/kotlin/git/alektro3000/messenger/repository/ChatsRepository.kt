package git.alektro3000.messenger.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import git.alektro3000.messenger.actions.PickedAvatar
import git.alektro3000.messenger.exception.ServerException
import git.alektro3000.messenger.local.AppDatabase
import git.alektro3000.messenger.model.chats.ChatFull
import git.alektro3000.messenger.model.chats.ChatMemberInfo
import git.alektro3000.messenger.model.chats.toChatFull
import git.alektro3000.messenger.network.ChatApi
import git.alektro3000.messenger.network.dto.ApiResult
import git.alektro3000.messenger.network.dto.ChatFullResponse
import git.alektro3000.messenger.repository.toChatEntity
import git.alektro3000.messenger.repository.toChatMemberEntity
import git.alektro3000.messenger.repository.mediator.ChatMembersRemoteMediator
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

@OptIn(ExperimentalCoroutinesApi::class)
class ChatsRepository constructor(
    private val api: ChatApi,
    private val db: AppDatabase
) {

    fun observeChat(chatId: Long): Flow<ChatFull?> =
        db.chatDao().observeChat(chatId)
            .map { chat -> chat?.toChatFull() }

    @OptIn(ExperimentalPagingApi::class)
    fun observeChatMembers(chatId: Long): Flow<PagingData<ChatMemberInfo>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                prefetchDistance = 5
            ),
            remoteMediator = ChatMembersRemoteMediator(chatId, api, db),
            pagingSourceFactory = {
                db.chatMemberDao().pagingChatMembers(chatId)
            }
        ).flow.map { pagingData ->
            pagingData.map { member ->
                member.toChatMemberInfo()
            }
        }
    }

    suspend fun refreshChatInfo(chatId: Long) {
        when (val response = api.getChatInfo(chatId)) {
            is ApiResult.Success -> saveChatInfo(response.data)
            is ApiResult.Error -> throw ServerException(response.message)
        }
    }

    suspend fun updateChatDisplayName(chatId: Long, displayName: String) {
        when (val response = api.updateChatInfo(chatId, displayName)) {
            is ApiResult.Success -> saveChatInfo(response.data)
            is ApiResult.Error -> throw ServerException(response.message)
        }
    }

    suspend fun uploadChatAvatar(chatId: Long, avatar: PickedAvatar) {
        when (val response = api.uploadChatAvatar(chatId, avatar)) {
            is ApiResult.Success -> saveChatInfo(response.data)
            is ApiResult.Error -> throw ServerException(response.message)
        }
    }

    suspend fun createGroupChat(displayName: String, memberIds: List<Long>): Long {
        when (val response = api.createGroupChat(displayName, memberIds)) {
            is ApiResult.Success -> {
                saveChatInfo(response.data)
                return response.data.chatId
            }
            is ApiResult.Error -> throw ServerException(response.message)
        }
    }

    private suspend fun saveChatInfo(chat: ChatFullResponse) {
        db.chatDao().upsert(chat.toChatEntity())

        chat.chatMembers.forEach { member ->
            db.userDao().upsertPreview(
                member.user.id,
                member.user.displayName,
                member.user.avatarUrl,
                member.user.lastSeenAt,
            )
        }
        db.chatMemberDao().upsertAll(
            chat.chatMembers.map { it.toChatMemberEntity(chat.chatId) }
        )
    }

    suspend fun findByReceiverId(receiverId: Long?): Long?
    {
        return receiverId?.let { db.chatDao().findDirectChatIdByReceiver(receiverId) }
    }
    fun observeChatId(receiverId: Long?): Flow<Long?>
    {
        return if(receiverId == null) flowOf(null) else { db.chatDao().observeChatId(receiverId) }
    }
}
