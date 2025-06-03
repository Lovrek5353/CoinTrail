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
            .padding(dimensionResource(R.dimen.padding8))
            .border(
                width = dimensionResource(R.dimen.padding1),
                color = MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(R.dimen.round24)
            )
            .clickable { onClick() },
        shape = RoundedCornerShape(dimensionResource(R.dimen.round24)),
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
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(dimensionResource(R.dimen.padding8))
                    .clip(RoundedCornerShape(R.dimen.clip4)),
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
fun TabCardPreview(){
    CoinTrailTheme {
        TabCard(
            tab= dummyTab,
            onClick = {},
        )
    }
}