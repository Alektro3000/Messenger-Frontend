package git.alektro3000.messenger.core

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.Koin
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope
import java.util.UUID

class SessionScopeManager(
    private val koin: Koin
) {
    private val _scopeVersion = MutableStateFlow(0)
    val scopeVersion = _scopeVersion.asStateFlow()
    private var scope: Scope? = null

    fun recreateScope(): Scope {
        scope?.close()

        scope = koin.createScope(
            scopeId = UUID.randomUUID().toString(),
            qualifier = named("Session")
        )
        _scopeVersion.value++

        return scope!!
    }

    fun currentScope(): Scope? = scope
}