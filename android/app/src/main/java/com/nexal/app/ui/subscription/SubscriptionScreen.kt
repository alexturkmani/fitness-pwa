package com.nexal.app.ui.subscription

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
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
                        if (uiState.isActive) {
                            if (uiState.isTrial) "Free Trial Active" else "Premium Active"
                        } else "No Active Subscription",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    if (uiState.isActive && uiState.isTrial) {
                        Spacer(Modifier.height(8.dp))
                        Surface(
                            color = Emerald500,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                "${uiState.trialDaysLeft} days remaining",
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        Text(
                            "Ends ${uiState.expirationDate}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    } else if (!uiState.isActive) {
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
                // Price display
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        "\$4.99",
                        style = MaterialTheme.typography.headlineLarge.copy(fontSize = 40.sp),
                        fontWeight = FontWeight.Bold,
                        color = Emerald500
                    )
                    Text(
                        "/month",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 6.dp, start = 2.dp)
                    )
                }

                Spacer(Modifier.height(16.dp))

                if (!uiState.hasUsedTrial) {
                    GradientButton(
                        text = "Start 7-Day Free Trial",
                        onClick = { viewModel.startTrial() },
                        modifier = Modifier.fillMaxWidth(),
                        loading = uiState.isLoading
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "No charge during trial. Cancel anytime.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    GradientButton(
                        text = "Subscribe Now",
                        onClick = { viewModel.purchase(context as Activity) },
                        modifier = Modifier.fillMaxWidth(),
                        loading = uiState.isLoading
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "Billed monthly. Cancel anytime.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
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
