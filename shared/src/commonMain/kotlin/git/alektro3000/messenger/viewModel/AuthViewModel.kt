package git.alektro3000.messenger.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import git.alektro3000.messenger.core.SessionScopeManager
import git.alektro3000.messenger.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel (
    private val repository: AuthRepository,
    private val notifier: Notifier,
    private val sessionScopeManager: SessionScopeManager,
) : ViewModel() {

    sealed interface LoginUiState {
        object Idle : LoginUiState
        object Loading : LoginUiState
        object Success : LoginUiState
        object Error : LoginUiState
    }

    private val _loginSuccessful =
        MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val loginSuccessful = _loginSuccessful.asStateFlow()

    private val _registerSuccessful =
        MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val registerSuccessful = _registerSuccessful.asStateFlow()

    public fun login(username: String, password: String) {
        viewModelScope.launch {
            try {
                _loginSuccessful.value = LoginUiState.Loading
                repository.login(username, password)
                sessionScopeManager.recreateScope()
                _loginSuccessful.value = LoginUiState.Success
            } catch (e: Exception) {
                notifier.show(e)
                _loginSuccessful.value = LoginUiState.Error
            }
        }
    }

    public fun register(username: String, password: String) {
        viewModelScope.launch {
            try {
                _registerSuccessful.value = LoginUiState.Loading
                repository.register(username, password)
                _registerSuccessful.value = LoginUiState.Success
            } catch (e: Exception) {
                notifier.show(e)
                _registerSuccessful.value = LoginUiState.Error
            }
        }
    }
}