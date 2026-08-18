package git.alektro3000.messenger.network

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.core.qualifier.named
import org.koin.dsl.module

val MainClient = named("MainClient")
val AuthClient = named("AuthClient")

val networkModule = module {

    scope(named("Session")) {
        scoped { AuthApi(get(AuthClient)) }
        scoped { ChatApi(get(MainClient)) }
        scoped { UserApi(get(MainClient)) }
        scoped { MessageApi(get(MainClient)) }
        scoped { MessageSocketClient(get(MainClient)) }
    }

}