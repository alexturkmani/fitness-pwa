package com.nexal.app.ui.paywall

import android.app.Activity
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
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nexal.app.ui.components.*
import com.nexal.app.data.repository.PlanType
import com.nexal.app.ui.subscription.SubscriptionViewModel
import com.nexal.app.ui.theme.Cyan500
import com.nexal.app.ui.theme.Emerald500

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaywallScreen(
    onBack: () -> Unit,
    onSubscribed: () -> Unit,
    viewModel: SubscriptionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Navigate when subscription is active
    LaunchedEffect(uiState.isActive, uiState.purchaseCompleted) {
        if (uiState.isActive || uiState.purchaseCompleted) onSubscribed()
    }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Hero section with gradient background
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Emerald500.copy(alpha = 0.08f),
                                Color.Transparent
                            )
                        )
                    )
                    .padding(horizontal = 24.dp, vertical = 40.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Animated icon
                    Box(
                        modifier = Modifier
                            .size(88.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.horizontalGradient(listOf(Emerald500, Cyan500))
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Bolt,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(44.dp)
                        )
                    }

                    Spacer(Modifier.height(24.dp))

                    Text(
                        "Welcome to Nexal",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        "Your AI-Powered Fitness Partner",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 8.dp, start = 16.dp, end = 16.dp)
                    )
                }
            }

            // Features
            Column(
                modifier = Modifier.padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                PaywallFeature(Icons.Default.FitnessCenter, "AI Workout Plans", "Personalized training tailored to your goals and experience", Emerald500)
                PaywallFeature(Icons.Default.Restaurant, "AI Meal Plans", "Macro-optimized nutrition with allergy and preference support", Cyan500)
                PaywallFeature(Icons.Default.QrCodeScanner, "Barcode Scanner", "Scan any product for instant nutrition info and AI assessment", Color(0xFFF59E0B))
                PaywallFeature(Icons.Default.BarChart, "Progress Analytics", "Track weight, volume, and nutrition trends over time", Color(0xFF8B5CF6))
                PaywallFeature(Icons.Default.SwapHoriz, "Smart Substitutions", "AI food alternatives that match your macro targets", Color(0xFFEC4899))
            }

            Spacer(Modifier.height(32.dp))

            // Pricing section
            Column(
                modifier = Modifier.padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Plan selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    PlanCard(
                        label = "Monthly",
                        price = "\$12.99",
                        period = "/month",
                        selected = uiState.selectedPlan == PlanType.MONTHLY,
                        badge = null,
                        onClick = { viewModel.selectPlan(PlanType.MONTHLY) },
                        modifier = Modifier.weight(1f)
                    )
                    PlanCard(
                        label = "Yearly",
                        price = "\$110",
                        period = "/year",
                        selected = uiState.selectedPlan == PlanType.YEARLY,
                        badge = "Save 29%",
                        onClick = { viewModel.selectPlan(PlanType.YEARLY) },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(Modifier.height(20.dp))

                GradientButton(
                    text = "Start 14-Day Free Trial",
                    onClick = { viewModel.purchase(context as Activity) },
                    modifier = Modifier.fillMaxWidth(),
                    loading = uiState.isLoading
                )
                Spacer(Modifier.height(8.dp))
                val selectedPriceText = if (uiState.selectedPlan == PlanType.MONTHLY) uiState.monthlyPriceText else uiState.yearlyPriceText
                Text(
                    "14 days free, then $selectedPriceText. Cancel anytime.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(8.dp))
                TextButton(onClick = { viewModel.skipForDev() }) {
                    Text(
                        "Skip (Dev Testing)",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                uiState.error?.let { error ->
                    Spacer(Modifier.height(12.dp))
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

            Spacer(Modifier.height(32.dp))
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
    val borderColor = if (selected) Emerald500 else MaterialTheme.colorScheme.outlineVariant
    val bgColor = if (selected) Emerald500.copy(alpha = 0.08f) else Color.Transparent

    Surface(
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(if (selected) 2.dp else 1.dp, borderColor),
        color = bgColor,
        modifier = modifier.clickable { onClick() }
    ) {
        Box {
            Column(
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    label,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    price,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (selected) Emerald500 else MaterialTheme.colorScheme.onSurface
                )
                Text(
                    period,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (badge != null) {
                Surface(
                    color = Emerald500,
                    shape = RoundedCornerShape(bottomStart = 8.dp, topEnd = 16.dp),
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
    color: Color
) {
    Surface(
        color = color.copy(alpha = 0.06f),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(22.dp))
            }
            Column {
                Text(title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                Text(description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
