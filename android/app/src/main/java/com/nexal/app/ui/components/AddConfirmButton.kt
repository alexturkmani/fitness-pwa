package com.nexal.app.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nexal.app.ui.theme.BrandBlue
import com.nexal.app.ui.theme.SuccessGreen

/**
 * + button that flips to a checkmark after a successful add.
 */
@Composable
fun AddConfirmIconButton(
    added: Boolean,
    onClick: () -> Unit,
    contentDescription: String = "Add",
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    FilledIconButton(
        onClick = onClick,
        enabled = enabled && !added,
        modifier = modifier.size(44.dp),
        colors = IconButtonDefaults.filledIconButtonColors(
            containerColor = BrandBlue,
            contentColor = androidx.compose.ui.graphics.Color.White,
            disabledContainerColor = SuccessGreen,
            disabledContentColor = androidx.compose.ui.graphics.Color.White
        )
    ) {
        AnimatedContent(
            targetState = added,
            transitionSpec = {
                (fadeIn() + scaleIn(initialScale = 0.6f)) togetherWith
                    (fadeOut() + scaleOut(targetScale = 0.6f))
            },
            label = "add-confirm"
        ) { isAdded ->
            if (isAdded) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = "Added",
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Icon(
                    Icons.Default.Add,
                    contentDescription = contentDescription,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
