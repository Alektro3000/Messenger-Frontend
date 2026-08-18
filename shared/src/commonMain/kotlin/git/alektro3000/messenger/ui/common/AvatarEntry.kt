package git.alektro3000.messenger.ui.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun AvatarEntry(url: String?, modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp)
            .padding(4.dp, 4.dp, 12.dp, 4.dp)
    )
    {
        AvatarImage(url, Modifier.padding(4.dp))

        Spacer(modifier = Modifier.width(8.dp))

        Column(
            modifier = Modifier
                .padding(horizontal = 4.dp)
                .fillMaxHeight()
                .weight(1f)
        )
        {
            content()
        }
    }
}