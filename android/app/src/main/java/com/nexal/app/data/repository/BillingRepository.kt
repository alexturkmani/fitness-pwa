package com.nexal.app.data.repository

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.*
import com.android.billingclient.api.BillingClient.BillingResponseCode
import com.android.billingclient.api.BillingClient.ProductType
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

enum class PlanType { MONTHLY, YEARLY }

@Singleton
class BillingRepository @Inject constructor(
    @ApplicationContext private val context: Context
) : PurchasesUpdatedListener {

    companion object {
        // Must match Play Console subscription product ID
        const val PRODUCT_ID = "nexal"
    }

    private val _connectionState = MutableStateFlow(false)
    val connectionState: StateFlow<Boolean> = _connectionState.asStateFlow()

    private val _purchaseEvents = Channel<PurchaseResult>(Channel.BUFFERED)
    val purchaseEvents: Flow<PurchaseResult> = _purchaseEvents.receiveAsFlow()

    private var productDetails: ProductDetails? = null

    private val billingClient: BillingClient = BillingClient.newBuilder(context)
        .setListener(this)
        .enablePendingPurchases(
            PendingPurchasesParams.newBuilder()
                .enableOneTimeProducts()
                .build()
        )
        .enableAutoServiceReconnection()
        .build()

    init {
        startConnection()
    }

    private fun startConnection() {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(result: BillingResult) {
                if (result.responseCode == BillingResponseCode.OK) {
                    _connectionState.value = true
                }
            }

            override fun onBillingServiceDisconnected() {
                _connectionState.value = false
            }
        })
    }

    override fun onPurchasesUpdated(result: BillingResult, purchases: MutableList<Purchase>?) {
        when (result.responseCode) {
            BillingResponseCode.OK -> {
                val purchase = purchases?.firstOrNull()
                if (purchase != null) {
                    _purchaseEvents.trySend(PurchaseResult.Success(purchase))
                } else {
                    // OK but no purchase to act on — still emit so collectors can
                    // clear their loading state instead of waiting forever.
                    _purchaseEvents.trySend(PurchaseResult.Cancelled)
                }
            }
            BillingResponseCode.USER_CANCELED -> {
                _purchaseEvents.trySend(PurchaseResult.Cancelled)
            }
            else -> {
                _purchaseEvents.trySend(
                    PurchaseResult.Error(result.debugMessage ?: "Purchase failed")
                )
            }
        }
    }

    suspend fun queryProductDetails(): ProductDetails? {
        if (productDetails != null) return productDetails

        ensureConnected()
        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(
                listOf(
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(PRODUCT_ID)
                        .setProductType(ProductType.SUBS)
                        .build()
                )
            )
            .build()

        return suspendCancellableCoroutine { continuation ->
            billingClient.queryProductDetailsAsync(params) { result, productDetailsResult ->
                val detailsList = productDetailsResult.productDetailsList
                if (result.responseCode == BillingResponseCode.OK && detailsList.isNotEmpty()) {
                    productDetails = detailsList.first()
                    continuation.resume(productDetails)
                } else {
                    continuation.resume(null)
                }
            }
        }
    }

    /**
     * Localized price from Play Billing (e.g. "A$19.99"). Never hardcode USD —
     * Play policy requires currency to match the purchase flow.
     */
    fun getFormattedPrice(planType: PlanType = PlanType.MONTHLY): String? {
        val offer = getOfferForPlan(planType) ?: return null
        return offer.pricingPhases.pricingPhaseList.lastOrNull()?.formattedPrice
    }

    fun getPeriodLabel(planType: PlanType = PlanType.MONTHLY): String {
        val offer = getOfferForPlan(planType)
        val billingPeriod = offer?.pricingPhases?.pricingPhaseList?.lastOrNull()?.billingPeriod
        return when (billingPeriod) {
            "P1M" -> "/month"
            "P1Y" -> "/year"
            "P1W" -> "/week"
            else -> when (planType) {
                PlanType.MONTHLY -> "/month"
                PlanType.YEARLY -> "/year"
            }
        }
    }

    fun getPriceText(planType: PlanType = PlanType.MONTHLY): String {
        val price = getFormattedPrice(planType) ?: return "—"
        return "$price${getPeriodLabel(planType)}"
    }

    fun hasFreeTrial(planType: PlanType = PlanType.MONTHLY): Boolean {
        val details = productDetails ?: return false
        val offer = getOfferForPlan(planType) ?: return false
        return offer.pricingPhases.pricingPhaseList.any {
            it.priceAmountMicros == 0L
        }
    }

    private fun getOfferForPlan(planType: PlanType): ProductDetails.SubscriptionOfferDetails? {
        val offers = productDetails?.subscriptionOfferDetails ?: return null
        val targetPeriod = when (planType) {
            PlanType.MONTHLY -> "P1M"
            PlanType.YEARLY -> "P1Y"
        }
        val matching = offers.filter { offer ->
            offer.pricingPhases.pricingPhaseList.any { it.billingPeriod == targetPeriod }
        }
        // Prefer offers that include a free trial phase (e.g. free-trial-14d)
        return matching.firstOrNull { offer ->
            offer.pricingPhases.pricingPhaseList.any { it.priceAmountMicros == 0L }
        } ?: matching.firstOrNull() ?: offers.firstOrNull()
    }

    fun launchPurchaseFlow(activity: Activity, planType: PlanType = PlanType.MONTHLY): Boolean {
        val details = productDetails ?: return false
        val offer = getOfferForPlan(planType) ?: return false

        val productDetailsParams = BillingFlowParams.ProductDetailsParams.newBuilder()
            .setProductDetails(details)
            .setOfferToken(offer.offerToken)
            .build()

        val flowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(listOf(productDetailsParams))
            .build()

        val result = billingClient.launchBillingFlow(activity, flowParams)
        return result.responseCode == BillingResponseCode.OK
    }

    suspend fun acknowledgePurchase(purchaseToken: String): Boolean {
        val params = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchaseToken)
            .build()

        return suspendCancellableCoroutine { continuation ->
            billingClient.acknowledgePurchase(params) { result ->
                continuation.resume(result.responseCode == BillingResponseCode.OK)
            }
        }
    }

    suspend fun queryCurrentPurchases(): List<Purchase> {
        ensureConnected()
        val params = QueryPurchasesParams.newBuilder()
            .setProductType(ProductType.SUBS)
            .build()

        return suspendCancellableCoroutine { continuation ->
            billingClient.queryPurchasesAsync(params) { result, purchases ->
                if (result.responseCode == BillingResponseCode.OK) {
                    continuation.resume(purchases)
                } else {
                    continuation.resume(emptyList())
                }
            }
        }
    }

    private suspend fun ensureConnected() {
        if (_connectionState.value) return
        suspendCancellableCoroutine { continuation ->
            billingClient.startConnection(object : BillingClientStateListener {
                override fun onBillingSetupFinished(result: BillingResult) {
                    _connectionState.value = result.responseCode == BillingResponseCode.OK
                    if (continuation.isActive) continuation.resume(Unit)
                }
                override fun onBillingServiceDisconnected() {
                    _connectionState.value = false
                    if (continuation.isActive) continuation.resume(Unit)
                }
            })
        }
    }
}

sealed class PurchaseResult {
    data class Success(val purchase: Purchase) : PurchaseResult()
    data object Cancelled : PurchaseResult()
    data class Error(val message: String) : PurchaseResult()
}
