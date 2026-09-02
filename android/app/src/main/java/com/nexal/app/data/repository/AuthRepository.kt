package com.nexal.app.data.repository

import android.content.Context
import com.nexal.app.data.local.NexalDatabase
import com.nexal.app.domain.model.AuthState
import com.nexal.app.util.Resource
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.providers.builtin.IDToken
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Serializable
data class SubscriptionRow(
    val status: String = "inactive",
    @SerialName("expiry_time") val expiryTime: String? = null
)

@Singleton
class AuthRepository @Inject constructor(
    private val auth: Auth,
    private val postgrest: Postgrest,
    private val database: NexalDatabase,
    @ApplicationContext private val context: Context
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val prefs by lazy {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    companion object {
        private const val PREFS_NAME = "nexal_auth_prefs"
        private fun paywallKey(userId: String) = "paywall_dismissed_$userId"

        /** Hosted HTML callback so verify links don't hit the bare Supabase API root. */
        // Live edge function that renders a success page + nexal:// deep link
        private val AUTH_CALLBACK_URL =
            com.nexal.app.BuildConfig.SUPABASE_URL.trimEnd('/') + "/functions/v1/email-confirmed"
    }

    init {
        // Observe Supabase session changes
        scope.launch {
            auth.sessionStatus.collect { status ->
                when (status) {
                    is SessionStatus.Authenticated -> {
                        val session = status.session
                        val user = session.user
                        if (user != null) {
                            val subscriptionActive = checkSubscriptionStatus(user.id)
                            _authState.value = AuthState.Authenticated(
                                userId = user.id,
                                email = user.email ?: "",
                                name = user.userMetadata?.get("name")?.toString()?.removeSurrounding("\""),
                                subscriptionActive = subscriptionActive
                            )
                        }
                    }
                    is SessionStatus.NotAuthenticated -> {
                        _authState.value = AuthState.Unauthenticated
                    }
                    is SessionStatus.Initializing -> {
                        // Don't replace the sign-in screen with a loading flash during logout.
                        if (_authState.value !is AuthState.Unauthenticated) {
                            _authState.value = AuthState.Loading
                        }
                    }
                    else -> {}
                }
            }
        }
    }

    private suspend fun checkSubscriptionStatus(userId: String): Boolean {
        return try {
            val result = postgrest.from("user_subscriptions")
                .select { filter { eq("user_id", userId) } }
                .decodeSingleOrNull<SubscriptionRow>()
            val expiry = result?.expiryTime?.let { runCatching { Instant.parse(it) }.getOrNull() }
            result?.status == "active" && (expiry == null || expiry.isAfter(Instant.now()))
        } catch (_: Exception) {
            false
        }
    }

    suspend fun login(email: String, password: String): Resource<AuthState.Authenticated> {
        return try {
            auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
            // Wait for session to update
            val state = waitForAuthenticatedState()
            if (state != null) Resource.Success(state)
            else Resource.Error("Login timed out. Please check your connection and try again.")
        } catch (e: Exception) {
            val msg = when {
                e.message?.contains("Invalid login credentials") == true -> "Invalid email or password"
                e.message?.contains("Email not confirmed") == true -> "Please verify your email first"
                else -> e.message ?: "Login failed. Please try again."
            }
            Resource.Error(msg)
        }
    }

    suspend fun register(name: String, email: String, password: String): Resource<Unit> {
        return try {
            auth.signUpWith(Email, redirectUrl = AUTH_CALLBACK_URL) {
                this.email = email
                this.password = password
                data = kotlinx.serialization.json.buildJsonObject {
                    put("name", kotlinx.serialization.json.JsonPrimitive(name))
                }
            }
            Resource.Success(Unit)
        } catch (e: Exception) {
            val msg = when {
                e.message?.contains("already registered") == true -> "An account with this email already exists"
                else -> e.message ?: "Registration failed. Please try again."
            }
            Resource.Error(msg)
        }
    }

    suspend fun googleSignIn(idToken: String, rawNonce: String = ""): Resource<AuthState.Authenticated> {
        return try {
            auth.signInWith(IDToken) {
                this.provider = Google
                this.idToken = idToken
                if (rawNonce.isNotBlank()) {
                    this.nonce = rawNonce
                }
            }
            val state = waitForAuthenticatedState()
            if (state != null) Resource.Success(state)
            else Resource.Error("Google sign-in timed out. Please try again.")
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Google sign-in failed. Please try again.")
        }
    }

    suspend fun forgotPassword(email: String): Resource<Unit> {
        return try {
            auth.resetPasswordForEmail(email, redirectUrl = AUTH_CALLBACK_URL)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to send reset email")
        }
    }

    suspend fun changePassword(newPassword: String): Resource<Unit> {
        return try {
            auth.updateUser { this.password = newPassword }
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to change password")
        }
    }

    suspend fun changeEmail(newEmail: String): Resource<Unit> {
        return try {
            auth.updateUser { this.email = newEmail }
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to change email")
        }
    }

    fun grantDevAccess() {
        val current = _authState.value
        if (current is AuthState.Authenticated) {
            _authState.value = current.copy(subscriptionActive = true)
        }
    }

    suspend fun refreshUserInfo(): Resource<Unit> {
        return try {
            val user = auth.currentUserOrNull() ?: return Resource.Error("Not authenticated")
            val subscriptionActive = checkSubscriptionStatus(user.id)
            val currentState = _authState.value
            if (currentState is AuthState.Authenticated) {
                // Clear any legacy soft-paywall dismiss flags from earlier builds
                prefs.edit().remove(paywallKey(user.id)).apply()
                _authState.value = currentState.copy(
                    subscriptionActive = subscriptionActive
                )
            }
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to refresh user info")
        }
    }

    suspend fun logout() {
        // Flip UI to the sign-in screen first so logout never feels like the app closed.
        _authState.value = AuthState.Unauthenticated
        try {
            auth.signOut()
        } catch (_: Exception) {
        }
        try {
            database.clearAllTables()
        } catch (_: Exception) {
        }
    }

    private suspend fun waitForAuthenticatedState(): AuthState.Authenticated? {
        // Check if already authenticated
        val current = _authState.value
        if (current is AuthState.Authenticated) return current

        // Wait up to 15 seconds for the session to become Authenticated
        // Only filter for Authenticated — ignore Unauthenticated/Loading replays
        return withTimeoutOrNull(15_000L) {
            _authState
                .filterIsInstance<AuthState.Authenticated>()
                .first()
        }
    }
}
