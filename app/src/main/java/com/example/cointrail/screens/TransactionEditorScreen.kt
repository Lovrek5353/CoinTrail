package com.example.cointrail.screens

import android.annotation.SuppressLint
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import com.example.cointrail.R
import com.example.cointrail.composables.CategoryDropDownList
import com.example.cointrail.composables.DatePickerModal
import com.example.cointrail.data.dummyCategories
import com.example.cointrail.ui.theme.CoinTrailTheme

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionEditorScreen(){
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
                        onClick = { /* Handle navigation icon click */ }  //navigate to screen before
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
                    value = "",
                    onValueChange = { /* Handle value change */ },
                    label = { Text(text = stringResource(id = R.string.transactionAmount)) },
                    placeholder = { Text(text = stringResource(id = R.string.transactionAmount)) },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        capitalization = KeyboardCapitalization.None,
                        keyboardType = KeyboardType.Number
                    ),
                    modifier = Modifier
                        .height(dimensionResource(id = R.dimen.padding16))
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
            item{
                //Doraditi
                //DatePickerModal()

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
                CategoryDropDownList(dummyCategories) //pass the real category list
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
                    value = "",
                    onValueChange = { /* Handle value change */ },
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
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(id = R.string.income),
                            modifier = Modifier.padding(end = dimensionResource(id = R.dimen.padding16))
                        )
                        RadioButton(
                            selected = true,
                            onClick = { /* Handle selection */ }
                        )
                        Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.padding16)))
                        Text(
                            text = stringResource(id = R.string.expense),
                            modifier = Modifier.padding(start = dimensionResource(id = R.dimen.padding16))
                        )
                        RadioButton(
                            selected = false,
                            onClick = { /* Handle selection */ }
                        )
                    }
                }


                //Dropdown for transaction type or radio buttons
            }
            item{
                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding16)))
            }
            item {
                Button(onClick = { /* Handle save click */ })
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
    CoinTrailTheme {
        TransactionEditorScreen()
    }
}