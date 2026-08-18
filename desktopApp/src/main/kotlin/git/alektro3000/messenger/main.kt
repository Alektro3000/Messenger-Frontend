package git.alektro3000.messenger

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import git.alektro3000.messenger.desktopModule
import git.alektro3000.messenger.initKoin
import java.awt.Dimension
import java.io.File

fun main() {

    initKoin{
        modules(
            desktopModule
        )
    }

    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "Messenger",
        ) {
            window.minimumSize = Dimension(800, 600)
            App()
        }
    }
}
