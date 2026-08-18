package git.alektro3000.messenger

import android.app.Application
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import coil3.ImageLoader
import git.alektro3000.messenger.local.localModule
import git.alektro3000.messenger.network.networkModule
import git.alektro3000.messenger.repository.repositoryModule
import git.alektro3000.messenger.ui.LocalImageLoader
import git.alektro3000.messenger.viewModel.viewModelModule
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.compose.koinInject
import java.net.InetSocketAddress
import java.net.Socket


class MessengerApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        initKoin {
            androidContext(this@MessengerApplication)
            modules(androidModule)
        }
    }
}

class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        Thread {
            try {
                Socket().use { socket ->
                    socket.connect(InetSocketAddress("10.0.2.2", 5069), 5000)
                    Log.d("TCP_TEST", "CONNECTED")
                }
            } catch (e: Exception) {
                Log.e("TCP_TEST", "FAILED: ${e::class.java.name}: ${e.message}", e)
            }
        }.start()
        enableEdgeToEdge()
        setContent {
            App()

        }
    }
}