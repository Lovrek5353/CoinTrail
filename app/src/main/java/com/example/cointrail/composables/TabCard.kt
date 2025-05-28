package com.example.cointrail.composables

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.cointrail.data.SavingPocket
import com.example.cointrail.data.Tab
import com.example.cointrail.data.dummyTab
import com.example.cointrail.ui.theme.CoinTrailTheme

@Composable
fun TabCard(
    tab: Tab,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
){
    val progress = if (tab.outstandingBalance > 0)
        (tab.outstandingBalance / tab.initialAmount).coerceIn(0.0, 1.0).toFloat()
    else 0f

    Surface(
        modifier = modifier
            .padding(8.dp)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline, // Use outline for subtle border
                shape = RoundedCornerShape(24.dp)
            )
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surfaceVariant // Use surfaceVariant for card background
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = tab.name,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant // Use onSurfaceVariant for text
            )

            Spacer(modifier = Modifier.height(12.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.secondaryContainer, // Use secondaryContainer for track
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "${tab.outstandingBalance} / ${tab.initialAmount}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}


@Preview
@Composable
fun TabCardPreview(){
    CoinTrailTheme {
        TabCard(
            tab= dummyTab,
            onClick = {},
        )
    }
}