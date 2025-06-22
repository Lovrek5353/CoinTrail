import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cointrail.data.Tab
import com.example.cointrail.ui.theme.AccentGold
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun TabSummary(
    tab: Tab,
    modifier: Modifier = Modifier
) {
    // Calculate remaining to pay, never less than zero
    val remainingToPay = (tab.initialAmount - tab.outstandingBalance).coerceAtLeast(0.0)
    val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
        tonalElevation = 2.dp,
        shadowElevation = 4.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Description (no label)
            Text(
                text = tab.description,
                fontSize = 24.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))

            PropertyRow(label = "Lender:", value = tab.lender)
            PropertyRow(label = "Amount:", value = "$${tab.initialAmount}")
            PropertyRow(label = "Outstanding:", value = "$${tab.outstandingBalance}")

            // Remaining to Pay (accent color)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp)
            ) {
                Text(
                    text = "Remaining to Pay:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = AccentGold
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "$${remainingToPay}",
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            PropertyRow(label = "Interest:", value = "${tab.interestRate}%")

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp)
            ) {
                Text(
                    text = "Status:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = tab.status.uppercase(),
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    color = when (tab.status) {
                        "active" -> Color(0xFF4CAF50)
                        "paid" -> Color(0xFF2196F3)
                        "overdue" -> Color(0xFFF44336)
                        else -> MaterialTheme.colorScheme.onSurface
                    }
                )
            }

            PropertyRow(
                label = "Start:",
                value = tab.startDate?.toDate()?.let { dateFormat.format(it) } ?: "-"
            )
            PropertyRow(
                label = "Due:",
                value = tab.dueDate?.toDate()?.let { dateFormat.format(it) } ?: "-"
            )
        }
    }
}

@Composable
private fun PropertyRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
    ) {
        Text(
            text = label,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = value,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
