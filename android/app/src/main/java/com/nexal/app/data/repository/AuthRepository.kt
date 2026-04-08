package com.nexal.app.data.repository

import com.nexal.app.data.local.NexalDatabase
import com.nexal.app.domain.model.AuthState
import com.nexal.app.util.Resource
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
import javax.inject.Inject
import javax.inject.Singleton

@Serializable
data class SubscriptionRow(
    val status: String = "inactive"
)

@Singleton
class AuthRepository @Inject constructor(
    private val auth: Auth,
    private val postgrest: Postgrest,
    private val database: NexalDatabase
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

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
                        _authState.value = AuthState.Loading
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
            result?.status == "active"
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
            auth.signUpWith(Email) {
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

    suspend fun googleSignIn(idToken: String): Resource<AuthState.Authenticated> {
        return try {
            auth.signInWith(IDToken) {
                this.provider = Google
                this.idToken = idToken
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
            auth.resetPasswordForEmail(email)
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

    suspend fun refreshUserInfo(): Resource<Unit> {
        return try {
            val user = auth.currentUserOrNull() ?: return Resource.Error("Not authenticated")
            val subscriptionActive = checkSubscriptionStatus(user.id)
            val currentState = _authState.value
            if (currentState is AuthState.Authenticated) {
                _authState.value = currentState.copy(subscriptionActive = subscriptionActive)
            }
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to refresh user info")
        }
    }

    suspend fun logout() {
        try { auth.signOut() } catch (_: Exception) {}
        database.clearAllTables()
        _authState.value = AuthState.Unauthenticated
    }

    private suspend fun waitForAuthenticatedState(): AuthState.Authenticated? {
        // Check if already authenticated
        val current = _authState.value
        if (current is AuthState.Authenticated) return current

        // Wait up to 10 seconds for the session status to transition
        return withTimeoutOrNull(10_000L) {
            _authState
                .filter { it is AuthState.Authenticated || it is AuthState.Unauthenticated }
                .map { it as? AuthState.Authenticated }
                .first()
        }
    }
}
