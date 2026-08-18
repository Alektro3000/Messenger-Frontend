package git.alektro3000.messenger.repository

import git.alektro3000.messenger.local.AppDatabase
import git.alektro3000.messenger.network.IncomingSocketEvent
import git.alektro3000.messenger.network.MessageSocketClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class RealtimeHandler(
    private val messageRepository: MessageRepository,
    private val db: AppDatabase,
    private val socketClient: MessageSocketClient,
    private val scope: CoroutineScope) {


    private var socketJob: Job? = null
    init {
        connectSocket()
    }
    fun connectSocket() {
        if (socketJob?.isActive == true) return

        socketJob = scope.launch {
            socketClient.observeMessages()
                .retryWhen { _, attempt ->
                    delay(((attempt + 1).coerceAtMost(5) * 1000).milliseconds)
                    true
                }
                .collect { event ->
                    println("Incoming WebSocket event $event")
                    when (event) {
                        is IncomingSocketEvent.NewMessage -> {
                            db.userDao().upsertBasic(
                                event.message.sender.id,
                                event.message.sender.displayName,
                                event.message.sender.avatarUrl,
                            )
                            db.messageDao()
                                .insertAll(listOf(event.message.toMessageEntity(event.chatId)))
                        }
                        is IncomingSocketEvent.NewChat -> {
                            db.userDao().upsertBasic(
                                event.chat.lastMessage.sender.id,
                                event.chat.lastMessage.sender.displayName,
                                event.chat.lastMessage.sender.avatarUrl,
                            )
                            db.messageDao().insertAll(listOf(event.chat.lastMessage.toMessageEntity(event.chat.chatId)))
                            db.chatDao().upsert(event.chat.toChatEntity())
                        }
                        is IncomingSocketEvent.DeleteMessage -> {
                            messageRepository.deleteMessage(
                                event.messageId,
                                event.chatId,
                                event.newLastMessageId,
                                event.deleteAt)
                        }
                        is IncomingSocketEvent.EditMessage -> {
                            db.messageDao().editId(event.messageId, event.newText, event.editAt)
                        }
                    }
                }
        }
    }

    fun disconnectSocket() {
        socketJob?.cancel()
        socketJob = null
    }

}