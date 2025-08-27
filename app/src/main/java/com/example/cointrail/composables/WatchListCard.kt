import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
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
fun WatchlistCard(
    asset: AssetSearch,
    onClick: () -> Unit,
    onDelete: () -> Unit
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
                    text = asset.typeDispl,
                    fontWeight = FontWeight.Normal
                )
            }
            IconButton(
                onClick = onDelete,
                modifier = Modifier // ensures the delete button doesn't trigger card click
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete from Watchlist"
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WatchlistCardPreview() {
    WatchlistCard(
        asset = AssetSearch(
            symbol = "APL",
            name = "Apple Inc.",
            exchDisp = "NASDAQ",
            typeDispl = "Equity"
        ),
        onClick = {},
        onDelete = {}
    )
}
