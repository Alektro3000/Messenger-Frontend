package git.alektro3000.messenger.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import git.alektro3000.messenger.actions.PickedAvatar
import git.alektro3000.messenger.core.SessionScopeManager
import git.alektro3000.messenger.local.SessionStorage
import git.alektro3000.messenger.model.user.PickedProfile
import git.alektro3000.messenger.model.user.UserFull
import git.alektro3000.messenger.repository.AuthRepository
import git.alektro3000.messenger.repository.UserRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository,
    private val sessionScopeManager: SessionScopeManager,
    private val notifier: Notifier
) : ViewModel() {
    sealed interface MeEvent {
        data object NavigateToLogin : MeEvent
    }

    private val _events = MutableSharedFlow<MeEvent>()
    val events = _events.asSharedFlow()

    fun logout() {
        viewModelScope.launch {
            try {
                authRepository.logout()
                _events.emit(MeEvent.NavigateToLogin)
                sessionScopeManager.recreateScope()
            } catch (ex: Exception) {
                notifier.show(ex)
            }
        }
    }

    val meState: StateFlow<MeUiState> =
        userRepository.observeMe()
            .map<UserFull?, MeUiState> { user ->
                if (user == null) MeUiState.Loading
                else MeUiState.Success(user)
            }
            .catch { ex ->
                notifier.show(ex)
                emit(MeUiState.Error(ex.message ?: "Unknown error"))
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                MeUiState.Loading
            )

    init {
        refreshMe()
    }

    fun refreshMe() {
        viewModelScope.launch {
            try {
                userRepository.refreshMe()
            } catch (ex: Exception) {
                notifier.show(ex)
            }
        }
    }
    sealed interface MeUiState {
        data object Loading : MeUiState
        data class Success(val user: UserFull) : MeUiState
        data class Error(val message: String) : MeUiState
    }

    sealed interface AvatarUiState {
        data object Idle : AvatarUiState
        data object Loading : AvatarUiState
        data object Success : AvatarUiState
        data object Error : AvatarUiState
    }
    private val _avatarState = MutableStateFlow<AvatarUiState>(AvatarUiState.Idle)
    val avatarUiState: StateFlow<AvatarUiState> = _avatarState.asStateFlow()

    fun uploadAvatar(avatar: PickedAvatar)
    {
        viewModelScope.launch {

            _avatarState.value = AvatarUiState.Loading

            try {
                userRepository.uploadAvatar(avatar)
                _avatarState.value = AvatarUiState.Success
            } catch (ex: Exception) {
                notifier.show(ex)
            }
        }
    }
    private val _profileState = MutableStateFlow<AvatarUiState>(AvatarUiState.Idle)
    val profileUiState: StateFlow<AvatarUiState> = _profileState.asStateFlow()

    fun updateProfile(profile: PickedProfile)
    {
        viewModelScope.launch {

            _profileState.value = AvatarUiState.Loading

            try {
                userRepository.updateProfile(profile)
                _profileState.value = AvatarUiState.Success
            } catch (ex: Exception) {
                notifier.show(ex)
                _profileState.value = AvatarUiState.Error
            }
        }
    }

}