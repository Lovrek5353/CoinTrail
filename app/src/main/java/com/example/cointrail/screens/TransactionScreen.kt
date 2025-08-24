import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.cointrail.data.Transaction
import com.example.cointrail.data.enums.TransactionType
import com.example.cointrail.navigation.Screen
import com.example.cointrail.viewModels.TransactionViewModel
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.collectLatest
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionScreen(
    transactionID: String,
    categoryID: String,
    navController: NavController,
    viewModel: TransactionViewModel
) {
    val transaction by viewModel.transaction.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showDeleteDialog by remember { mutableStateOf(false) }

    // Collect snack-bar events from the ViewModel
    LaunchedEffect(Unit) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is TransactionViewModel.UiEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(event.message)
                }
                else -> {}
            }
        }
    }

    // Load transaction when screen is opened
    LaunchedEffect(categoryID, transactionID) {
        if(categoryID=="0"){
            viewModel.loadSingleTransaction(transactionID)
        }
        else{
            viewModel.loadTransaction(categoryID, transactionID)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Transaction Details",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Card(
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = transaction?.description ?: "No Description",
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = { showDeleteDialog = true },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = "Delete Transaction",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                        IconButton(
                            onClick = { navController.navigate(Screen.UpdateTransactionEditorScreen.createRoute(transactionID)) },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Edit,
                                contentDescription = "Edit Transaction",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    TransactionDetailRow(
                        label = "Amount",
                        value = transaction?.amount?.let { "€%.2f".format(it) } ?: "No amount",
                        color = if ((transaction?.type ?: TransactionType.DEPOSIT) == TransactionType.DEPOSIT)
                            Color(0xFF388E3C) else Color(0xFFD32F2F)
                    )
//                    TransactionDetailRow("Category", transaction?.categoryId ?: "No category")
                    TransactionDetailRow("Type", transaction?.type?.name ?: "No type")
                    TransactionDetailRow("Date", formatDate(transaction?.date))
//                    TransactionDetailRow("User ID", transaction?.userID ?: "No userID")
                }
            }
        }
    }

    // Confirmation Dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Transaction") },
            text = { Text("Are you sure you want to delete this transaction?") },
            confirmButton = {
                TextButton(onClick = {
                    transaction?.id?.let { viewModel.deleteTransaction(it) }
                    viewModel.updateBalanceAfterDeletion(categoryID, transaction?.amount ?: 0.0)
                    showDeleteDialog = false
                }) { Text("Yes") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("No") }
            }
        )
    }
}

@Composable
private fun TransactionDetailRow(label: String, value: String, color: Color = MaterialTheme.colorScheme.onSurface) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = color
        )
    }
}

private fun formatDate(timestamp: Timestamp?): String {
    return timestamp?.toDate()
        ?.toInstant()
        ?.atZone(ZoneId.systemDefault())
        ?.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
        ?: "N/A"
}
