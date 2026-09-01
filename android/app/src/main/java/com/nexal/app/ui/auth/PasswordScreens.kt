package com.nexal.app.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nexal.app.ui.components.FadeSlideIn
import com.nexal.app.ui.components.FitCard
import com.nexal.app.ui.components.GradientButton
import com.nexal.app.ui.components.ScalePopIn
import com.nexal.app.ui.components.rememberAdaptiveMetrics
import com.nexal.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(
    onNavigateBack: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val metrics = rememberAdaptiveMetrics()
    var email by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Emerald50, Slate50, CreamSurface)))
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text("Reset Password") },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .imePadding()
                    .verticalScroll(rememberScrollState())
                    .padding(
                        horizontal = metrics.horizontalPadding,
                        vertical = metrics.verticalPadding
                    ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (uiState.forgotPasswordSent) {
                    ScalePopIn {
                        Icon(
                            Icons.Default.Email,
                            null,
                            modifier = Modifier.size(metrics.heroSize),
                            tint = BrandBlue
                        )
                    }
                    Spacer(modifier = Modifier.height(metrics.sectionSpacing))
                    FadeSlideIn(delayMs = 80) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "Check Your Email",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "We've sent a password reset link to your email. Please check your inbox.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                } else {
                    FadeSlideIn {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "Forgot your password?",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Enter your email and we'll send you a link to reset it.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(metrics.sectionSpacing))

                    ScalePopIn(delayMs = 80) {
                        FitCard(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(if (metrics.isCompactWidth) 16.dp else 20.dp)) {
                                OutlinedTextField(
                                    value = email,
                                    onValueChange = { email = it },
                                    label = { Text("Email") },
                                    leadingIcon = { Icon(Icons.Default.Email, null, tint = BrandBlue) },
                                    modifier = Modifier.fillMaxWidth(),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                    singleLine = true,
                                    shape = MaterialTheme.shapes.medium
                                )
                                Spacer(modifier = Modifier.height(16.dp))

                                if (uiState.error != null) {
                                    Text(
                                        uiState.error!!,
                                        color = ErrorRed,
                                        style = MaterialTheme.typography.bodySmall,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Spacer(modifier = Modifier.height(metrics.fieldSpacing))
                                }

                                GradientButton(
                                    text = "Send Reset Link",
                                    onClick = { viewModel.forgotPassword(email) },
                                    modifier = Modifier.fillMaxWidth(),
                                    loading = uiState.isLoading,
                                    enabled = email.isNotBlank()
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResetPasswordScreen(
    token: String,
    onSuccess: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val metrics = rememberAdaptiveMetrics()
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.resetPasswordSuccess) {
        if (uiState.resetPasswordSuccess) onSuccess()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Emerald50, Slate50, CreamSurface)))
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text("Set New Password") },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .imePadding()
                    .verticalScroll(rememberScrollState())
                    .padding(
                        horizontal = metrics.horizontalPadding,
                        vertical = metrics.verticalPadding
                    ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                FadeSlideIn {
                    Text(
                        "Create a new password",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Spacer(modifier = Modifier.height(metrics.sectionSpacing))

                ScalePopIn(delayMs = 80) {
                    FitCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(if (metrics.isCompactWidth) 16.dp else 20.dp)) {
                            OutlinedTextField(
                                value = password,
                                onValueChange = { password = it },
                                label = { Text("New Password") },
                                modifier = Modifier.fillMaxWidth(),
                                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                trailingIcon = {
                                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                        Icon(
                                            if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                            contentDescription = "Toggle password visibility"
                                        )
                                    }
                                },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                singleLine = true,
                                shape = MaterialTheme.shapes.medium
                            )
                            Spacer(modifier = Modifier.height(metrics.fieldSpacing))
                            OutlinedTextField(
                                value = confirmPassword,
                                onValueChange = { confirmPassword = it },
                                label = { Text("Confirm Password") },
                                modifier = Modifier.fillMaxWidth(),
                                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                singleLine = true,
                                shape = MaterialTheme.shapes.medium,
                                isError = confirmPassword.isNotBlank() && password != confirmPassword
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            if (uiState.error != null) {
                                Text(
                                    uiState.error!!,
                                    color = ErrorRed,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(metrics.fieldSpacing))
                            }

                            GradientButton(
                                text = "Reset Password",
                                onClick = { viewModel.resetPassword(token, password) },
                                modifier = Modifier.fillMaxWidth(),
                                loading = uiState.isLoading,
                                enabled = password.length >= 8 && password == confirmPassword
                            )
                        }
                    }
                }
            }
        }
    }
}
