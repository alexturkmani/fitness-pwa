package com.nexal.app.ui.subscription

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexal.app.data.repository.AuthRepository
import com.nexal.app.data.repository.BillingRepository
import com.nexal.app.data.repository.PlanType
import com.nexal.app.data.repository.PurchaseResult
import com.nexal.app.domain.model.AuthState
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.functions.Functions
import io.ktor.client.call.body
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import javax.inject.Inject

data class SubscriptionUiState(
    val isActive: Boolean = false,
    val hasFreeTrial: Boolean = false,
    val error: String? = null,
    val isLoading: Boolean = false,
    val purchaseCompleted: Boolean = false,
    val selectedPlan: PlanType = PlanType.MONTHLY,
    val monthlyPriceText: String = "$12.99/month",
    val yearlyPriceText: String = "$110.00/year"
)

@HiltViewModel
class SubscriptionViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val billingRepository: BillingRepository,
    private val functions: Functions
) : ViewModel() {

    private val _uiState = MutableStateFlow(SubscriptionUiState())
    val uiState: StateFlow<SubscriptionUiState> = _uiState.asStateFlow()

    private val _events = Channel<SubscriptionEvent>(Channel.BUFFERED)
    val events: Flow<SubscriptionEvent> = _events.receiveAsFlow()

    init {
        viewModelScope.launch {
            authRepository.authState.collect { state ->
                if (state is AuthState.Authenticated) {
                    _uiState.update { it.copy(isActive = state.subscriptionActive) }
                }
            }
        }
        viewModelScope.launch {
            authRepository.refreshUserInfo()
        }
        // Fetch price and trial info from Play Billing
        viewModelScope.launch {
            billingRepository.queryProductDetails()
            _uiState.update {
                it.copy(
                    monthlyPriceText = billingRepository.getPriceText(PlanType.MONTHLY),
                    yearlyPriceText = billingRepository.getPriceText(PlanType.YEARLY),
                    hasFreeTrial = billingRepository.hasFreeTrial(it.selectedPlan)
                )
            }
        }
        // Collect purchase results
        viewModelScope.launch {
            billingRepository.purchaseEvents.collect { result ->
                when (result) {
                    is PurchaseResult.Success -> {
                        verifyAndAcknowledge(result.purchase.purchaseToken)
                    }
                    is PurchaseResult.Cancelled -> {
                        _uiState.update { it.copy(isLoading = false) }
                    }
                    is PurchaseResult.Error -> {
                        _uiState.update { it.copy(isLoading = false, error = result.message) }
                    }
                }
            }
        }
    }

    private fun verifyAndAcknowledge(purchaseToken: String) {
        viewModelScope.launch {
            try {
                // Call verify-purchase Edge Function
                functions.invoke("verify-purchase", body = buildJsonObject {
                    put("purchaseToken", purchaseToken)
                    put("productId", BillingRepository.PRODUCT_ID)
                })
                // Acknowledge the purchase
                billingRepository.acknowledgePurchase(purchaseToken)
                // Refresh auth state
                authRepository.refreshUserInfo()
                _uiState.update { it.copy(isLoading = false, purchaseCompleted = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = "Verification failed: ${e.message}") }
            }
        }
    }

    fun selectPlan(planType: PlanType) {
        _uiState.update {
            it.copy(
                selectedPlan = planType,
                hasFreeTrial = billingRepository.hasFreeTrial(planType)
            )
        }
    }

    fun purchase(activity: Activity) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                billingRepository.queryProductDetails()
                billingRepository.launchPurchaseFlow(activity, _uiState.value.selectedPlan)
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
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
