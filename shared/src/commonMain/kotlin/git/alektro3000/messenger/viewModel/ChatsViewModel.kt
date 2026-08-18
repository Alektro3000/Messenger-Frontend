package git.alektro3000.messenger.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import git.alektro3000.messenger.actions.PickedAvatar
import git.alektro3000.messenger.local.AppDatabase
import git.alektro3000.messenger.model.user.UserFull
import git.alektro3000.messenger.network.ChatApi
import git.alektro3000.messenger.network.UserApi
import git.alektro3000.messenger.repository.ChatsRepository
import git.alektro3000.messenger.repository.UserRepository
import git.alektro3000.messenger.repository.mediator.ChatsRemoteMediator
import git.alektro3000.messenger.repository.pagingSources.UsersPagingSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class ChatsViewModel(
    private val apiChat: ChatApi,
    private val db: AppDatabase
) : ViewModel() {

    var chatQuery by mutableStateOf("")
        private set

    fun updateChatQuery(value: String) {
        chatQuery = value
    }

    @OptIn(ExperimentalPagingApi::class, FlowPreview::class)
    val chats =
        snapshotFlow { chatQuery }
            .debounce(300.milliseconds)
            .distinctUntilChanged()
            .flatMapLatest { query ->
                Pager(
                    config = PagingConfig(
                        pageSize = 30,
                        prefetchDistance = 10
                    ),
                    remoteMediator = ChatsRemoteMediator(apiChat, db, query),
                    pagingSourceFactory = {
                        db.chatDao().pagingChats(query)
                    }
                )
                .flow
                .cachedIn(viewModelScope)
            }
}
