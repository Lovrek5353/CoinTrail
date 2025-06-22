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
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.cointrail.R
import com.example.cointrail.data.Tab
import com.example.cointrail.ui.theme.CoinTrailTheme

@Composable
fun TabCard(
    tab: Tab,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val progress = if (tab.initialAmount > 0)
        (tab.outstandingBalance.toFloat() / tab.initialAmount.toFloat()).coerceIn(0f, 1f)
    else 0f

    // Explicitly convert dimension resource to Dp for RoundedCornerShape
    val cornerRadius = dimensionResource(R.dimen.round24)
    val clipRadius = dimensionResource(R.dimen.clip4)
    val borderWidth = dimensionResource(R.dimen.padding1)

    Surface(
        modifier = modifier
            .padding(dimensionResource(R.dimen.padding8))
            .border(
                width = borderWidth,
                color = MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(cornerRadius)
            )
            .clickable { onClick() },
        shape = RoundedCornerShape(cornerRadius),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.padding16))
        ) {
            Text(
                text = tab.name,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.height12)))

            LinearProgressIndicator(
                progress = progress, // Use the calculated float progress
                modifier = Modifier
                    .fillMaxWidth()
                    .height(dimensionResource(R.dimen.padding8))
                    .clip(RoundedCornerShape(clipRadius)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.secondaryContainer,
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
fun TabCardPreview() {
    CoinTrailTheme {
        // You'll need to define a dummyTab here for the preview to work
//        val dummyTab = Tab(
//            id = "1",
//            name = "Groceries",
//            initialAmount = 100.0,
//            outstandingBalance = 50.0,
//            currency = "USD",
//            createdTimestamp = System.currentTimeMillis(),
//            lastModifiedTimestamp = System.currentTimeMillis()
//        )
//        TabCard(
//            tab = dummyTab,
//            onClick = {},
//        )
    }
}