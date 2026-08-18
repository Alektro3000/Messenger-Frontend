package git.alektro3000.messenger.ui.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Button
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecureTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import git.alektro3000.messenger.ui.common.Header
import git.alektro3000.messenger.ui.common.HeaderDescription
import git.alektro3000.messenger.viewModel.AuthViewModel
import messenger.shared.generated.resources.Res
import messenger.shared.generated.resources.login_action_login
import messenger.shared.generated.resources.login_action_register
import messenger.shared.generated.resources.login_password
import messenger.shared.generated.resources.login_username
import messenger.shared.generated.resources.nav_contacts
import messenger.shared.generated.resources.nav_login
import org.jetbrains.compose.resources.stringResource

@Composable
fun LoginScreen(
    snackbarHostState: SnackbarHostState,
    viewModel: AuthViewModel
) {
    val loginSuccessful by viewModel.loginSuccessful.collectAsState()
    val registerSuccessful by viewModel.registerSuccessful.collectAsState()
    val username = rememberTextFieldState()
    val password = rememberTextFieldState()

    val isLoadingLogin = loginSuccessful is AuthViewModel.LoginUiState.Loading
    val isLoadingRegister = registerSuccessful is AuthViewModel.LoginUiState.Loading

    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbarHostState,
                modifier = Modifier.imePadding())
        }
    )
    { paddingValues ->
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically),
            modifier = Modifier
                .padding(paddingValues)
                .padding(8.dp)
        )
        {
            Header(
                HeaderDescription.defaultNoNavigation,
                stringResource(Res.string.nav_login)
            )
            TextField(
                label = { Text(stringResource(Res.string.login_username)) },
                state = username,
                modifier = Modifier.fillMaxWidth(),
            )
            SecureTextField(
                label = { Text(stringResource(Res.string.login_password)) },
                state = password,
                modifier = Modifier.fillMaxWidth(),
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.align(Alignment.End)
            )
            {
                Button(
                    onClick = {
                        viewModel.login(
                            username.text.toString(),
                            password.text.toString()
                        )
                    },
                    enabled = !isLoadingLogin
                ) {
                    Text(
                        stringResource(Res.string.login_action_login)
                    )
                }

                Button(
                    onClick = {
                        viewModel.register(
                            username.text.toString(),
                            password.text.toString()
                        )
                    },
                    enabled = !isLoadingRegister
                ) {
                    Text(
                        stringResource(Res.string.login_action_register)
                    )
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun LoginScreenPreview() {
    val isLoadingRegister = false
    Button(
        onClick = {
        },
        enabled = !isLoadingRegister
    ) {

        if (!isLoadingRegister)
            Text(
                stringResource(Res.string.login_action_register)
            )
        else
            LoadingIndicator()

    }

}
