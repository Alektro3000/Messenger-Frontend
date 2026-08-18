package git.alektro3000.messenger.extensions

import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.isMetaPressed

fun KeyEvent.ifRefreshButtonPressed(function: () -> Unit) : Boolean
{
    if (
        type == KeyEventType.KeyDown &&
        (
            key == Key.F5 ||
            key == Key.Refresh ||
            (key == Key.R && (isCtrlPressed || isMetaPressed) )
        )
    )
    {
        function()
        return true
    }
    return false
}