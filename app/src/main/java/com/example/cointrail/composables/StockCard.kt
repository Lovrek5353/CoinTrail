package com.example.cointrail.composables

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import com.example.cointrail.R
import com.example.cointrail.data.Stock
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row


@SuppressLint("DefaultLocale")
@Composable
fun StockCard(
    stock: Stock,
    onClick: () -> Unit
) {
    val percentChange = if (stock.originalPrice != 0.0)
        ((stock.currentPrice - stock.originalPrice) / stock.originalPrice) * 100
    else 0.0

    val percentColor = when {
        percentChange > 0 -> Color(0xFF2E7D32)
        percentChange < 0 -> Color(0xFFC62828)
        else -> Color.Gray
    }

    val totalValue = stock.amount * stock.currentPrice

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(dimensionResource(R.dimen.padding8))
            .clickable{ onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = dimensionResource(R.dimen.height4))
    ) {
        Column(modifier = Modifier.padding(dimensionResource(R.dimen.padding16))) {
            Text(
                text = "${stock.name} (${stock.symbol})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.height4)))
            Text(
                text = "Amount: ${stock.amount}",
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.height4)))
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Price: ${stock.currentPrice} ${stock.currency}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "${stock.netChange}%",
                    color = percentColor,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.height4)))
            Text(
                text = "Total: %.2f ${stock.currency}".format(totalValue),
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
