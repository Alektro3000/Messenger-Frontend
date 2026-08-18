package git.alektro3000.messenger.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import git.alektro3000.messenger.model.user.UserPreview
import git.alektro3000.messenger.repository.ChatsRepository
import git.alektro3000.messenger.repository.UserRepository
import git.alektro3000.messenger.repository.pagingSources.UsersPagingSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class CreateGroupChatViewModel(
    private val userRepository: UserRepository,
    private val chatsRepository: ChatsRepository,
    private val notifier: Notifier
) : ViewModel() {

    sealed interface Event {
        data class Created(val chatId: Long) : Event
        data object Dismiss : Event
    }

    private val _events = MutableSharedFlow<Event>()
    val events: SharedFlow<Event> = _events.asSharedFlow()

    var query by mutableStateOf("")
        private set

    var groupName by mutableStateOf("")
        private set

    var selectedUsers by mutableStateOf<Set<Long>>(emptySet())
        private set

    fun updateQuery(value: String) {
        query = value
    }

    fun updateGroupName(value: String) {
        groupName = value
    }

    fun toggleUser(user: UserPreview) {
        selectedUsers = if (selectedUsers.contains(user.id)) {
            selectedUsers - user.id
        } else {
            selectedUsers + user.id
        }
    }

    fun isSelected(userId: Long): Boolean = selectedUsers.contains(userId)

    fun clearSelection() {
        selectedUsers = emptySet()
    }

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val users = snapshotFlow { query }
        .debounce(300.milliseconds)
        .distinctUntilChanged()
        .flatMapLatest { q ->
            Pager(
                config = PagingConfig(
                    pageSize = 30,
                    prefetchDistance = 10
                ),
                pagingSourceFactory = {
                    UsersPagingSource(
                        repository = userRepository,
                        notifier = notifier,
                        query = q
                    )
                }
            ).flow
        }
        .cachedIn(viewModelScope)

    fun createGroupChat() {
        val name = groupName.trim()
        if (name.isBlank() || selectedUsers.isEmpty()) {
            viewModelScope.launch {
                notifier.show("Choose at least one user and a group name")
            }
            return
        }

        viewModelScope.launch {
            try {
                val chatId = chatsRepository.createGroupChat(name, selectedUsers.toList())
                clearSelection()
                _events.emit(Event.Created(chatId))
            } catch (ex: Exception) {
                notifier.show(ex)
            }
        }
    }
}
