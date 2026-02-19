package com.fitmate.app.ui.subscription

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fitmate.app.data.repository.AuthRepository
import com.fitmate.app.domain.model.AuthState
import com.fitmate.app.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import javax.inject.Inject

data class SubscriptionUiState(
    val isActive: Boolean = false,
    val isTrial: Boolean = false,
    val hasUsedTrial: Boolean = false,
    val trialDaysLeft: Int = 0,
    val expirationDate: String = "",
    val error: String? = null,
    val isLoading: Boolean = false,
    val trialStarted: Boolean = false
)

@HiltViewModel
class SubscriptionViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SubscriptionUiState())
    val uiState: StateFlow<SubscriptionUiState> = _uiState.asStateFlow()

    private val _events = Channel<SubscriptionEvent>(Channel.BUFFERED)
    val events: Flow<SubscriptionEvent> = _events.receiveAsFlow()

    init {
        viewModelScope.launch {
            authRepository.authState.collect { state ->
                if (state is AuthState.Authenticated) {
                    updateFromAuthState(state)
                }
            }
        }
        viewModelScope.launch {
            authRepository.refreshUserInfo()
        }
    }

    private fun updateFromAuthState(state: AuthState.Authenticated) {
        val now = Instant.now()
        val trialEnd = state.trialEndsAt?.let {
            try { Instant.parse(it) } catch (_: Exception) { null }
        }
        val isTrialActive = trialEnd != null && trialEnd.isAfter(now)
        val daysLeft = if (trialEnd != null && trialEnd.isAfter(now)) {
            ChronoUnit.DAYS.between(now, trialEnd).toInt() + 1
        } else 0

        val expirationFormatted = trialEnd?.let {
            DateTimeFormatter.ofPattern("MMM d, yyyy")
                .withZone(ZoneId.systemDefault())
                .format(it)
        } ?: ""

        _uiState.update {
            it.copy(
                isActive = state.hasAccess,
                isTrial = isTrialActive,
                hasUsedTrial = state.hasUsedTrial,
                trialDaysLeft = daysLeft,
                expirationDate = expirationFormatted
            )
        }
    }

    fun startTrial() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = authRepository.startTrial()) {
                is Resource.Success -> {
                    _uiState.update { it.copy(isLoading = false, trialStarted = true) }
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(isLoading = false, error = result.message) }
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun purchase() {
        // Placeholder for RevenueCat integration when API key is configured
        startTrial()
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun manageSubscription() {
        viewModelScope.launch {
            _events.send(
                SubscriptionEvent.OpenUrl(
                    "https://play.google.com/store/account/subscriptions"
                )
            )
        }
    }
}

sealed class SubscriptionEvent {
    data class OpenUrl(val url: String) : SubscriptionEvent()
}
