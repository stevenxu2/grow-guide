package com.xxu.growguide.ui.components

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable

/**
 * Purpose: A customized floating action button component
 *
 * @param description Content description for accessibility
 * @param onClick Callback function to be invoked when the button is clicked
 */
@Composable
fun FloatingButton(description: String, onClick: () -> Unit) {
    FloatingActionButton(
            shape = CircleShape,
            onClick = {
                onClick()
            },
        ) {
        Icon(Icons.Filled.Add, description)
    }
}
