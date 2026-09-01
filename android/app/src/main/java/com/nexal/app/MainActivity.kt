package com.nexal.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.nexal.app.ui.navigation.NexalNavHost
import com.nexal.app.ui.theme.NexalTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    companion object {
        private val _deepLinkFlow = MutableSharedFlow<String>(extraBufferCapacity = 1)
        val deepLinkFlow: SharedFlow<String> = _deepLinkFlow.asSharedFlow()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Handle deep link from initial launch
        handleDeepLink(intent)

        setContent {
            NexalTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = androidx.compose.material3.MaterialTheme.colorScheme.background
                ) {
                    NexalNavHost()
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleDeepLink(intent)
    }

    private fun handleDeepLink(intent: Intent?) {
        val data = intent?.data ?: return
        val host = data.host.orEmpty()
        val path = data.path.orEmpty()
        val full = "${data.scheme}://$host$path"

        when {
            // nexal://app/login (from auth-callback page)
            data.scheme == "nexal" && (path.contains("login") || full.contains("login") || host == "app") -> {
                _deepLinkFlow.tryEmit("login")
            }
            path.startsWith("/reset-password") -> {
                val token = data.getQueryParameter("token")
                if (!token.isNullOrBlank()) {
                    _deepLinkFlow.tryEmit("reset_password/$token")
                }
            }
            path.startsWith("/api/auth/verify") || path.contains("auth-callback") -> {
                _deepLinkFlow.tryEmit("login")
            }
        }
    }
}
