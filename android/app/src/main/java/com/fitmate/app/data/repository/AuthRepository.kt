package com.fitmate.app.data.repository

import android.content.SharedPreferences
import com.fitmate.app.data.remote.api.AuthApi
import com.fitmate.app.data.remote.dto.*
import com.fitmate.app.domain.model.AuthState
import com.fitmate.app.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val authApi: AuthApi,
    @Named("tokenStore") private val prefs: SharedPreferences
) {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    init {
        // Try to restore session from stored token
        restoreSession()
    }

    private fun restoreSession() {
        val token = prefs.getString("auth_token", null)
        val userId = prefs.getString("user_id", null)
        val email = prefs.getString("user_email", null)
        val name = prefs.getString("user_name", null)
        val trialEndsAt = prefs.getString("trial_ends_at", null)
        val subscriptionActive = prefs.getBoolean("subscription_active", false)
        val isFreeAccount = prefs.getBoolean("is_free_account", false)
        val hasUsedTrial = prefs.getBoolean("has_used_trial", false)

        if (token != null && userId != null && email != null) {
            _authState.value = AuthState.Authenticated(
                userId = userId,
                email = email,
                name = name,
                token = token,
                trialEndsAt = trialEndsAt,
                subscriptionActive = subscriptionActive,
                isFreeAccount = isFreeAccount,
                hasUsedTrial = hasUsedTrial
            )
        } else {
            _authState.value = AuthState.Unauthenticated
        }
    }

    suspend fun login(email: String, password: String): Resource<AuthState.Authenticated> {
        return try {
            val response = authApi.login(LoginRequestDto(email, password))
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                val state = saveSession(body)
                Resource.Success(state)
            } else {
                val errorMsg = when (response.code()) {
                    401 -> "Invalid email or password"
                    403 -> "Account not verified. Please check your email."
                    else -> "Login failed. Please try again."
                }
                Resource.Error(errorMsg, response.code())
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error. Please check your connection.")
        }
    }

    suspend fun register(name: String, email: String, password: String): Resource<RegisterResponseDto> {
        return try {
            val response = authApi.register(RegisterRequestDto(name, email, password))
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                val errorMsg = when (response.code()) {
                    409 -> "An account with this email already exists"
                    else -> "Registration failed. Please try again."
                }
                Resource.Error(errorMsg, response.code())
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error. Please check your connection.")
        }
    }

    suspend fun googleSignIn(idToken: String): Resource<AuthState.Authenticated> {
        return try {
            val response = authApi.googleSignIn(GoogleSignInRequestDto(idToken))
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                val state = saveSession(body)
                Resource.Success(state)
            } else {
                Resource.Error("Google sign-in failed. Please try again.", response.code())
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error. Please check your connection.")
        }
    }

    suspend fun forgotPassword(email: String): Resource<Unit> {
        return try {
            val response = authApi.forgotPassword(ForgotPasswordRequestDto(email))
            if (response.isSuccessful) Resource.Success(Unit)
            else Resource.Error("Failed to send reset email.")
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error")
        }
    }

    suspend fun resetPassword(token: String, newPassword: String): Resource<Unit> {
        return try {
            val response = authApi.resetPassword(ResetPasswordRequestDto(token, newPassword))
            if (response.isSuccessful) Resource.Success(Unit)
            else Resource.Error("Failed to reset password. Link may have expired.")
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error")
        }
    }

    suspend fun changePassword(currentPassword: String, newPassword: String): Resource<Unit> {
        return try {
            val response = authApi.changePassword(
                ChangePasswordRequestDto(currentPassword, newPassword)
            )
            if (response.isSuccessful) Resource.Success(Unit)
            else Resource.Error("Failed to change password. Check your current password.")
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error")
        }
    }

    suspend fun changeEmail(newEmail: String): Resource<Unit> {
        return try {
            val response = authApi.changeEmail(ChangeEmailRequestDto(newEmail))
            if (response.isSuccessful) Resource.Success(Unit)
            else Resource.Error("Failed to change email.")
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error")
        }
    }

    /** Refresh user data (subscription status, etc.) from server */
    suspend fun refreshUserInfo(): Resource<Unit> {
        return try {
            val response = authApi.getMe()
            if (response.isSuccessful && response.body() != null) {
                val user = response.body()!!
                val currentState = _authState.value
                if (currentState is AuthState.Authenticated) {
                    val updated = currentState.copy(
                        name = user.name,
                        email = user.email,
                        trialEndsAt = user.trialEndsAt,
                        subscriptionActive = user.subscriptionActive,
                        isFreeAccount = user.isFreeAccount,
                        hasUsedTrial = user.hasUsedTrial
                    )
                    _authState.value = updated
                    prefs.edit()
                        .putString("user_name", user.name)
                        .putString("user_email", user.email)
                        .putString("trial_ends_at", user.trialEndsAt)
                        .putBoolean("subscription_active", user.subscriptionActive)
                        .putBoolean("is_free_account", user.isFreeAccount)
                        .putBoolean("has_used_trial", user.hasUsedTrial)
                        .apply()
                }
                Resource.Success(Unit)
            } else {
                Resource.Error("Failed to refresh user info")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error")
        }
    }

    fun logout() {
        prefs.edit().clear().apply()
        _authState.value = AuthState.Unauthenticated
    }

    suspend fun startTrial(): Resource<String> {
        return try {
            val response = authApi.startTrial()
            if (response.isSuccessful && response.body() != null) {
                val trialEndsAt = response.body()!!.trialEndsAt
                // Refresh user info to update auth state
                refreshUserInfo()
                Resource.Success(trialEndsAt ?: "")
            } else {
                val code = response.code()
                val msg = if (code == 400) "Trial already used" else "Failed to start trial"
                Resource.Error(msg, code)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error")
        }
    }

    private fun saveSession(loginResponse: LoginResponseDto): AuthState.Authenticated {
        val user = loginResponse.user
        val state = AuthState.Authenticated(
            userId = user.id,
            email = user.email,
            name = user.name,
            token = loginResponse.token,
            trialEndsAt = user.trialEndsAt,
            subscriptionActive = user.subscriptionActive,
            isFreeAccount = user.isFreeAccount,
            hasUsedTrial = user.hasUsedTrial
        )
        prefs.edit()
            .putString("auth_token", loginResponse.token)
            .putString("user_id", user.id)
            .putString("user_email", user.email)
            .putString("user_name", user.name)
            .putString("trial_ends_at", user.trialEndsAt)
            .putBoolean("subscription_active", user.subscriptionActive)
            .putBoolean("is_free_account", user.isFreeAccount)
            .putBoolean("has_used_trial", user.hasUsedTrial)
            .apply()
        _authState.value = state
        return state
    }
}
