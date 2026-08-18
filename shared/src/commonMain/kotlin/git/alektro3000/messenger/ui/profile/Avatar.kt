package git.alektro3000.messenger.ui.profile

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import git.alektro3000.messenger.AppConfig
import git.alektro3000.messenger.actions.PickedAvatar
import git.alektro3000.messenger.ui.LocalImageLoader
import git.alektro3000.messenger.ui.common.AvatarImage
import git.alektro3000.messenger.viewModel.ChatsViewModel
import git.alektro3000.messenger.viewModel.ProfileViewModel
import org.koin.compose.viewmodel.koinViewModel
import kotlin.time.ExperimentalTime

@Composable
expect fun AvatarPickerUi(
    avatarUrl: String?, modifier: Modifier = Modifier, viewModel: ProfileViewModel)

@Composable
fun AvatarEditStateless(avatarUrl: String?, modifier: Modifier = Modifier, onEdit: () -> Unit) {
    Box(modifier = modifier)
    {
        AvatarImage(avatarUrl, Modifier.size(120.dp))
        IconButton(
            onClick = onEdit,
            modifier = Modifier
                .background(
                    MaterialTheme.colorScheme.primary,
                    CircleShape
                )
                .align(Alignment.BottomEnd)
        ) {
            Icon(
                imageVector = Icons.Default.AddAPhoto,
                tint = MaterialTheme.colorScheme.onPrimary,
                contentDescription = "edit_avatar")
        }
    }
}

@Composable
@Preview(showBackground = true)
fun PreviewAvatarEdit()
{
    AvatarEditStateless(
        "/uploads/avatars/user-7-700ddc3a-1e83-4caa-8096-4d5021ba66ca.png"
    ) {}
}
