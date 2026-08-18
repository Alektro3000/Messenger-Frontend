package git.alektro3000.messenger.viewModel

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class Notifier {
    private val _messages = MutableSharedFlow<String>()
    val messages = _messages.asSharedFlow()

    suspend fun show(message: String) {
        _messages.emit(message)
    }
    suspend fun show(exception: Throwable) {
        _messages.emit(exception.message ?: "Unknown Error")
    }

    fun tryShow(message: String) {
        _messages.tryEmit(message)
    }
}