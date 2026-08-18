package git.alektro3000.messenger.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import git.alektro3000.messenger.actions.PickedAvatar
import git.alektro3000.messenger.model.chats.ChatBaseInfo
import git.alektro3000.messenger.model.chats.ChatMemberInfo
import git.alektro3000.messenger.model.chats.MessageDraft
import git.alektro3000.messenger.model.chats.toChatFull
import git.alektro3000.messenger.model.message.MessageAction
import git.alektro3000.messenger.repository.AuthRepository
import git.alektro3000.messenger.repository.ChatsRepository
import git.alektro3000.messenger.repository.MessageRepository
import git.alektro3000.messenger.repository.UserRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


@OptIn(ExperimentalCoroutinesApi::class)
class ChatViewModel(
    private val authRepository: AuthRepository,
    private val messageRepository: MessageRepository,
    private val chatRepository: ChatsRepository,
    private val userRepository: UserRepository,
    private val notifier: Notifier
) : ViewModel() {
    sealed interface ChatEditUiState {
        data object Idle : ChatEditUiState
        data object Loading : ChatEditUiState
        data object Success : ChatEditUiState
        data class Error(val message: String) : ChatEditUiState
    }

    private val args = MutableStateFlow<ChatBaseInfo?>(null)
    private val currentChat = args
        .flatMapLatest { chat ->
            if (chat == null) {
                flowOf(null)
            } else {
                chatRepository.observeChatId(chat.receiverId)
                    .map { observedChatId ->
                        chat.copy(
                            chatId = observedChatId ?: chat.chatId
                        )
                    }
            }
        }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            null
        )

    fun setArgs(chat: ChatBaseInfo?) {
        args.value = chat
    }

    val userId = authRepository.userId


    val messages = currentChat
        .flatMapLatest { args ->
            if (args == null) {
                flowOf(PagingData.empty())
            } else {
                messageRepository.recentMessages(
                    args.chatId,
                    args.receiverId,
                )
            }
        }

    val chat = currentChat
        .flatMapLatest { args ->
            when {
                args?.chatId != null -> {
                    chatRepository.observeChat(args.chatId)
                }

                args?.receiverId != null -> {
                    userRepository.observeUser(args.receiverId)
                        .map {
                            it?.toChatFull()
                        }
                }

                else -> flowOf(null)
            }
        }

    val chatMembers = currentChat
        .flatMapLatest { args ->
            if (args?.chatId != null) {
                chatRepository.observeChatMembers(args.chatId)
            } else {
                flowOf(PagingData.empty<ChatMemberInfo>())
            }
        }
        .cachedIn(viewModelScope)

    private val _chatEditState = MutableStateFlow<ChatEditUiState>(ChatEditUiState.Idle)
    val chatEditState: StateFlow<ChatEditUiState> = _chatEditState.asStateFlow()

    fun updateChatDisplayName(displayName: String) {
        val chatId = currentChat.value?.chatId ?: return
        viewModelScope.launch {
            _chatEditState.value = ChatEditUiState.Loading
            try {
                chatRepository.updateChatDisplayName(chatId, displayName)
                _chatEditState.value = ChatEditUiState.Success
            } catch (ex: Exception) {
                notifier.show(ex)
                _chatEditState.value = ChatEditUiState.Error(ex.message ?: "Unknown error")
            }
        }
    }

    fun uploadChatAvatar(avatar: PickedAvatar) {
        val chatId = currentChat.value?.chatId ?: return
        viewModelScope.launch {
            _chatEditState.value = ChatEditUiState.Loading
            try {
                chatRepository.uploadChatAvatar(chatId, avatar)
                _chatEditState.value = ChatEditUiState.Success
            } catch (ex: Exception) {
                notifier.show(ex)
                _chatEditState.value = ChatEditUiState.Error(ex.message ?: "Unknown error")
            }
        }
    }


    fun sendMessage(message: String) {
        val currentChat = currentChat.value ?: return
        viewModelScope.launch {
            val result = messageRepository.sendMessage(
                currentChat.chatId, currentChat.receiverId,
                MessageDraft(message, "Text")
            ) ?: return@launch
            notifier.show(result)
        }
    }

    fun messageAction(clientId: String?, messageAction: MessageAction) {
        viewModelScope.launch {
            messageRepository.messageAction(clientId, messageAction)
        }
    }
}
