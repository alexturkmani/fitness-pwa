package com.nexal.app.ui.paywall

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nexal.app.ui.components.*
import com.nexal.app.data.repository.PlanType
import com.nexal.app.ui.subscription.SubscriptionViewModel
import com.nexal.app.ui.theme.*

// LocalContext is not always the Activity itself — it can be a themed wrapper,
// where a direct cast would throw.
private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaywallScreen(
    onBack: () -> Unit,
    onSubscribed: () -> Unit,
    viewModel: SubscriptionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val selectedPriceText =
        if (uiState.selectedPlan == PlanType.MONTHLY) uiState.monthlyPriceText
        else uiState.yearlyPriceText

    LaunchedEffect(uiState.isActive, uiState.purchaseCompleted) {
        if (uiState.isActive || uiState.purchaseCompleted) onSubscribed()
    }

    val metrics = rememberAdaptiveMetrics()

    BackHandler(onBack = onBack)

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            Surface(color = MaterialTheme.colorScheme.surface, shadowElevation = 14.dp, tonalElevation = 4.dp) {
                Column(
                    modifier = Modifier.navigationBarsPadding().padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    GradientButton(
                        text = when {
                            !uiState.pricesLoaded -> "Loading subscription options…"
                            uiState.hasFreeTrial -> "Start 14-Day Free Trial"
                            else -> "Subscribe to Nexal Premium"
                        },
                        onClick = { context.findActivity()?.let { viewModel.purchase(it) } },
                        modifier = Modifier.fillMaxWidth(),
                        loading = uiState.isLoading,
                        enabled = uiState.pricesLoaded && !uiState.isLoading
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(
                        if (uiState.pricesLoaded) "$selectedPriceText · Cancel anytime" else "Plans and pricing will appear here",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        },
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Dark hero band — the single high-contrast surface, matching the
            // dashboard so the paywall doesn't read as a different product.
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp)
                    .clip(RoundedCornerShape(30.dp))
                    .background(HeroInk)
                    .padding(
                        horizontal = metrics.horizontalPadding,
                        vertical = if (metrics.isCompactHeight) 22.dp else 32.dp
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .widthIn(max = 520.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ScalePopIn(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val hero = if (metrics.isCompactHeight) 64.dp else metrics.heroSize.coerceAtLeast(64.dp)
                        Box(
                            modifier = Modifier
                                .size(hero)
                                .clip(CircleShape)
                                .background(AccentBright),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.MonitorHeart,
                                contentDescription = null,
                                tint = HeroInk,
                                modifier = Modifier.size(hero / 2)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(metrics.sectionSpacing))

                    FadeSlideIn(
                        delayMs = 80,
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "NEXAL PREMIUM",
                            style = MaterialTheme.typography.labelSmall,
                            color = AccentBright,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            "Train with a plan that adapts to you",
                            style = MaterialTheme.typography.headlineLarge,
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            "Keep tracking for free, or unlock AI plans and barcode scanning.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.62f),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            FadeSlideIn(delayMs = 120) {
                Column(
                    modifier = Modifier
                        .widthIn(max = 520.dp)
                        .fillMaxWidth()
                        .padding(horizontal = metrics.horizontalPadding),
                    verticalArrangement = Arrangement.spacedBy(if (metrics.isCompactHeight) 8.dp else 12.dp)
                ) {
                    PaywallFeature(Icons.Default.FitnessCenter, "AI Workout Plans", "Personalized training tailored to your goals", Accent, metrics.useCompactOptions)
                    PaywallFeature(Icons.Default.Restaurant, "AI Meal Plans", "Macro-optimized nutrition for your preferences", Accent, metrics.useCompactOptions)
                    PaywallFeature(Icons.Default.QrCodeScanner, "Barcode Scanner", "Instant nutrition info and AI assessment", Accent, metrics.useCompactOptions)
                    if (!metrics.isCompactHeight) {
                        PaywallFeature(Icons.Default.BarChart, "Progress Analytics", "Track weight, volume, and nutrition trends", Accent, false)
                        PaywallFeature(Icons.Default.SwapHoriz, "Smart Substitutions", "AI alternatives that match your macros", Accent, false)
                    }
                }
            }

            Spacer(modifier = Modifier.height(metrics.sectionSpacing))

            FadeSlideIn(delayMs = 180) {
                Column(
                    modifier = Modifier
                        .widthIn(max = 520.dp)
                        .fillMaxWidth()
                        .padding(horizontal = metrics.horizontalPadding),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (metrics.isCompactWidth) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            PlanCard(
                                label = "Monthly",
                                price = uiState.monthlyPrice,
                                period = uiState.monthlyPeriod,
                                selected = uiState.selectedPlan == PlanType.MONTHLY,
                                badge = null,
                                onClick = { viewModel.selectPlan(PlanType.MONTHLY) },
                                modifier = Modifier.fillMaxWidth()
                            )
                            PlanCard(
                                label = "Yearly",
                                price = uiState.yearlyPrice,
                                period = uiState.yearlyPeriod,
                                selected = uiState.selectedPlan == PlanType.YEARLY,
                                badge = "Best value",
                                onClick = { viewModel.selectPlan(PlanType.YEARLY) },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            PlanCard(
                                label = "Monthly",
                                price = uiState.monthlyPrice,
                                period = uiState.monthlyPeriod,
                                selected = uiState.selectedPlan == PlanType.MONTHLY,
                                badge = null,
                                onClick = { viewModel.selectPlan(PlanType.MONTHLY) },
                                modifier = Modifier.weight(1f)
                            )
                            PlanCard(
                                label = "Yearly",
                                price = uiState.yearlyPrice,
                                period = uiState.yearlyPeriod,
                                selected = uiState.selectedPlan == PlanType.YEARLY,
                                badge = "Best value",
                                onClick = { viewModel.selectPlan(PlanType.YEARLY) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    GradientButton(
                        text = if (uiState.hasFreeTrial) "Start 14-Day Free Trial" else "Subscribe",
                        onClick = { context.findActivity()?.let { viewModel.purchase(it) } },
                        modifier = Modifier.fillMaxWidth(),
                        loading = uiState.isLoading,
                        enabled = uiState.pricesLoaded && !uiState.isLoading
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        if (uiState.hasFreeTrial) {
                            "14 days free, then $selectedPriceText. Cancel anytime in Google Play before the trial ends to avoid being charged."
                        } else {
                            "$selectedPriceText. Cancel anytime in Google Play."
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    if (!uiState.pricesLoaded) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            "Loading local prices from Google Play…",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    TextButton(
                        onClick = { viewModel.restorePurchases() },
                        enabled = !uiState.isLoading,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "Restore purchases",
                            color = BrandBlue,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    TextButton(
                        onClick = onBack,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "Continue with free tracking",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    if (com.nexal.app.BuildConfig.DEBUG) {
                        Spacer(modifier = Modifier.height(4.dp))
                        TextButton(onClick = { viewModel.skipForDev() }) {
                            Text(
                                "Skip (Dev Testing)",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }

                    uiState.error?.let { error ->
                        Spacer(modifier = Modifier.height(12.dp))
                        Surface(
                            color = MaterialTheme.colorScheme.errorContainer,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                error,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(metrics.sectionSpacing))
        }
    }
}

@Composable
private fun PlanCard(
    label: String,
    price: String,
    period: String,
    selected: Boolean,
    badge: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (selected) Accent else MaterialTheme.colorScheme.outlineVariant
    val bgColor = if (selected) AccentWash else MaterialTheme.colorScheme.surface

    Surface(
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(if (selected) 2.dp else 1.dp, borderColor),
        color = bgColor,
        modifier = modifier.clickable { onClick() }
    ) {
        Box {
            Column(
                modifier = Modifier.padding(vertical = 22.dp, horizontal = 16.dp).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    label.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = if (selected) AccentDeep else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    price,
                    style = MaterialTheme.typography.headlineMedium,
                    color = if (selected) AccentDeep else MaterialTheme.colorScheme.onSurface
                )
                Text(
                    period,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (badge != null) {
                Surface(
                    color = Accent,
                    shape = RoundedCornerShape(bottomStart = 12.dp, topEnd = 24.dp),
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Text(
                        badge,
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun PaywallFeature(
    icon: ImageVector,
    title: String,
    description: String,
    color: Color,
    compact: Boolean = false
) {
    Surface(
        color = color.copy(alpha = 0.06f),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(if (compact) 10.dp else 14.dp),
            horizontalArrangement = Arrangement.spacedBy(if (compact) 10.dp else 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(if (compact) 34.dp else 40.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(if (compact) 18.dp else 22.dp)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1
                )
                Text(
                    description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = if (compact) 2 else 3
                )
            }
        }
    }
}
