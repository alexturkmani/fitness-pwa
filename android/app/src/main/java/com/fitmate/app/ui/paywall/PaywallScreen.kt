package com.fitmate.app.ui.paywall

import android.app.Activity
import androidx.compose.foundation.background
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
import com.fitmate.app.ui.components.*
import com.fitmate.app.ui.subscription.SubscriptionViewModel
import com.fitmate.app.ui.theme.Cyan500
import com.fitmate.app.ui.theme.Emerald500

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
    LaunchedEffect(uiState.isActive, uiState.trialStarted) {
        if (uiState.isActive || uiState.trialStarted) onSubscribed()
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
                        "Start your 7-day free trial, then just ${uiState.priceText}",
                        style = MaterialTheme.typography.bodyLarge,
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
                // Price badge
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            verticalAlignment = Alignment.Bottom,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                uiState.priceText.substringBefore("/"),
                                style = MaterialTheme.typography.headlineLarge.copy(fontSize = 42.sp),
                                fontWeight = FontWeight.Bold,
                                color = Emerald500
                            )
                            Text(
                                "/" + uiState.priceText.substringAfter("/", "month"),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(bottom = 8.dp, start = 2.dp)
                            )
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "7-day free trial included",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Emerald500,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))

                // Subscribe button — RevenueCat handles the 7-day free trial
                GradientButton(
                    text = "Start Free Trial & Subscribe",
                    onClick = { viewModel.purchase(context as Activity) },
                    modifier = Modifier.fillMaxWidth(),
                    loading = uiState.isLoading
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "7 days free, then ${uiState.priceText}. Cancel anytime.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

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
