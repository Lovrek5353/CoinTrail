package com.example.cointrail.composables

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.cointrail.R
import com.example.cointrail.data.SavingPocket
import com.example.cointrail.data.dummySavingPocket

@Composable
fun SavingPocketCard(
    savingPocket: SavingPocket,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val progress = if (savingPocket.targetAmount > 0)
        (savingPocket.balance / savingPocket.targetAmount).coerceIn(0.0, 1.0).toFloat()
    else 0f

    Surface(
        modifier = modifier
            .padding(dimensionResource(R.dimen.padding8))
            .border(
                width = dimensionResource(R.dimen.padding1),
                color = MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(dimensionResource(R.dimen.round24))
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
                text = savingPocket.name,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.height12)))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(dimensionResource(R.dimen.round4))),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.secondaryContainer,
            )

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.height4)))

            Text(
                text = "${savingPocket.balance} / ${savingPocket.targetAmount}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview
@Composable
fun SavingPocketCardPreview() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        SavingPocketCard(
            savingPocket = dummySavingPocket,
            modifier = Modifier.fillMaxWidth(0.75f),
            onClick = {}
        )
    }
}
