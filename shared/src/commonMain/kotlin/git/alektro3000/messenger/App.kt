package git.alektro3000.messenger

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil3.ImageLoader
import git.alektro3000.messenger.core.SessionScopeManager
import git.alektro3000.messenger.ui.LocalImageLoader
import git.alektro3000.messenger.ui.login.LoginScreen
import git.alektro3000.messenger.viewModel.AppStartupViewModel
import git.alektro3000.messenger.viewModel.Notifier
import git.alektro3000.messenger.viewModel.StartupState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource

import messenger.shared.generated.resources.Res
import messenger.shared.generated.resources.compose_multiplatform
import org.koin.compose.getKoin
import org.koin.compose.koinInject

@Composable
@Preview
fun App() {
    val localImageLoader: ImageLoader = koinInject()

    val notifier: Notifier = koinInject()

    val snackbarHostState = remember { SnackbarHostState() }

    val koin = getKoin()
    val startupViewModel: AppStartupViewModel = koin.get()

    LaunchedEffect(Unit) {
        notifier.messages.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    val state by startupViewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        startupViewModel.start()
    }

    val sessionScopeManager = koin.get<SessionScopeManager>()
    val scopeVersion by sessionScopeManager.scopeVersion.collectAsState()
    val scope = remember(scopeVersion) {
        sessionScopeManager.currentScope()
    }
    CompositionLocalProvider(
        LocalImageLoader provides localImageLoader
    ) {
        MaterialTheme {
            when(val current = state)
            {
                is StartupState.Loading -> SplashScreen()
                is StartupState.Unauthenticated -> key("guest") {
                    LoginScreen(snackbarHostState, scope!!.get())
                }
                is StartupState.Authenticated -> key("user-${current.userId}-${scopeVersion}") {
                    MainNavigation(snackbarHostState, scope!!)
                }
            }
        }
    }
}

