import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.cointrail.data.AssetSearch

@Composable
fun AssetSearchItem(
    asset: AssetSearch,
    onClick: () -> Unit,
    onAddToPortfolio: () -> Unit,
    onAddToWatchlist: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp)
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "${asset.symbol} - ${asset.name}",
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${asset.typeDispl} | ${asset.exchDisp}",
                    fontWeight = FontWeight.Normal
                )
            }
            Row {
                IconButton(onClick = onAddToPortfolio) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add to Portfolio"
                    )
                }
                IconButton(onClick = onAddToWatchlist) {
                    Icon(
                        imageVector = Icons.Default.FavoriteBorder,
                        contentDescription = "Add to Watchlist"
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AssetSearchItemPreview() {
    AssetSearchItem(
        asset = AssetSearch(
            symbol = "AAPL",
            name = "Apple Inc.",
            exchDisp = "NASDAQ",
            typeDispl = "Equity"
        ),
        onClick = {},
        onAddToPortfolio = {},
        onAddToWatchlist = {}
    )
}
