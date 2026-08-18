package git.alektro3000.messenger.ui.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

data class HeaderDescription(
    val dropDownMenu: @Composable (() -> Unit)?,
    val onShowMenu: (() -> Unit)?

){
    companion object {
        val defaultNoNavigation = HeaderDescription(null, null)
    }
}

@Composable
fun Header(description: HeaderDescription, header: String, modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .padding(horizontal = 16.dp, vertical=4.dp)
    ) {

        description.onShowMenu?.let {
            Box {
                IconButton(
                    onClick = it
                )
                {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "menu"
                    )
                }
                description.dropDownMenu?.invoke()
            }
        }
        Text(
            header,
            style = MaterialTheme.typography.headlineMedium
        )
    }
    Spacer(Modifier.height(8.dp))
}