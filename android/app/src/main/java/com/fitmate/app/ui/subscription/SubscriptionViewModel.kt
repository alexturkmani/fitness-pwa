package com.fitmate.app.ui.subscription

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fitmate.app.data.repository.AuthRepository
import com.fitmate.app.domain.model.AuthState
import com.fitmate.app.util.Resource
import com.revenuecat.purchases.CustomerInfo
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.PurchaseParams
import com.revenuecat.purchases.models.StoreTransaction
import com.revenuecat.purchases.interfaces.PurchaseCallback
import com.revenuecat.purchases.PurchasesError
import com.revenuecat.purchases.getOfferingsWith
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
    val trialStarted: Boolean = false,
    val priceText: String = "\$4.99/month"
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
                    // Identify user to RevenueCat
                    try {
                        Purchases.sharedInstance.logIn(state.userId)
                    } catch (_: Exception) { /* RevenueCat not configured */ }
                }
            }
        }
        viewModelScope.launch {
            authRepository.refreshUserInfo()
        }
        // Fetch price from RevenueCat offerings
        fetchPrice()
    }

    private fun fetchPrice() {
        try {
            Purchases.sharedInstance.getOfferingsWith(
                onError = { /* Use default price text */ },
                onSuccess = { offerings ->
                    val pkg = offerings.current?.availablePackages?.firstOrNull()
                    if (pkg != null) {
                        val price = pkg.product.price.formatted
                        val period = pkg.product.period?.let { p ->
                            when (p.unit) {
                                com.revenuecat.purchases.models.Period.Unit.MONTH -> if (p.value == 1) "/month" else "/${p.value} months"
                                com.revenuecat.purchases.models.Period.Unit.YEAR -> if (p.value == 1) "/year" else "/${p.value} years"
                                com.revenuecat.purchases.models.Period.Unit.WEEK -> if (p.value == 1) "/week" else "/${p.value} weeks"
                                else -> ""
                            }
                        } ?: "/month"
                        _uiState.update { it.copy(priceText = "$price$period") }
                    }
                }
            )
        } catch (_: Exception) { /* RevenueCat not configured */ }
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

    fun purchase(activity: Activity) {
        _uiState.update { it.copy(isLoading = true, error = null) }
        try {
            Purchases.sharedInstance.getOfferingsWith(
                onError = { error ->
                    _uiState.update { it.copy(isLoading = false, error = error.message) }
                },
                onSuccess = { offerings ->
                    val pkg = offerings.current?.availablePackages?.firstOrNull()
                    if (pkg == null) {
                        _uiState.update { it.copy(isLoading = false, error = "No subscription packages available") }
                        return@getOfferingsWith
                    }

                    Purchases.sharedInstance.purchase(
                        PurchaseParams.Builder(activity, pkg).build(),
                        object : PurchaseCallback {
                            override fun onCompleted(storeTransaction: StoreTransaction, customerInfo: CustomerInfo) {
                                // Purchase successful — refresh user info from server
                                // (RevenueCat webhook will update the server-side subscription status)
                                viewModelScope.launch {
                                    authRepository.refreshUserInfo()
                                    _uiState.update { it.copy(isLoading = false, trialStarted = true) }
                                }
                            }

                            override fun onError(error: PurchasesError, userCancelled: Boolean) {
                                if (userCancelled) {
                                    _uiState.update { it.copy(isLoading = false) }
                                } else {
                                    _uiState.update { it.copy(isLoading = false, error = error.message) }
                                }
                            }
                        }
                    )
                }
            )
        } catch (e: Exception) {
            _uiState.update { it.copy(isLoading = false, error = "Subscription service not available. Please try again later.") }
        }
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
