package com.nexal.app.ui.auth

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.NoCredentialException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexal.app.R
import com.nexal.app.data.repository.AuthRepository
import com.nexal.app.util.Resource
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.security.MessageDigest
import java.util.UUID
import javax.inject.Inject

data class AuthUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val loginSuccess: Boolean = false,
    val registerSuccess: Boolean = false,
    val registerEmail: String = "",
    val forgotPasswordSent: Boolean = false,
    val resetPasswordSuccess: Boolean = false
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.update { it.copy(error = "Please fill in all fields") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = authRepository.login(email.trim(), password)) {
                is Resource.Success -> {
                    _uiState.update { it.copy(isLoading = false, loginSuccess = true) }
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(isLoading = false, error = friendlyAuthError(result.message)) }
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun register(name: String, email: String, password: String) {
        if (name.isBlank() || email.isBlank() || password.isBlank()) {
            _uiState.update { it.copy(error = "Please fill in all fields") }
            return
        }
        if (password.length < 8) {
            _uiState.update { it.copy(error = "Password must be at least 8 characters") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = authRepository.register(name.trim(), email.trim(), password)) {
                is Resource.Success -> {
                    _uiState.update { it.copy(isLoading = false, registerSuccess = true, registerEmail = email.trim()) }
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(isLoading = false, error = friendlyAuthError(result.message)) }
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun forgotPassword(email: String) {
        if (email.isBlank()) {
            _uiState.update { it.copy(error = "Please enter your email") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = authRepository.forgotPassword(email.trim())) {
                is Resource.Success -> {
                    _uiState.update { it.copy(isLoading = false, forgotPasswordSent = true) }
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(isLoading = false, error = friendlyAuthError(result.message)) }
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun resetPassword(token: String, newPassword: String) {
        if (newPassword.length < 8) {
            _uiState.update { it.copy(error = "Password must be at least 8 characters") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            // With Supabase, the reset link signs the user in, so we just update the password
            when (val result = authRepository.changePassword(newPassword)) {
                is Resource.Success -> {
                    _uiState.update { it.copy(isLoading = false, resetPasswordSuccess = true) }
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(isLoading = false, error = friendlyAuthError(result.message)) }
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun signInWithGoogle(context: Context) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val webClientId = context.getString(R.string.default_web_client_id)
                val credentialManager = CredentialManager.create(context)
                val rawNonce = UUID.randomUUID().toString()
                val hashedNonce = sha256Hex(rawNonce)

                // Try every Credential Manager path. SHA / Play Services / One Tap
                // quirks vary by device; one of these usually succeeds.
                val idToken = obtainGoogleIdToken(
                    context = context,
                    credentialManager = credentialManager,
                    webClientId = webClientId,
                    hashedNonce = hashedNonce
                )

                // Prefer nonce verification; if the backend rejects nonce, retry
                // without one (Supabase skip-nonce is enabled as a safety net).
                val signInResult = when (val withNonce = authRepository.googleSignIn(idToken, rawNonce)) {
                    is Resource.Success -> withNonce
                    is Resource.Error -> {
                        if (looksLikeNonceError(withNonce.message)) {
                            authRepository.googleSignIn(idToken, rawNonce = "")
                        } else {
                            withNonce
                        }
                    }
                    is Resource.Loading -> withNonce
                }

                when (signInResult) {
                    is Resource.Success -> {
                        _uiState.update { it.copy(isLoading = false, loginSuccess = true) }
                    }
                    is Resource.Error -> {
                        _uiState.update { it.copy(isLoading = false, error = friendlyAuthError(signInResult.message)) }
                    }
                    is Resource.Loading -> {}
                }
            } catch (_: GetCredentialCancellationException) {
                _uiState.update { it.copy(isLoading = false) }
            } catch (_: NoCredentialException) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Couldn't open Google sign-in. Add a Google account in Settings, update Google Play Services, then try again."
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = friendlyAuthError(e.message)) }
            }
        }
    }

    private suspend fun obtainGoogleIdToken(
        context: Context,
        credentialManager: CredentialManager,
        webClientId: String,
        hashedNonce: String
    ): String {
        // 1) Explicit button / account picker (most reliable for first-time users)
        try {
            return requestGoogleSignInButtonToken(context, credentialManager, webClientId, hashedNonce)
        } catch (_: NoCredentialException) { /* fall through */ }

        // 2) One Tap / bottomsheet for any account on device
        try {
            return requestGoogleIdToken(
                context, credentialManager, webClientId, hashedNonce, filterAuthorizedOnly = false
            )
        } catch (_: NoCredentialException) { /* fall through */ }

        // 3) Authorized accounts only (returning users)
        try {
            return requestGoogleIdToken(
                context, credentialManager, webClientId, hashedNonce, filterAuthorizedOnly = true
            )
        } catch (_: NoCredentialException) { /* fall through */ }

        // 4) Last resort: button flow without nonce (some Play Services builds mishandle it)
        return requestGoogleSignInButtonToken(context, credentialManager, webClientId, hashedNonce = null)
    }

    private fun looksLikeNonceError(message: String?): Boolean {
        val m = message?.lowercase().orEmpty()
        return "nonce" in m || "invalid id token" in m || "id_token" in m
    }

    private suspend fun requestGoogleIdToken(
        context: Context,
        credentialManager: CredentialManager,
        webClientId: String,
        hashedNonce: String?,
        filterAuthorizedOnly: Boolean
    ): String {
        val builder = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(filterAuthorizedOnly)
            .setServerClientId(webClientId)
            .setAutoSelectEnabled(false)
        if (!hashedNonce.isNullOrBlank()) {
            builder.setNonce(hashedNonce)
        }
        val request = GetCredentialRequest.Builder()
            .addCredentialOption(builder.build())
            .build()
        val result = credentialManager.getCredential(context, request)
        return GoogleIdTokenCredential.createFrom(result.credential.data).idToken
    }

    private suspend fun requestGoogleSignInButtonToken(
        context: Context,
        credentialManager: CredentialManager,
        webClientId: String,
        hashedNonce: String?
    ): String {
        val builder = GetSignInWithGoogleOption.Builder(webClientId)
        if (!hashedNonce.isNullOrBlank()) {
            builder.setNonce(hashedNonce)
        }
        val request = GetCredentialRequest.Builder()
            .addCredentialOption(builder.build())
            .build()
        val result = credentialManager.getCredential(context, request)
        return GoogleIdTokenCredential.createFrom(result.credential.data).idToken
    }

    private fun sha256Hex(value: String): String {
        val digest = MessageDigest.getInstance("SHA-256").digest(value.toByteArray())
        return digest.joinToString("") { "%02x".format(it) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    private fun friendlyAuthError(raw: String?): String {
        val message = raw?.trim().orEmpty()
        if (message.isEmpty()) return "Something went wrong. Please try again."
        val lower = message.lowercase()
        return when {
            "unable to resolve host" in lower ||
                "no address associated with hostname" in lower ||
                "failed to connect" in lower ||
                "unknownhost" in lower ||
                "network" in lower && "unreachable" in lower ->
                "Can't reach Nexal servers right now. Check your connection and try again."
            "invalid login credentials" in lower || "invalid_credentials" in lower ->
                "Incorrect email or password."
            "email not confirmed" in lower ->
                "Please confirm your email before signing in."
            message.length > 160 || "http" in lower || "supabase" in lower ->
                "Sign-in failed. Please try again."
            else -> message
        }
    }
}
