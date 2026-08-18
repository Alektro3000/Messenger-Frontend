package git.alektro3000.messenger

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
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
import org.jetbrains.compose.splitpane.ExperimentalSplitPaneApi
import org.jetbrains.compose.splitpane.HorizontalSplitPane
import org.jetbrains.compose.splitpane.rememberSplitPaneState
import org.koin.core.scope.Scope
import java.awt.Cursor
import kotlin.time.ExperimentalTime


fun Modifier.cursorForHorizontalResize(): Modifier {
    return this.pointerHoverIcon(PointerIcon(Cursor(Cursor.E_RESIZE_CURSOR)))
}

@OptIn(ExperimentalTime::class, ExperimentalSplitPaneApi::class)
@Composable
actual fun MainNavigation(
    snackbarHostState: SnackbarHostState,
    scope: Scope
) {
    val rightController = rememberNavController()
    val leftController = rememberNavController()


    val splitPaneState = rememberSplitPaneState()

    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        })
    { padding ->
        HorizontalSplitPane(splitPaneState = splitPaneState)
        {
            first(minSize = 250.dp) {
                Surface(
                    tonalElevation = 1.dp,
                    shape = RoundedCornerShape(0.dp, 12.dp, 12.dp, 0.dp)
                )
                {

                    var expanded by remember { mutableStateOf(false) }
                    val description = HeaderDescription(
                        dropDownMenu = {
                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                DropdownMenuItem(
                                    leadingIcon = { Icon(Icons.AutoMirrored.Outlined.Chat, null) },
                                    text = { Text(stringResource(Res.string.nav_chat)) },
                                    onClick = {
                                        leftController.navigate(ChatsRoute) {
                                            popUpTo(leftController.graph.startDestinationId)
                                            launchSingleTop = true
                                        }
                                        expanded = false
                                    }
                                )
                                DropdownMenuItem(
                                    leadingIcon = { Icon(Icons.Default.Contacts, null) },
                                    text = { Text(stringResource(Res.string.nav_contacts)) },
                                    onClick = {
                                        leftController.navigate(ContactsRoute) {
                                            popUpTo(leftController.graph.startDestinationId)
                                            launchSingleTop = true
                                        }
                                        expanded = false
                                    }
                                )
                                DropdownMenuItem(
                                    leadingIcon = { Icon(Icons.Default.Settings, null) },
                                    text = { Text(stringResource(Res.string.nav_profile)) },
                                    onClick = {
                                        leftController.navigate(ProfileRoute) {
                                            popUpTo(leftController.graph.startDestinationId)
                                            launchSingleTop = true
                                        }
                                        expanded = false
                                    }
                                )
                            }
                        }
                    ) {
                        expanded = true
                    }
                    NavHost(
                        navController = leftController,
                        startDestination = ChatsRoute,
                        modifier = Modifier.padding(padding),
                    ) {
                        composable<ChatsRoute> {
                            ChatsScreen(
                                description,
                                {
                                    leftController.navigate(CreateGroupRoute)
                                },
                                {
                                    rightController.navigate(GroupChatRoute(it))
                                },
                                scope.get()
                            )
                        }
                        composable<ProfileRoute> {
                            ProfileScreen(
                                description,
                                {
                                    leftController.navigate(LoginRoute) {
                                        popUpTo(0)
                                    }
                                }, scope.get()
                            )
                        }
                        composable<ContactsRoute> {
                            ContactScreen(
                                description,
                                {
                                    rightController.navigate(
                                        DirectChatRoute(
                                            it
                                        )
                                    )
                                }, scope.get()
                            )
                        }
                        composable<CreateGroupRoute> {
                            CreateGroupChatScreen(
                                {
                                    leftController.popBackStack()
                                    leftController.navigate(GroupChatRoute(it))
                                }, {
                                    leftController.popBackStack()
                                },
                                scope.get()
                            )
                        }
                    }
                }
            }
            splitter {

                visiblePart {
                    Box(
                        Modifier
                            .width(1.dp)
                            .fillMaxHeight()
                    )
                }
                handle {
                    Box(
                        Modifier
                            .markAsHandle()
                            .cursorForHorizontalResize()
                            .width(7.dp)
                            .fillMaxHeight()
                    )
                }
            }
            second(minSize = 500.dp) {
                NavHost(
                    navController = rightController,
                    startDestination = EmptyChatRoute,
                    modifier = Modifier.padding(padding),
                ) {
                    composable<EmptyChatRoute> { entry ->
                    }
                    composable<GroupChatRoute> { entry ->
                        val route = entry.toRoute<GroupChatRoute>()
                        val viewModel: ChatViewModel = scope.get()

                        val chatArgs = ChatBaseInfo(route.chatId, null)

                        LaunchedEffect(chatArgs.chatId, chatArgs.receiverId) {
                            viewModel.setArgs(chatArgs)
                        }
                        ChatScreen(null, viewModel)
                    }

                    composable<DirectChatRoute> { entry ->
                        val route = entry.toRoute<DirectChatRoute>()
                        val viewModel: ChatViewModel = scope.get()

                        val chatArgs = ChatBaseInfo(null, route.receiverId)

                        LaunchedEffect(chatArgs.chatId, chatArgs.receiverId) {
                            viewModel.setArgs(chatArgs)
                        }
                        ChatScreen(null, viewModel)
                    }
                }
            }
        }
    }
}
