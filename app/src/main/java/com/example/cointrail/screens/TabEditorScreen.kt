package com.example.cointrail.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import com.example.cointrail.data.dummyCategories
import com.example.cointrail.ui.theme.CoinTrailTheme

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TabEditorScreen() {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.newTab),
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
            verticalArrangement = Arrangement.Center, // Centers items vertically
            horizontalAlignment = Alignment.CenterHorizontally // Centers
        ) {
            item {
                Text(
                    text = stringResource(id = R.string.tabAmount),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            item {
                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding16)))
            }
            item {
                OutlinedTextField(
                    value = "",
                    onValueChange = { /* Handle value change */ },
                    label = { Text(text = stringResource(id = R.string.tabAmount)) },
                    placeholder = { Text(text = stringResource(id = R.string.tabAmount)) },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        capitalization = KeyboardCapitalization.None,
                        keyboardType = KeyboardType.Number
                    ),
                    modifier = Modifier
                        .height(dimensionResource(id = R.dimen.padding16))
                )
            }
            item {
                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding16)))
            }
            item {
                Text(
                    text = stringResource(id = R.string.transactionDate),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            item {
                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding16)))
            }
            item {
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
            item {
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
            item {
                OutlinedTextField(
                    value = "",
                    onValueChange = { /* Handle value change */ },
                    label = { Text(text = stringResource(id = R.string.transactionDescription)) },
                )
            }
            item {
                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding16)))
            }
            item {
                Button(
                    onClick = { /* Handle save click */ },
                    modifier = Modifier
                        .fillMaxWidth(2f / 3f) // 2/3 of the parent width
                        .aspectRatio(5f) // width:height ratio, so height = width/3
                ) {
                    Text(text = stringResource(id = R.string.add))
                }

            }
        }
    }
}


@Preview
@Composable

fun TabEditorScreenPreview() {
    CoinTrailTheme {
        TabEditorScreen()
    }
}