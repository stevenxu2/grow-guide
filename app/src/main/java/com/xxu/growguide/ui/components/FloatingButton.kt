package com.xxu.growguide.ui.components

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Purpose: A customized floating action button component
 *
 * @param description Content description for accessibility
 * @param onClick Callback function to be invoked when the button is clicked
 */
@Composable
fun FloatingButton(
    modifier: Modifier,
    description: String,
    onClick: () -> Unit
) {
    FloatingActionButton(
        modifier = modifier,
        shape = CircleShape,
        onClick = {
            onClick()
        },
        containerColor = MaterialTheme.colorScheme.secondary
    ) {
        Icon(Icons.Filled.Add, description)
    }
}
