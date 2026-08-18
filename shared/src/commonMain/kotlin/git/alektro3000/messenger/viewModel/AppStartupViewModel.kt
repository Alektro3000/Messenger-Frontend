package git.alektro3000.messenger.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import git.alektro3000.messenger.core.SessionScopeManager
import git.alektro3000.messenger.local.SessionStorage
import git.alektro3000.messenger.repository.AuthRepository
import git.alektro3000.messenger.repository.MessageRepository
import git.alektro3000.messenger.repository.RealtimeHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface StartupState {
    data object Loading : StartupState
    data class Authenticated(val userId: Long) : StartupState
    data object Unauthenticated : StartupState
}
class AppStartupViewModel(
    private val sessionStorage: SessionStorage,
    private val sessionScopeManager: SessionScopeManager,
) : ViewModel() {
    val state = sessionStorage.userId
        .map { userId ->
            if (userId == null)
                StartupState.Unauthenticated
            else
                StartupState.Authenticated(userId)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = StartupState.Loading
        )

    fun start() {
        sessionScopeManager.recreateScope()
    }
}