package com.nexal.app.domain.model

/**
 * Represents the user's authentication and subscription state.
 */
sealed class AuthState {
    data object Loading : AuthState()
    data object Unauthenticated : AuthState()
    data class Authenticated(
        val userId: String,
        val email: String,
        val name: String?,
        val subscriptionActive: Boolean
    ) : AuthState() {
        /** Premium controls paid features, not access to the core app. */
        val isPremium: Boolean get() = subscriptionActive
    }
}

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String
)
