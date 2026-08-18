package git.alektro3000.messenger

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import git.alektro3000.messenger.model.chats.ChatBaseInfo
import git.alektro3000.messenger.ui.chat.ChatScreen
import git.alektro3000.messenger.ui.chats.ChatsScreen
import git.alektro3000.messenger.ui.contacts.ContactScreen
import git.alektro3000.messenger.ui.contacts.CreateGroupChatScreen
import git.alektro3000.messenger.ui.login.LoginScreen
import git.alektro3000.messenger.ui.profile.ProfileScreen
import git.alektro3000.messenger.viewModel.ChatViewModel
import kotlinx.serialization.Serializable
import messenger.shared.generated.resources.Res
import messenger.shared.generated.resources.nav_chat
import messenger.shared.generated.resources.nav_contacts
import messenger.shared.generated.resources.nav_profile
import org.jetbrains.compose.resources.stringResource
import org.koin.core.scope.Scope
import kotlin.time.ExperimentalTime


@Serializable
data object ChatsRoute

@Serializable
data object LoginRoute

@Serializable
data object ProfileRoute

@Serializable
data object ContactsRoute

@Serializable
data object CreateGroupRoute

@Serializable
data object EmptyChatRoute

@Serializable
data class GroupChatRoute(val chatId: Long)

@Serializable
data class DirectChatRoute(val receiverId: Long)

@Composable
expect fun MainNavigation(
    snackbarHostState: SnackbarHostState,
    scope: Scope
)

