package com.yazidistiqlaladhyfadhillah607062430005.mobpro1_assessment3_cineticket.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun StarRatingBar(
    rating: Float,
    onRatingChanged: (Float) -> Unit,
    modifier: Modifier = Modifier,
    maxStars: Int = 5,
    isReadOnly: Boolean = false
) {
    Row(modifier = modifier) {
        for (i in 1..maxStars) {
            val isSelected = i <= rating
            val icon = Icons.Filled.Star
            val iconTintColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
            
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTintColor,
                modifier = Modifier
                    .size(36.dp)
                    .clickable(enabled = !isReadOnly) {
                        onRatingChanged(i.toFloat())
                    }
            )
        }
    }
}
