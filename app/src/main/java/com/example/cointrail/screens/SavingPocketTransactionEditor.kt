package com.example.cointrail.screens

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.example.cointrail.R
import com.example.cointrail.composables.DatePickerModal
import com.example.cointrail.ui.theme.CoinTrailTheme
import com.example.cointrail.viewModels.SavingPocketsViewModel
import kotlinx.coroutines.flow.collectLatest
import java.util.Date
import java.util.Locale


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavingPocketTransactionEditor(
    viewModel: SavingPocketsViewModel,
    navController: NavController,
    modifier: Modifier = Modifier,
    savingPocketID: String
) {
    Log.d("SavingPocketTransactionEditor", "savingPocketID: $savingPocketID")
    var showDatePicker by remember { mutableStateOf(false) }

    // IMPORTANT: Fetch the specific saving pocket when this screen is launched
    LaunchedEffect(savingPocketID) {
        if (savingPocketID.isNotEmpty() && savingPocketID != "Default ID") { // Avoid fetching with a default or empty ID
            viewModel.fetchSavingPocket(savingPocketID)
            Log.d("SavingPocketTransactionEditor", "Calling fetchSavingPocket for: $savingPocketID")
        } else {
            Log.e("SavingPocketTransactionEditor", "Received invalid savingPocketID: $savingPocketID")
            // Optionally, show a snackbar or navigate back if the ID is invalid
            viewModel.onTransactionDescriptionChange("Error: Invalid Saving Pocket ID") // Or a better error handling
        }
        // Also set the category ID here, it's safe to do so
        viewModel.setTransactionCategory(savingPocketID)
    }
    LaunchedEffect(Unit) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is SavingPocketsViewModel.UiEvent.SubmissionSuccess -> {
                    navController.popBackStack() // or navigate("destination_screen")
                }
                is SavingPocketsViewModel.UiEvent.ShowSnackbar -> {
                }
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.newTransaction),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() }  //navigate to screen before
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.emailIcon),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )

        }
    )
    {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(it), // Apply Scaffold's padding here
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Text(
                    text = stringResource(id = R.string.savingPocketTransactionAmout),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            item {
                Spacer(modifier = modifier.height(dimensionResource(id = R.dimen.padding16)))
            }
            item {
                OutlinedTextField(
                    value = viewModel.transactionAmountString,
                    onValueChange = viewModel::onAmountInputChange,
                    label = { Text(text = stringResource(id = R.string.savingPocketTransactionAmout)) },
                    placeholder = { Text(text = stringResource(id = R.string.savingPocketTransactionAmout)) },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        capitalization = KeyboardCapitalization.None,
                        keyboardType = KeyboardType.Number
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                Spacer(modifier = modifier.height(dimensionResource(id = R.dimen.padding16)))
            }
            item {
                Text(
                    text = stringResource(id = R.string.transactionDate),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            item {
                Spacer(modifier = modifier.height(dimensionResource(id = R.dimen.padding16)))
            }
            item {
                val selectedDateMillis = viewModel.transactionDateMillis
                val formattedDate = selectedDateMillis?.let {
                    java.text.SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) // Corrected format to "yyyy"
                        .format(Date(it))
                } ?: stringResource(id = R.string.selectDatePrompt)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = dimensionResource(id = R.dimen.padding16))
                        .clickable { showDatePicker = true }
                        .height(dimensionResource(id = R.dimen.padding48)),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = formattedDate,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            item {
                if (showDatePicker) {
                    DatePickerModal(
                        onDateSelected = { millis ->
                            if (millis != null) {
                                viewModel.onTransactionDateSelected(millis)
                            }
                            showDatePicker = false
                        },
                        onDismiss = { showDatePicker = false }
                    )
                }
            }
            item {
                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding16)))
            }
            item {
                Text(
                    text = stringResource(id = R.string.transactionDescription),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            item {
                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding16)))
            }
            item{
                OutlinedTextField(
                    value = viewModel.transactionDescriptionString,
                    onValueChange = viewModel::onTransactionDescriptionChange,
                    label = { Text(text = stringResource(id = R.string.transactionDescription)) },
                )
            }
            item{
                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding16)))
            }
            item {
                Button(onClick = {
                    // Category ID is already set in LaunchedEffect for this screen
                    viewModel.onTransactionSubmit()
                    Log.d("SavingPocketTransactionEditor", "onSubmit clicked")
                })
                {
                    Text(text = stringResource(id = R.string.add))
                }
            }
        }
    }
}

@Preview
@Composable
fun SavingPocketTransactionEditorPreview() {
    CoinTrailTheme {
        // You would need to provide mock dependencies for the ViewModel and NavController
        // for a meaningful preview.
        // val mockRepo = object : Repository { /* implement mock methods */ }
        // val mockViewModel = SavingPocketsViewModel(mockRepo)
        // val mockNavController = rememberNavController()
        // SavingPocketTransactionEditor(viewModel = mockViewModel, navController = mockNavController, savingPocketID = "preview_id")
    }
}