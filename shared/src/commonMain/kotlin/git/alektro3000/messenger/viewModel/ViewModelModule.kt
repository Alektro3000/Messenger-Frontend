package git.alektro3000.messenger.viewModel

import git.alektro3000.messenger.core.SessionScopeManager
import org.koin.core.module.dsl.scopedOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

val viewModelModule = module {
    singleOf(::Notifier)
    single{SessionScopeManager(getKoin())}
    singleOf(::AppStartupViewModel)
    scope(named("Session")) {
        scopedOf(::AuthViewModel)
        scopedOf(::ChatViewModel)
        scopedOf(::ChatsViewModel)
        scopedOf(::UsersViewModel)
        scopedOf(::CreateGroupChatViewModel)
        scopedOf(::ProfileViewModel)
    }
}
