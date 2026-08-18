package git.alektro3000.messenger.ui.common

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import messenger.shared.generated.resources.Res
import messenger.shared.generated.resources.common_search
import messenger.shared.generated.resources.nav_contacts
import org.jetbrains.compose.resources.stringResource


@Composable
fun SearchBar(value: String, onValueChanged: (String) -> Unit = {})
{
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    )
    {
        //placeholder = { Text(stringResource(Res.string.common_search)) },
        val shape = RoundedCornerShape(30.dp)
        var isFocused by remember { mutableStateOf(false) }


        val borderColor by animateColorAsState(
            targetValue = if (isFocused)
                MaterialTheme.colorScheme.primary
            else
                Color.DarkGray,
            label = "borderColor",
        )

        val borderWidth by animateDpAsState(
            targetValue = if (isFocused) 2.dp else 1.dp,
            label = "borderWidth",
        )

        BasicTextField(
            value = value,
            onValueChange = onValueChanged,
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .border(borderWidth, color = borderColor, shape = shape)
                .clip(shape)
                .padding(8.dp)
                .onFocusChanged {
                    isFocused = it.isFocused
                },
            textStyle = MaterialTheme.typography.bodyMedium
        ){ internalTextBox ->
            Row {
                Icon(Icons.Default.Search, null)
                Spacer(Modifier.width(10.dp))
                Box(contentAlignment = Alignment.CenterStart)
                {
                    if (value.isBlank()) {
                        Text(stringResource(Res.string.common_search),
                            style = MaterialTheme.typography.bodyMedium)
                    }
                    internalTextBox()
                }
            }
        }
    }
}