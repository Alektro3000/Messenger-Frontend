package git.alektro3000.messenger

import androidx.compose.foundation.layout.imePadding
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
import git.alektro3000.messenger.ui.common.HeaderDescription
import git.alektro3000.messenger.ui.contacts.ContactScreen
import git.alektro3000.messenger.ui.contacts.CreateGroupChatScreen
import git.alektro3000.messenger.ui.login.LoginScreen
import git.alektro3000.messenger.ui.profile.ProfileScreen
import git.alektro3000.messenger.viewModel.ChatViewModel
import messenger.shared.generated.resources.Res
import messenger.shared.generated.resources.nav_chat
import messenger.shared.generated.resources.nav_contacts
import messenger.shared.generated.resources.nav_profile
import org.jetbrains.compose.resources.stringResource
import org.koin.core.scope.Scope
import kotlin.time.ExperimentalTime


private val bottomBarRoutes = setOf(
    ChatsRoute::class,
    ContactsRoute::class,
    ProfileRoute::class
)

@OptIn(ExperimentalTime::class)
@Composable
actual fun MainNavigation(
    snackbarHostState: SnackbarHostState,
    scope: Scope
) {
    val navController = rememberNavController()

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination

    val showBottomBar = currentDestination?.let { destination ->
        bottomBarRoutes.any { destination.hasRoute(it) }
    } == true

    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbarHostState,
                modifier = Modifier.imePadding())
        },
        bottomBar = {
            if (!showBottomBar)
                return@Scaffold
            NavigationBar {
                NavigationBarItem(
                    selected = currentDestination.hasRoute<ChatsRoute>(),
                    onClick = {
                        navController.navigate(ChatsRoute) {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    },
                    icon = { Icon(Icons.AutoMirrored.Outlined.Chat, null) },
                    label = { Text(stringResource(Res.string.nav_chat)) }
                )

                NavigationBarItem(
                    selected = currentDestination.hasRoute<ContactsRoute>(),
                    onClick = {
                        navController.navigate(ContactsRoute) {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    },
                    icon = { Icon(Icons.Default.Contacts, null) },
                    label = { Text(stringResource(Res.string.nav_contacts)) }
                )

                NavigationBarItem(
                    selected = currentDestination.hasRoute<ProfileRoute>(),
                    onClick = {
                        navController.navigate(ProfileRoute) {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    },
                    icon = { Icon(Icons.Default.Settings, null) },
                    label = { Text(stringResource(Res.string.nav_profile)) }
                )

            }
        })
    { padding ->
        NavHost(
            navController = navController,
            startDestination = ChatsRoute,
            modifier = Modifier.padding(padding),
        ) {
            val description = HeaderDescription(null, null);
            composable<ChatsRoute> {
                ChatsScreen(
                    description,
                    {
                        navController.navigate(CreateGroupRoute)
                    },
                    {
                        navController.navigate(GroupChatRoute(it))
                    },
                    scope.get()
                )
            }
            composable<ProfileRoute> {
                ProfileScreen(
                    description,{
                    navController.navigate(LoginRoute) {
                        popUpTo(0)
                    }
                }, scope.get())
            }
            composable<ContactsRoute> {
                ContactScreen(
                    description,{
                    navController.navigate(
                        DirectChatRoute(
                            it
                        )
                    )
                }, scope.get())
            }
            composable<CreateGroupRoute> {
                CreateGroupChatScreen({
                    navController.popBackStack()
                    navController.navigate(GroupChatRoute(it))
                }, {
                    navController.popBackStack()
                },
                    scope.get())
            }

            composable<GroupChatRoute> { entry ->
                val route = entry.toRoute<GroupChatRoute>()
                val viewModel: ChatViewModel = scope.get()

                val chatArgs = ChatBaseInfo(route.chatId, null)

                LaunchedEffect(chatArgs.chatId, chatArgs.receiverId) {
                    viewModel.setArgs(chatArgs)
                }
                ChatScreen({
                    navController.popBackStack()
                }, viewModel)
            }

            composable<DirectChatRoute> { entry ->
                val route = entry.toRoute<DirectChatRoute>()
                val viewModel: ChatViewModel = scope.get()

                val chatArgs = ChatBaseInfo(null, route.receiverId)

                LaunchedEffect(chatArgs.chatId, chatArgs.receiverId) {
                    viewModel.setArgs(chatArgs)
                }
                ChatScreen({
                    navController.popBackStack()
                }, viewModel)
            }
        }
    }
}
