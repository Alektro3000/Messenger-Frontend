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
import git.alektro3000.messenger.repository.UserRepository
import git.alektro3000.messenger.repository.pagingSources.UsersPagingSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlin.time.Duration.Companion.milliseconds

class UsersViewModel (
    private val userRepository: UserRepository,
    private val notifier: Notifier,
) : ViewModel() {

    var query by mutableStateOf("")
        private set


    fun updateQuery(value: String) {
        query = value
    }

    @OptIn(ExperimentalCoroutinesApi::class)
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

}
