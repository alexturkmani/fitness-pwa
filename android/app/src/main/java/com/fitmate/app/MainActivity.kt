package com.fitmate.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.fitmate.app.ui.navigation.FitMateNavHost
import com.fitmate.app.ui.theme.FitMateTheme
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
            FitMateTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    FitMateNavHost()
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
        val path = data.path ?: return

        when {
            path.startsWith("/reset-password") -> {
                val token = data.getQueryParameter("token")
                if (!token.isNullOrBlank()) {
                    _deepLinkFlow.tryEmit("reset_password/$token")
                }
            }
            path.startsWith("/api/auth/verify") -> {
                // Email verification is handled by the server via browser
                // Just navigate to login after verification
                _deepLinkFlow.tryEmit("login")
            }
        }
    }
}
