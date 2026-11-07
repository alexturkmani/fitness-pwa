package com.nexal.app.ui.paywall

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nexal.app.BuildConfig
import com.nexal.app.R
import com.nexal.app.data.repository.PlanType
import com.nexal.app.ui.components.*
import com.nexal.app.ui.subscription.SubscriptionUiState
import com.nexal.app.ui.subscription.SubscriptionViewModel
import com.nexal.app.ui.theme.*
import kotlinx.coroutines.delay

// LocalContext is not always the Activity itself — it can be a themed wrapper,
// where a direct cast would throw.
private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

@Composable
fun PaywallScreen(
    onBack: () -> Unit,
    onSubscribed: () -> Unit,
    afterSetup: Boolean = false,
    viewModel: SubscriptionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var automaticRetryCount by remember { mutableIntStateOf(0) }

    LaunchedEffect(uiState.pricingChecked, uiState.pricesLoaded) {
        if (uiState.pricingChecked && !uiState.pricesLoaded && automaticRetryCount < 2) {
            delay(1_500)
            automaticRetryCount++
            viewModel.retryPricing()
        }
    }

    LaunchedEffect(uiState.isActive, uiState.purchaseCompleted) {
        if (uiState.isActive || uiState.purchaseCompleted) onSubscribed()
    }

    BackHandler(onBack = onBack)

    PaywallContent(
        state = uiState,
        afterSetup = afterSetup,
        devPricingFallback = BuildConfig.DEBUG && uiState.pricingChecked && !uiState.pricesLoaded,
        onClose = onBack,
        onSelectPlan = viewModel::selectPlan,
        onPurchase = { context.findActivity()?.let { viewModel.purchase(it) } },
        onRestore = { viewModel.restorePurchases() },
        onDevSkip = { viewModel.skipForDev() }
    )
}

/** The paywall sits on a photo in both themes, so it always uses the dark glass colours. */
@Composable
internal fun PaywallContent(
    state: SubscriptionUiState,
    afterSetup: Boolean,
    devPricingFallback: Boolean,
    onClose: () -> Unit,
    onSelectPlan: (PlanType) -> Unit,
    onPurchase: () -> Unit,
    onRestore: () -> Unit,
    onDevSkip: () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = MaterialTheme.typography,
        shapes = MaterialTheme.shapes
    ) {
        PhotoBackdrop(photo = R.drawable.bg_strength) {
            Column(Modifier.fillMaxSize().statusBarsPadding()) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp)
                        .widthIn(max = 520.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Spacer(Modifier.height(4.dp))
                    GlassIconButton(Icons.Default.Close, "Close", onClose)
                    PaywallHeader(state, afterSetup)
                    FeatureList()
                    PlanOptions(state, onSelectPlan)
                    if (state.hasFreeTrial && state.pricesLoaded) TrialTimeline(state)
                    PaywallLinks(onRestore, onClose, onDevSkip, state.isLoading)
                    state.error?.let { error ->
                        Text(
                            error,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                }
                PurchaseBar(state, devPricingFallback, onPurchase, onDevSkip)
            }
        }
    }
}

@Composable
private fun PaywallHeader(state: SubscriptionUiState, afterSetup: Boolean) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("NEXAL PREMIUM", style = MaterialTheme.typography.labelSmall, color = GlassTextMuted)
        Text(
            if (afterSetup) setupHeadline(state.setupDays, state.setupGoal) else "Get a plan built around you",
            style = MaterialTheme.typography.headlineLarge,
            color = GlassText
        )
        Text(
            if (afterSetup) {
                "Your goal and schedule are saved. Start Premium to create your plan, or keep tracking for free."
            } else {
                "Keep tracking for free, or unlock AI plans and barcode scanning."
            },
            style = MaterialTheme.typography.bodyMedium,
            color = GlassTextMuted
        )
    }
}

@Composable
private fun FeatureList() {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            FeatureRow(Icons.Default.FitnessCenter, "AI workout plans", "Gym or home, built around your goal and schedule")
            FeatureRow(Icons.Default.Restaurant, "AI meal plans", "Daily meals built to your calories, macros and allergies")
            FeatureRow(Icons.Default.QrCodeScanner, "Barcode scanner", "Scan packaged food to check its nutrition")
            FeatureRow(Icons.Default.SwapHoriz, "Smart substitutions", "Swap a meal and stay close to your macros")
        }
    }
}

@Composable
private fun FeatureRow(icon: ImageVector, title: String, detail: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
        IconBadge(icon, tint = GlassText)
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = GlassText)
            Text(detail, style = MaterialTheme.typography.bodySmall, color = GlassTextMuted)
        }
    }
}

@Composable
private fun PlanOptions(state: SubscriptionUiState, onSelectPlan: (PlanType) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        if (PlanType.YEARLY in state.availablePlans) {
            PlanRow(
                label = "Yearly",
                price = state.yearlyPrice,
                period = state.yearlyPeriod,
                note = state.yearlyPerMonth?.let { "$it per month" },
                badge = state.yearlySavingsPercent?.let { "Save $it%" },
                selected = state.selectedPlan == PlanType.YEARLY,
                onClick = { onSelectPlan(PlanType.YEARLY) }
            )
        }
        if (PlanType.MONTHLY in state.availablePlans) {
            PlanRow(
                label = "Monthly",
                price = state.monthlyPrice,
                period = state.monthlyPeriod,
                note = null,
                badge = null,
                selected = state.selectedPlan == PlanType.MONTHLY,
                onClick = { onSelectPlan(PlanType.MONTHLY) }
            )
        }
    }
}

@Composable
private fun PlanRow(
    label: String,
    price: String,
    period: String,
    note: String?,
    badge: String?,
    selected: Boolean,
    onClick: () -> Unit
) {
    val lime = limeEdge()
    val outline = if (selected) Modifier.border(BorderStroke(2.dp, lime), RoundedCornerShape(24.dp)) else Modifier
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .then(outline)
            .semantics {
                this.selected = selected
                role = Role.RadioButton
            },
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(label, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = GlassText)
                    if (badge != null) {
                        Text(
                            badge,
                            style = MaterialTheme.typography.labelSmall,
                            color = GlassText,
                            modifier = Modifier
                                .border(BorderStroke(1.dp, lime), CircleShape)
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }
                if (note != null) {
                    Text(note, style = MaterialTheme.typography.bodySmall, color = GlassTextMuted)
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(price, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = GlassText)
                Text(period, style = MaterialTheme.typography.bodySmall, color = GlassTextMuted)
            }
        }
    }
}

@Composable
private fun TrialTimeline(state: SubscriptionUiState) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text("How your free trial works", style = MaterialTheme.typography.titleMedium, color = GlassText)
            FeatureRow(Icons.Default.LockOpen, "Today", "Full Premium access, nothing to pay")
            FeatureRow(Icons.Default.CreditCard, "After 14 days", "${selectedPriceText(state)} starts unless you cancel first")
            FeatureRow(Icons.Default.Close, "Anytime", "Cancel in Google Play")
        }
    }
}

@Composable
private fun PaywallLinks(onRestore: () -> Unit, onClose: () -> Unit, onDevSkip: () -> Unit, loading: Boolean) {
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        TextButton(onClick = onClose) {
            Text("Continue with free tracking", color = GlassText, fontWeight = FontWeight.SemiBold)
        }
        TextButton(onClick = onRestore, enabled = !loading) {
            Text("Restore purchases", color = GlassTextMuted)
        }
        if (BuildConfig.DEBUG) {
            TextButton(onClick = onDevSkip) {
                Text("Skip (Dev Testing)", color = GlassTextMuted, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
private fun PurchaseBar(
    state: SubscriptionUiState,
    devPricingFallback: Boolean,
    onPurchase: () -> Unit,
    onDevSkip: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Brush.verticalGradient(listOf(Color.Transparent, Slate950, Slate950)))
            .navigationBarsPadding()
            .padding(start = 20.dp, end = 20.dp, top = 18.dp, bottom = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        GradientButton(
            text = when {
                devPricingFallback -> "Continue with Dev Premium"
                !state.pricesLoaded -> "Connecting to Google Play..."
                state.hasFreeTrial -> "Start 14-Day Free Trial"
                else -> "Subscribe to Nexal Premium"
            },
            onClick = { if (devPricingFallback) onDevSkip() else onPurchase() },
            modifier = Modifier.fillMaxWidth(),
            loading = state.isLoading,
            enabled = (state.pricesLoaded || devPricingFallback) && !state.isLoading
        )
        Text(
            when {
                devPricingFallback -> "Testing access only. No payment will be made."
                !state.pricesLoaded && state.pricingChecked -> "Could not load Google Play pricing."
                !state.pricesLoaded -> "Secure billing through Google Play"
                state.hasFreeTrial -> "14 days free, then ${selectedPriceText(state)}. Cancel anytime in Google Play before the trial ends to avoid being charged."
                else -> "${selectedPriceText(state)}. Cancel anytime in Google Play."
            },
            style = MaterialTheme.typography.labelSmall,
            color = GlassTextMuted,
            textAlign = TextAlign.Center
        )
    }
}

private fun selectedPriceText(state: SubscriptionUiState): String =
    if (state.selectedPlan == PlanType.MONTHLY) state.monthlyPriceText else state.yearlyPriceText
