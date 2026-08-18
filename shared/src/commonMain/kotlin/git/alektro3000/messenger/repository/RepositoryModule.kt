package git.alektro3000.messenger.repository

import git.alektro3000.messenger.network.MainClient
import git.alektro3000.messenger.network.MessageSocketClient
import git.alektro3000.messenger.viewModel.AuthViewModel
import git.alektro3000.messenger.viewModel.ChatViewModel
import git.alektro3000.messenger.viewModel.ChatsViewModel
import git.alektro3000.messenger.viewModel.ProfileViewModel
import git.alektro3000.messenger.viewModel.UsersViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.core.module.dsl.scopedOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

val WebsocketScope = named("WebsocketScope")

val repositoryModule = module {
    scope(named("Session")) {
        scopedOf(::AuthRepository)
        scopedOf(::ChatsRepository)
        scopedOf(::UserRepository)
        scopedOf(::MessageRepository)
        scoped<RealtimeHandler>{
            RealtimeHandler(
                get(),
                get(),
                get(),
                get(WebsocketScope)
            )
        }
        scoped<CoroutineScope>(WebsocketScope) {
            CoroutineScope(
                SupervisorJob() + Dispatchers.Default
            )
        }
    }
}