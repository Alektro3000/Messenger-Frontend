package git.alektro3000.messenger

import git.alektro3000.messenger.local.localModule
import git.alektro3000.messenger.network.networkModule
import git.alektro3000.messenger.repository.repositoryModule
import git.alektro3000.messenger.viewModel.viewModelModule
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration


public fun initKoin(config: KoinAppDeclaration? = null) {
    startKoin {
        config?.invoke(this)
        modules(
            networkModule,
            localModule,
            repositoryModule,
            viewModelModule
        )
    }
}