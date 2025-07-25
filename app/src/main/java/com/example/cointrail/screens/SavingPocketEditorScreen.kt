package com.example.cointrail.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.example.cointrail.R
import com.example.cointrail.ui.theme.CoinTrailTheme
import com.example.cointrail.viewModels.SavingPocketsViewModel
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.rememberNavController
import com.example.cointrail.composables.DatePickerModal
import com.example.cointrail.navigation.Screen
import com.example.cointrail.repository.RepositoryImpl
import java.util.Date
import java.util.Locale


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavingPocketEditorScreen(
    modifier: Modifier=Modifier,
    navController: NavController,
    viewModel: SavingPocketsViewModel
){
    var showDatePicker by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.newSavingPocket),
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
                            contentDescription = stringResource(id = R.string.backIcon),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )

        }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Text(
                    text = stringResource(id = R.string.savingPocketName),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(bottom = dimensionResource(id = R.dimen.padding8))
                )
            }
            item {
                OutlinedTextField(
                    value = viewModel.nameString,
                    onValueChange = viewModel::onNameInput,
                    label = { Text(text = stringResource(id = R.string.savingPocketName)) },
                    placeholder = { Text(text = stringResource(id = R.string.savingPocketName)) },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        capitalization = KeyboardCapitalization.None,
                        keyboardType = KeyboardType.Text
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
            item {
                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding16)))
            }
            item {
                Text(
                    text = stringResource(id = R.string.savingAmount),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            item {
                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding16)))
            }
            item {
                OutlinedTextField(
                    value = viewModel.targetAmountString,
                    onValueChange = viewModel::onTargetAmountInput,
                    label = { Text(text = stringResource(id = R.string.savingAmount)) },
                    placeholder = { Text(text = stringResource(id = R.string.savingAmount)) },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        capitalization = KeyboardCapitalization.None,
                        keyboardType = KeyboardType.Number
                    )
                )
            }
            item {
                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding16)))
            }
            item {
                Text(
                    text = stringResource(id = R.string.savingPocketDate),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            item {
                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding16)))
            }
            item {
                val selectedDateMillis = viewModel.selectedDateMillis
                val formattedDate = selectedDateMillis?.let {
                    java.text.SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
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
                                viewModel.onDateSelected(millis)
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
                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding16)))
            }
            item {
                Text(
                    text = stringResource(id = R.string.savingPocketDescription),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            item {
                Spacer(modifier = modifier.height(dimensionResource(id = R.dimen.padding16)))
            }
            item {
                OutlinedTextField(
                    value = viewModel.descriptionString,
                    onValueChange = viewModel::onDescriptionInput,
                    label = { Text(text = stringResource(id = R.string.savingPocketDescription)) },
                )
            }
            item {
                Spacer(modifier = modifier.height(dimensionResource(id = R.dimen.padding16)))
            }
            item {
                Button(
                    onClick = {
                        viewModel.onSubmit()
                        navController.navigate(Screen.SavingPocketsScreen.route)
                              },
                    modifier = modifier
                        .fillMaxWidth(2f / 3f)
                        .aspectRatio(5f)
                ) {
                    Text(text = stringResource(id = R.string.add))
                }

            }
        }
    }
}

@Preview
@Composable
fun SavingPocketEditorScreenPreview(){

    val viewModel=SavingPocketsViewModel(repository = RepositoryImpl())
    val navController= rememberNavController()
    CoinTrailTheme {
        SavingPocketEditorScreen(
            viewModel = viewModel,
            navController = navController
        )
    }
}
