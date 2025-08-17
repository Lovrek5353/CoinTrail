package com.example.cointrail.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.navigation.compose.rememberNavController
import com.example.cointrail.R
import com.example.cointrail.composables.CategoryDropDownList
import com.example.cointrail.composables.DatePickerModal
import com.example.cointrail.data.Category
import com.example.cointrail.data.dummyCategories
import com.example.cointrail.data.enums.TransactionType
import com.example.cointrail.repository.RepositoryImpl
import com.example.cointrail.ui.theme.CoinTrailTheme
import com.example.cointrail.viewModels.MainViewModel
import com.example.cointrail.viewModels.TabsViewModel
import kotlinx.coroutines.flow.collectLatest

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionEditorScreen(
    viewModel: MainViewModel,
    navController: NavController,
    modifier: Modifier = Modifier
){
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    val categoryList by viewModel.fetchCategories().collectAsState(initial= emptyList())

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is MainViewModel.UiEvent.SubmissionSuccess -> {
                    navController.popBackStack() // or navigate("destination_screen")
                }
                is MainViewModel.UiEvent.ShowSnackbar -> {
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
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        )  {
            item{
                Text(
                    text = stringResource(id = R.string.transactionAmount),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground)
            }
            item{
                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding16)))
            }
            item{
                OutlinedTextField(
                    value = viewModel.amountInputString,
                    onValueChange = viewModel::onAmountInputChange,
                    label = { Text(text = stringResource(id = R.string.transactionAmount)) },
                    placeholder = { Text(text = stringResource(id = R.string.transactionAmount)) },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        capitalization = KeyboardCapitalization.None,
                        keyboardType = KeyboardType.Number
                    ),
                    modifier = Modifier.fillMaxWidth() // No explicit height needed
                )

            }
            item{
                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding16)))
            }
            item{
                Text(
                    text = stringResource(id = R.string.transactionDate),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground)
            }
            item{
                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding16)))
            }
            item {
                val selectedDateMillis = viewModel.transaction.date?.toDate()?.time
                val formattedDate = selectedDateMillis?.let {
                    java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault())
                        .format(java.util.Date(it))
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
                Text(
                    text = stringResource(id = R.string.transactionCategory),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            item {
                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding16)))
            }
            item{
                CategoryDropDownList(
                    items = categoryList,
                    selectedItem = selectedCategory,
                    onCategorySelected = { category ->
                        selectedCategory = category
                        viewModel.onCategorySelected(category)
                    }
                )
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
                    value = viewModel.descriptionInputString,
                    onValueChange = viewModel::onDescriptionChange,
                    label = { Text(text = stringResource(id = R.string.transactionDescription)) },
                )
            }
            item{
                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding16)))
            }
            item{
                Text(
                    text = stringResource(id = R.string.transactionType),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground)
            }
            item {
                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding16)))
            }
            item{
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(dimensionResource(id = R.dimen.padding16)),
                    contentAlignment = Alignment.Center
                ) {
                    val selectedType = viewModel.transaction.type

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(id = R.string.income),
                            modifier = Modifier.padding(end = dimensionResource(id = R.dimen.padding16))
                        )
                        RadioButton(
                            selected = selectedType == TransactionType.DEPOSIT,
                            onClick = { viewModel.transactionTypeSelected(TransactionType.DEPOSIT) }
                        )
                        Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.padding16)))
                        Text(
                            text = stringResource(id = R.string.expense),
                            modifier = Modifier.padding(start = dimensionResource(id = R.dimen.padding16))
                        )
                        RadioButton(
                            selected = selectedType == TransactionType.WITHDRAWAL,
                            onClick = { viewModel.transactionTypeSelected(TransactionType.WITHDRAWAL) }
                        )
                    }
                }


                //Dropdown for transaction type or radio buttons
            }
            item{
                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding16)))
            }
            item {
                Button(onClick = { viewModel.onSubmit() })
                {
                    Text(text = stringResource(id = R.string.add))
                }
            }
        }
    }

}

@Preview
@Composable
fun TransactionEditorScreenPreview(){
//    val viewModel=MainViewModel(repository = RepositoryImpl())
//    val navController= rememberNavController()
//    CoinTrailTheme {
//        TransactionEditorScreen(viewModel = viewModel, navController = navController)
//    }
}