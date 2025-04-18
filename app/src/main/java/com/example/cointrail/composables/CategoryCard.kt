package com.example.cointrail.composables

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import com.example.cointrail.data.Category
import com.example.cointrail.data.dummyCategories
import com.example.cointrail.ui.theme.CoinTrailTheme

@Composable
fun CategoryCard(
    category: Category,
    modifier: Modifier = Modifier, // Allow external modifiers for flexibility
    onClick: () -> Unit // Callback for click events
) {
    Surface(
        modifier = modifier
            .padding(8.dp) // Add spacing around the card
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(24.dp) // Rounded corners for the border
            )
            .clickable { onClick() }, // Add click handling here
        shape = RoundedCornerShape(24.dp), // Shape of the card itself (matches border)
        color = MaterialTheme.colorScheme.surface // Background color of the card
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp), // Padding inside the card
            contentAlignment = Alignment.Center // Center the text inside the box
        ) {
            Text(
                text = category.name,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface // Text color based on theme
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CategoryCardPreview() {
    CoinTrailTheme {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2), // Two columns in the grid layout
            modifier = Modifier.padding(16.dp)
        ) {
            items(dummyCategories) { category ->
                CategoryCard(
                    category = category,
                    onClick = {
                        // Handle click event here (e.g., print to log or navigate)
                        println("Clicked on ${category.name}")
                    }
                )
            }
        }
    }
}
