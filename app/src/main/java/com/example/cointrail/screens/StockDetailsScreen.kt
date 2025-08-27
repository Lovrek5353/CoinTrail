package com.example.cointrail.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.cointrail.composables.HistoryLineGraph
import com.example.cointrail.data.Stock
import com.example.cointrail.viewModels.StocksViewModel
import java.text.SimpleDateFormat
import java.util.*


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockDetailsScreen(
    stockSymbol: String,
    navController: NavController,
    viewModel: StocksViewModel,
    stockID: String
) {
    Log.d("StockSymbolDetails", stockSymbol)
    var isFavorite by remember { mutableStateOf(false) }

    // Load API and history always, DB only if ID is provided
    LaunchedEffect(stockSymbol, stockID) {
        viewModel.fetchStockDetails(stockSymbol, type = "STOCKS")
        viewModel.fetchStockHistory(stockSymbol)
        if (stockID.isNotBlank()) {
            viewModel.loadStock(stockID)
        }
    }

    val stockApi by viewModel.stockState.collectAsState()
    val stockHistory by viewModel.stockHistory.collectAsState()
    val stockDB by viewModel.currentStock.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stockApi?.name ?: "Loading...",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.fillMaxWidth(0.7f)
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
                actions = {
                    IconButton(onClick = { isFavorite = !isFavorite }) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = "Watchlist",
                            tint = if (isFavorite) Color.Red else MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    IconButton(onClick = { /* TODO: Edit */ }) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = "Edit",
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                if (stockApi != null) {
                    ModernStockDetailsBody(stockApi = stockApi!!, stockDB = stockDB, viewModel=viewModel)
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
            item {
                HistoryLineGraph(stockHistory)
            }
        }
    }
}

@Composable
fun ModernStockDetailsBody(stockApi: Stock, stockDB: Stock?, viewModel: StocksViewModel) {
    val dateFormatter = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }

    Column(
        modifier = Modifier.padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(Modifier.weight(1f)) {
                        if (!stockApi.symbol.isNullOrBlank()) {
                            Text(
                                text = stockApi.symbol,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.primary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        if (!stockApi.name.isNullOrBlank()) {
                            val nameTextStyle = if ((stockApi.name?.length ?: 0) > 20)
                                MaterialTheme.typography.bodyMedium
                            else
                                MaterialTheme.typography.bodyLarge

                            Text(
                                text = stockApi.name,
                                style = nameTextStyle,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.fillMaxWidth(0.9f)
                            )
                        }
                    }
                    Column(
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .wrapContentWidth(Alignment.End)
                    ) {
                        if (stockApi.currentPrice != 0.0) {
                            Text(
                                text = "Current",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                            Text(
                                text = "${stockApi.currentPrice} ${stockApi.currency}",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                Divider()
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Total value from DB only if available
                    if (stockDB?.currentStockPrice != null && stockDB.currentStockPrice != 0.0) {
                        Column {
                            if(stockApi.currentPrice!=stockDB.originalPrice){
                                stockDB.currentStockPrice=stockApi.currentPrice*stockDB.amount
                                viewModel.updateStockPrice(stockDB.id.toString(),stockDB.currentStockPrice)
                            }
                            Text(
                                text = "Total Value",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                            Text(
                                text = "${stockDB.currentStockPrice} ${stockApi.currency}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                    // Net change from API
                    val netChange = stockApi.netChange.toDoubleOrNull() ?: 0.0
                    if (netChange != 0.0) {
                        val isPositive = netChange >= 0
                        val deltaColor = if (isPositive) Color(0xFF4CAF50) else Color(0xFFF44336)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (isPositive) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint = deltaColor
                            )
                            Text(
                                text = (if (isPositive) "+" else "") + "%.2f".format(netChange) + "%",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = deltaColor
                            )
                        }
                    }
                }
            }
        }

        if (stockDB != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    if (stockDB.amount != 0.0) InfoRow("Shares Owned", "%.2f".format(stockDB.amount))
                    if (stockDB.originalPrice != 0.0) InfoRow("Bought At", "${stockDB.originalPrice} ${stockApi.currency}")
                    stockDB.purchaseDate?.let {
                        val dateStr =
                            try {
                                dateFormatter.format(it.toDate())
                            } catch (e: Exception) {
                                it.toString()
                            }
                        InfoRow("Purchase Date", dateStr)
                    }
                    if (stockDB.targetPrice != null && stockDB.targetPrice != 0.0)
                        InfoRow("Target Price", "${stockDB.targetPrice} ${stockApi.currency}")
                    if (stockDB.dividendsReceived != 0.0)
                        InfoRow("Dividends", "${stockDB.dividendsReceived} ${stockApi.currency}")
                    if (!stockDB.sector.isNullOrBlank()) InfoRow("Sector", stockDB.sector)
                    if (!stockDB.exchange.isNullOrBlank()) InfoRow("Exchange", stockDB.exchange)

                    if (!stockApi.deltaIndicator.isNullOrBlank()) {
                        val isUp = stockApi.deltaIndicator.lowercase() == "up"
                        InfoRow(
                            label = "Change",
                            value = stockApi.deltaIndicator.replaceFirstChar { it.uppercase() },
                            valueColor = if (isUp) Color(0xFF4CAF50) else Color(0xFFF44336)
                        )
                    }
                }
            }
        }

        if (stockDB?.notes?.isNotBlank() == true) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text(
                        text = "Notes",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = stockDB.notes,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
fun InfoRow(label: String, value: String, valueColor: Color = MaterialTheme.colorScheme.onSurface) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = valueColor
        )
    }
}
