package git.alektro3000.messenger.local

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val localModule = module {

    singleOf(::SessionStorage)
}