package com.nexal.app.ui.subscription

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexal.app.BuildConfig
import com.nexal.app.data.repository.AuthRepository
import com.nexal.app.data.repository.BillingRepository
import com.nexal.app.data.repository.PlanType
import com.nexal.app.data.repository.PurchaseResult
import com.nexal.app.domain.model.AuthState
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.put
import javax.inject.Inject

data class SubscriptionUiState(
    val isActive: Boolean = false,
    val hasFreeTrial: Boolean = false,
    val error: String? = null,
    val isLoading: Boolean = false,
    val purchaseCompleted: Boolean = false,
    val selectedPlan: PlanType = PlanType.MONTHLY,
    val monthlyPrice: String = "—",
    val yearlyPrice: String = "—",
    val monthlyPeriod: String = "/month",
    val yearlyPeriod: String = "/year",
    val monthlyPriceText: String = "—",
    val yearlyPriceText: String = "—",
    val pricesLoaded: Boolean = false,
    val availablePlans: Set<PlanType> = emptySet()
)

@HiltViewModel
class SubscriptionViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val billingRepository: BillingRepository,
    private val supabase: SupabaseClient
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
            // If Play already has an active sub but our DB doesn't (verify failed
            // earlier, or first launch after purchase), sync it automatically so
            // Premium features unlock after relaunch.
            val state = authRepository.authState.value
            if (state is AuthState.Authenticated && !state.subscriptionActive) {
                restorePurchases(silent = true)
            }
        }
        // Fetch localized price and trial info from Play Billing (never hardcode currency)
        viewModelScope.launch {
            billingRepository.queryProductDetails()
            refreshPrices()
        }
        // Collect purchase results
        viewModelScope.launch {
            billingRepository.purchaseEvents.collect { result ->
                when (result) {
                    is PurchaseResult.Success -> {
                        verifyAndAcknowledge(result.purchase)
                    }
                    is PurchaseResult.Pending -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = "Your payment is pending in Google Play. Premium will unlock automatically when it completes."
                            )
                        }
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

    private fun verifyAndAcknowledge(purchase: com.android.billingclient.api.Purchase) {
        viewModelScope.launch {
            try {
                val purchaseToken = purchase.purchaseToken
                // Must send the signed-in user JWT so the server can attach the
                // Play purchase to this account. Gateway JWT verify is off for
                // this function (ES256 user tokens); the function still calls getUser().
                val accessToken = supabase.auth.currentSessionOrNull()?.accessToken
                    ?: throw IllegalStateException("Not signed in")
                val url = BuildConfig.SUPABASE_URL.trimEnd('/') + "/functions/v1/verify-purchase"
                val response = supabase.httpClient.post(url) {
                    header("apikey", BuildConfig.SUPABASE_ANON_KEY)
                    header(HttpHeaders.Authorization, "Bearer $accessToken")
                    contentType(ContentType.Application.Json)
                    setBody(buildJsonObject {
                        put("purchaseToken", purchaseToken)
                        put("productId", BillingRepository.PRODUCT_ID)
                        put("packageName", "com.nexal.app")
                    })
                }
                val responseBody = response.bodyAsText()
                if (!response.status.isSuccess()) {
                    throw IllegalStateException(friendlyVerifyError(responseBody, response.status.value))
                }
                val subscriptionActive = runCatching {
                    Json.parseToJsonElement(responseBody).jsonObject["subscriptionActive"]
                        ?.jsonPrimitive?.booleanOrNull
                }.getOrNull() == true
                if (!subscriptionActive) {
                    throw IllegalStateException("Google Play does not report this subscription as active yet. Tap Restore purchases after payment completes.")
                }
                if (!purchase.isAcknowledged && !billingRepository.acknowledgePurchase(purchaseToken)) {
                    throw IllegalStateException("Purchase verified, but Google Play acknowledgement failed. Tap Restore purchases.")
                }
                authRepository.refreshUserInfo()
                val entitlementActive = (authRepository.authState.value as? AuthState.Authenticated)
                    ?.subscriptionActive == true
                if (!entitlementActive) {
                    throw IllegalStateException("Purchase verified, but Premium is still syncing. Tap Restore purchases.")
                }
                _uiState.update { it.copy(isLoading = false, purchaseCompleted = true, isActive = true, error = null) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message?.takeIf { msg -> msg.isNotBlank() }
                            ?: "Couldn't verify your purchase. Tap Restore purchases."
                    )
                }
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
                refreshPrices()
                if (!_uiState.value.pricesLoaded) {
                    _uiState.update {
                        it.copy(isLoading = false, error = "Prices unavailable. Check your Play Store connection and try again.")
                    }
                    return@launch
                }
                // If the flow never launches, onPurchasesUpdated is never called and
                // nothing would clear isLoading — the button would spin forever.
                val launched = billingRepository.launchPurchaseFlow(activity, _uiState.value.selectedPlan)
                if (!launched) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = "Couldn't open Google Play checkout. Please try again."
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }

    fun restorePurchases(silent: Boolean = false) {
        viewModelScope.launch {
            if (!silent) {
                _uiState.update { it.copy(isLoading = true, error = null) }
            }
            try {
                val purchases = billingRepository.queryCurrentPurchases()
                val active = purchases.firstOrNull {
                    it.purchaseState == com.android.billingclient.api.Purchase.PurchaseState.PURCHASED
                }
                if (active == null) {
                    if (!silent) {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = "No active subscription found for this Google account."
                            )
                        }
                    }
                    return@launch
                }
                verifyAndAcknowledge(active)
            } catch (e: Exception) {
                if (!silent) {
                    _uiState.update {
                        it.copy(isLoading = false, error = e.message ?: "Could not restore purchases.")
                    }
                }
            }
        }
    }

    fun skipForDev() {
        authRepository.grantDevAccess()
    }

    private fun refreshPrices() {
        val monthly = billingRepository.getFormattedPrice(PlanType.MONTHLY)
        val yearly = billingRepository.getFormattedPrice(PlanType.YEARLY)
        val availablePlans = PlanType.entries.filterTo(mutableSetOf()) {
            billingRepository.isPlanAvailable(it)
        }
        _uiState.update {
            val selectedPlan = if (it.selectedPlan in availablePlans) {
                it.selectedPlan
            } else {
                availablePlans.firstOrNull() ?: it.selectedPlan
            }
            it.copy(
                monthlyPrice = monthly ?: "—",
                yearlyPrice = yearly ?: "—",
                monthlyPeriod = billingRepository.getPeriodLabel(PlanType.MONTHLY),
                yearlyPeriod = billingRepository.getPeriodLabel(PlanType.YEARLY),
                monthlyPriceText = billingRepository.getPriceText(PlanType.MONTHLY),
                yearlyPriceText = billingRepository.getPriceText(PlanType.YEARLY),
                pricesLoaded = availablePlans.isNotEmpty(),
                availablePlans = availablePlans,
                selectedPlan = selectedPlan,
                hasFreeTrial = billingRepository.hasFreeTrial(selectedPlan)
            )
        }
    }

    private fun friendlyVerifyError(rawBody: String, statusCode: Int): String {
        val body = rawBody.trim()
        if (body.isEmpty()) return "Purchase verification failed ($statusCode). Tap Restore purchases."
        // Prefer {"error":"..."} message from the edge function
        val errorMatch = Regex("\"error\"\\s*:\\s*\"((?:\\\\.|[^\"\\\\])*)\"").find(body)
        val extracted = errorMatch?.groupValues?.getOrNull(1)
            ?.replace("\\\"", "\"")
            ?.replace("\\\\", "\\")
        val message = extracted?.takeIf { it.isNotBlank() } ?: body
        return when {
            "not valid JSON" in message || "GOOGLE_SERVICE_ACCOUNT_JSON" in message ->
                "Subscription server isn't configured yet. Please try Restore purchases in a minute."
            "Unauthorized" in message ->
                "Please sign in again, then tap Restore purchases."
            message.length > 180 ->
                "Couldn't verify your purchase. Tap Restore purchases."
            else -> message
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
