package git.alektro3000.messenger.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.paging.LoadState
import git.alektro3000.messenger.LoginRoute
import git.alektro3000.messenger.ProfileRoute
import git.alektro3000.messenger.extensions.ifRefreshButtonPressed
import git.alektro3000.messenger.extensions.toCreateTime
import git.alektro3000.messenger.model.user.PickedProfile
import git.alektro3000.messenger.model.user.UserFull
import git.alektro3000.messenger.ui.common.Header
import git.alektro3000.messenger.ui.common.HeaderDescription
import git.alektro3000.messenger.viewModel.ChatsViewModel
import git.alektro3000.messenger.viewModel.ProfileViewModel
import messenger.shared.generated.resources.Res
import messenger.shared.generated.resources.nav_contacts
import messenger.shared.generated.resources.nav_profile
import messenger.shared.generated.resources.profile_bio
import messenger.shared.generated.resources.profile_created_at
import messenger.shared.generated.resources.profile_loading
import messenger.shared.generated.resources.profile_logout
import messenger.shared.generated.resources.profile_name
import messenger.shared.generated.resources.profile_no_bio
import messenger.shared.generated.resources.profile_no_name
import messenger.shared.generated.resources.profile_no_surname
import messenger.shared.generated.resources.profile_surname
import messenger.shared.generated.resources.profile_update
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ProfileScreen(
    description: HeaderDescription,
    onNavigateToLogin: () -> Unit,
    viewModel: ProfileViewModel
) {

    val state by viewModel.meState.collectAsState()
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Scaffold(
        modifier = Modifier.onPreviewKeyEvent {
            it.ifRefreshButtonPressed {
                viewModel.refreshMe()
            }
        }
            .focusRequester(focusRequester)
            .focusable(),
        content =
            { padding ->

                PullToRefreshBox(
                    isRefreshing = state is ProfileViewModel.MeUiState.Loading,
                    onRefresh = {
                        viewModel.refreshMe()
                    }
                )
                {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxSize()
                    )
                    {
                        Header(description,
                            stringResource(Res.string.nav_profile),
                            modifier = Modifier.align(Alignment.Start))

                        Column(
                            modifier = Modifier
                                .padding(top = 34.dp, start = 16.dp, end = 16.dp, bottom = 8.dp)

                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        )
                        {
                            when (val s = state) {
                                is ProfileViewModel.MeUiState.Loading -> {

                                    Spacer(Modifier.height(8.dp))

                                    CircularProgressIndicator(
                                        modifier = Modifier.size(96.dp)
                                    )

                                    Spacer(Modifier.height(16.dp))

                                    Text(stringResource(Res.string.profile_loading))
                                }

                                is ProfileViewModel.MeUiState.Success -> {
                                    val user = s.user
                                    ProfileScreen(onNavigateToLogin, user, viewModel)
                                }

                                else -> {}
                            }
                            Button(
                                onClick = {
                                    viewModel.logout()
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                            {
                                Text(stringResource(Res.string.profile_logout))
                            }
                        }
                    }
                }
            })
}

@Composable
fun ProfileScreen(onNavigateToLogin: () -> Unit, user: UserFull, viewModel: ProfileViewModel) {
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                ProfileViewModel.MeEvent.NavigateToLogin -> {
                    onNavigateToLogin()
                }
            }
        }
    }

        val textFieldStateName = rememberTextFieldState(user.name ?: "")
        val textFieldStateSurname = rememberTextFieldState(user.surname ?: "")
        val textFieldStateBio = rememberTextFieldState(user.bio ?: "")

        AvatarPickerUi(user.avatarUrl, Modifier, viewModel)

        /*
        Spacer(Modifier.height(10.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
        ){
            Text(stringResource(Res.string.profile_created_at) + user.createdAt.toCreateTime(),
                modifier = Modifier.padding(4.dp))
            Text(user.displayName,
                modifier = Modifier.padding(4.dp))
        }*/
        Spacer(Modifier.height(10.dp))

        CustomTextField(
            textFieldStateName,
            Res.string.profile_no_name,
            Res.string.profile_name
        )
        CustomTextField(
            textFieldStateSurname,
            Res.string.profile_no_surname,
            Res.string.profile_surname
        )
        CustomTextField(
            textFieldStateBio,
            Res.string.profile_no_bio,
            Res.string.profile_bio
        )

        Spacer(Modifier.height(10.dp))

        Button(
            onClick = {
                viewModel.updateProfile(
                    PickedProfile(
                        textFieldStateName.text.toString(),
                        textFieldStateSurname.text.toString(),
                        textFieldStateBio.text.toString()
                    )
                )
            },
            modifier = Modifier.fillMaxWidth()
        )
        {
            Text(stringResource(Res.string.profile_update))
        }

}

@Composable
fun CustomTextField(
    textFieldState: TextFieldState,
    default: StringResource,
    label: StringResource
) {
    OutlinedTextField(
        textFieldState,
        label = { Text(stringResource(label)) },
        shape = RoundedCornerShape(12.dp),
        placeholder = { Text(stringResource(default)) },
        modifier = Modifier.fillMaxWidth()
    )
}