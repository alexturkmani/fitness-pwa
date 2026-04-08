package com.nexal.app.ui.subscription

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.nexal.app.data.repository.PlanType
import com.nexal.app.ui.components.*
import com.nexal.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionScreen(
    onBack: () -> Unit,
    viewModel: SubscriptionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Handle one-shot events (open URLs)
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is SubscriptionEvent.OpenUrl -> {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(event.url))
                    context.startActivity(intent)
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Subscription", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Status banner
            Surface(
                color = if (uiState.isActive)
                    Emerald500.copy(alpha = 0.1f)
                else
                    Color(0xFFF59E0B).copy(alpha = 0.08f),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(
                                if (uiState.isActive) Brush.horizontalGradient(listOf(Emerald500, Cyan500))
                                else Brush.horizontalGradient(listOf(Color(0xFFF59E0B), Color(0xFFEF4444)))
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            if (uiState.isActive) Icons.Default.WorkspacePremium else Icons.Default.Lock,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    Text(
                        if (uiState.isActive) "Premium Active" else "No Active Subscription",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    if (!uiState.isActive) {
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "Unlock all premium features",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // Features grid
            Text(
                "What You Get",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(Modifier.height(12.dp))

            // Feature cards in a 2-column grid
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                FeatureCard("AI Workouts", "Personalized plans", Icons.Default.FitnessCenter, Emerald500, Modifier.weight(1f))
                FeatureCard("AI Meal Plans", "Macro-optimized", Icons.Default.Restaurant, Cyan500, Modifier.weight(1f))
            }
            Spacer(Modifier.height(10.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                FeatureCard("Scanner", "Barcode lookup", Icons.Default.QrCodeScanner, Color(0xFFF59E0B), Modifier.weight(1f))
                FeatureCard("Progress", "Charts & trends", Icons.Default.BarChart, Color(0xFF8B5CF6), Modifier.weight(1f))
            }
            Spacer(Modifier.height(10.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                FeatureCard("Substitutions", "AI food swaps", Icons.Default.SwapHoriz, Color(0xFFEC4899), Modifier.weight(1f))
                FeatureCard("Custom", "Your exercises", Icons.Default.Edit, Color(0xFF06B6D4), Modifier.weight(1f))
            }

            Spacer(Modifier.height(32.dp))

            // CTA section
            if (!uiState.isActive) {
                // Plan selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    PlanOption(
                        label = "Monthly",
                        price = "\$12.99",
                        period = "/month",
                        selected = uiState.selectedPlan == PlanType.MONTHLY,
                        badge = null,
                        onClick = { viewModel.selectPlan(PlanType.MONTHLY) },
                        modifier = Modifier.weight(1f)
                    )
                    PlanOption(
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
                    text = if (uiState.hasFreeTrial) "Start Free Trial" else "Subscribe Now",
                    onClick = { viewModel.purchase(context as Activity) },
                    modifier = Modifier.fillMaxWidth(),
                    loading = uiState.isLoading
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    if (uiState.hasFreeTrial) "Free trial included. Cancel anytime."
                    else "${if (uiState.selectedPlan == PlanType.MONTHLY) uiState.monthlyPriceText else uiState.yearlyPriceText}. Cancel anytime.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                OutlinedButton(
                    onClick = { viewModel.manageSubscription() },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Settings, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Manage Subscription")
                }
            }

            uiState.error?.let { error ->
                Spacer(Modifier.height(12.dp))
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        error,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun FeatureCard(
    title: String,
    description: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        color = color.copy(alpha = 0.08f),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = color, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.height(10.dp))
            Text(title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
            Text(description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun PlanOption(
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
