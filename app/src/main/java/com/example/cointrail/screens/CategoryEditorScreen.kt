package com.example.cointrail.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import com.example.cointrail.R
import com.example.cointrail.ui.theme.CoinTrailTheme

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryEditorScreen() {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.newCategory),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { /* Handle navigation icon click */ }
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
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = dimensionResource(id = R.dimen.padding16)),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        )  {
            item {
                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding16)))
            }
            item {
                Text(
                    text = stringResource(id = R.string.categoryName),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(bottom = dimensionResource(id = R.dimen.padding8))
                )
            }
            item {
                OutlinedTextField(
                    value = "",
                    onValueChange = { /* Handle value change */ },
                    label = { Text(text = stringResource(id = R.string.categoryName)) },
                    placeholder = { Text(text = stringResource(id = R.string.categoryName)) },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        capitalization = KeyboardCapitalization.None,
                        keyboardType = KeyboardType.Text
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(dimensionResource(id = R.dimen.padding56))
                )
            }
            item {
                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding16)))
            }
            item {
                Text(
                    text = stringResource(id = R.string.categoryDescription),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(bottom = dimensionResource(id = R.dimen.padding8))
                )
            }
            item {
                OutlinedTextField(
                    value = "",
                    onValueChange = { /* Handle value change */ },
                    label = { Text(text = stringResource(id = R.string.categoryDescription)) },
                    placeholder = { Text(text = stringResource(id = R.string.categoryDescription)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(dimensionResource(id = R.dimen.padding112))
                )
            }
            item {
                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding24)))
            }
            item {
                Button(
                    onClick = { /* Handle save click */ },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = stringResource(id = R.string.add))
                }
            }
        }
    }
}

@Preview
@Composable
fun CategoryEditorScreenPreview() {
    CoinTrailTheme {
        CategoryEditorScreen()
    }
}
