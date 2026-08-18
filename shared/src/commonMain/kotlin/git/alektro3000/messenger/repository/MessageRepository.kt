package git.alektro3000.messenger.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import git.alektro3000.messenger.local.AppDatabase
import git.alektro3000.messenger.local.SessionStorage
import git.alektro3000.messenger.local.dao.ChatEntity
import git.alektro3000.messenger.local.dao.ChatType
import git.alektro3000.messenger.local.dao.MessageEntity
import git.alektro3000.messenger.local.dao.MessageStatus
import git.alektro3000.messenger.local.dao.MessageWithUser
import git.alektro3000.messenger.model.chats.MessageDraft
import git.alektro3000.messenger.model.message.MessageAction
import git.alektro3000.messenger.network.IncomingSocketEvent
import git.alektro3000.messenger.network.MessageApi
import git.alektro3000.messenger.network.MessageSocketClient
import git.alektro3000.messenger.network.dto.ApiResult
import git.alektro3000.messenger.network.dto.MessageResponseWithId
import git.alektro3000.messenger.repository.mediator.MessageRemoteMediator
import git.alektro3000.messenger.viewModel.Notifier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.time.Clock
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Instant

class MessageRepository(
    private val api: MessageApi,
    private val sessionStorage: SessionStorage,
    private val notifier: Notifier,
    private val db: AppDatabase,
) {

    @OptIn(ExperimentalPagingApi::class)
    fun recentMessages(chatId: Long?, receiverId: Long?): Flow<PagingData<MessageWithUser>> {
        return Pager(
            config = PagingConfig(pageSize = 30),
            remoteMediator = MessageRemoteMediator(
                chatId = chatId,
                api = api,
                db = db
            ),
            pagingSourceFactory = {
                db.messageDao().pagingLocalMessagesWithUsers(chatId, receiverId)
            }
        ).flow
    }

    //Returns ErrorMessage
    suspend fun sendMessage(chatId: Long?, receiverId: Long?, request: MessageDraft): String? {

        val realChatId = chatId ?: receiverId?.let {
            db.chatDao().findDirectChatIdByReceiver(it)
        }

        val clientId = UUID.randomUUID().toString()
        val messageEntity =
            MessageEntity(
                clientId = clientId,
                status = MessageStatus.Pending,
                text = request.text,
                type = request.type,
                sendAt = Clock.System.now(),
                senderId = sessionStorage.getUserId(),
                chatId = realChatId,
                receiverId = receiverId
            )
        db.messageDao().insertMessage(
            messageEntity
        )

        return sendMessage(messageEntity)
    }

    suspend fun sendMessage(messageEntity: MessageEntity): String?
    {
        val realChatId = messageEntity.chatId
        val receiverId = messageEntity.receiverId
        val clientId = messageEntity.clientId
        val request = MessageDraft(messageEntity.text!!, messageEntity.type)
        val result =
            if (realChatId != null) {
                api.sendMessageChat(realChatId, clientId, request)
            } else if (receiverId != null) {
                api.sendMessageDirect(receiverId, clientId, request)
            } else {
                db.messageDao().updateMessageFromErrorServerResponse(clientId)
                return "clientId has invalid destination"
            }

        when (result) {
            is ApiResult.Success<MessageResponseWithId> -> {
                val mes = result.data.mes
                db.messageDao().updateMessageFromServerResponse(
                    result.data.id,
                    serverId = mes.messageId,
                    chatId = mes.chatId,
                    sendAt = mes.sendAt,
                )
                receiverId?.let {
                    val chat = ChatEntity(
                        id = mes.chatId,
                        receiverId = receiverId,
                        type = ChatType.Direct,
                        displayName = null,
                        avatarUrl = null,
                        createdAt = mes.sendAt,
                        lastMessageId = mes.messageId,
                        unreadMessageCount = 0,
                    )
                    db.chatDao().upsert(chat)
                }
            }

            is ApiResult.Error -> {
                db.messageDao().updateMessageFromErrorServerResponse(clientId)
                return result.message
            }
        }
        return null
    }


    suspend fun deleteMessage(messageId: Long, chatId: Long, newLastMessageId: Long?, deleteAt: Instant)
    {
        db.chatDao().updateLastMessage(chatId, newLastMessageId);
        db.messageDao().deleteId(messageId, deleteAt)
    }
    suspend fun messageAction(clientId: String?,  messageAction: MessageAction)
    {
        when(messageAction)
        {
            is MessageAction.Cancel -> {
                db.messageDao().removeId(clientId)
            }
            is MessageAction.Resend -> {
                val message = db.messageDao().findId(clientId) ?: return
                sendMessage(message)
            }
            is MessageAction.Delete -> {
                val message = db.messageDao().findId(clientId) ?: return
                when(val response = api.deleteMessage(message.serverId!!, message.chatId!!))
                {
                    is ApiResult.Error ->
                        notifier.show(response.message)
                    is ApiResult.Success -> {
                        val messageResponse = response.data;
                        deleteMessage(
                            messageResponse.messageId,
                            message.chatId,
                            messageResponse.newLastMessageId,
                            messageResponse.deleteAt)
                    }
                }
            }
            is MessageAction.EditText -> {
                val message = db.messageDao().findId(clientId) ?: return
                when(val response = api.editTextMessage(
                    message.serverId!!,
                    message.chatId!!,
                    messageAction.newTextMessage))
                {
                    is ApiResult.Error ->
                        notifier.show(response.message)
                    is ApiResult.Success -> {
                        val message = response.data;
                        db.messageDao().editId(message.messageId, message.newText, message.editAt)
                    }
                }
            }
            else -> {

            }
        }
    }
}
