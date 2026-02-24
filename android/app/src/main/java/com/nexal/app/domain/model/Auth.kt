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
        val token: String,
        val trialEndsAt: String?,
        val subscriptionActive: Boolean,
        val isFreeAccount: Boolean,
        val hasUsedTrial: Boolean
    ) : AuthState() {
        val hasAccess: Boolean
            get() {
                if (isFreeAccount) return true
                if (subscriptionActive) return true
                val trialEnd = trialEndsAt ?: return false
                return try {
                    java.time.Instant.parse(trialEnd).isAfter(java.time.Instant.now())
                } catch (_: Exception) {
                    false
                }
            }
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
