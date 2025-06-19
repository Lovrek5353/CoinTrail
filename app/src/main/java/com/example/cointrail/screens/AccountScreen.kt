package com.example.cointrail.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.cointrail.R
import com.example.cointrail.data.User
import com.example.cointrail.data.dummyUser
import com.example.cointrail.ui.theme.CoinTrailTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(
    user: User,
    onNameEditClick: () -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.profile),
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
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding24)))
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = stringResource(R.string.profile_icon),
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(dimensionResource(R.dimen.padding96))
                        .background(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            shape = MaterialTheme.shapes.extraLarge
                        )
                        .padding(dimensionResource(R.dimen.padding16))
                )
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding16)))
            }
            item {
                Text(
                    text = stringResource(R.string.profile),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = dimensionResource(R.dimen.padding16),
                            top = dimensionResource(R.dimen.padding8),
                            bottom = dimensionResource(R.dimen.padding8)
                        )
                )
            }
            item {
                HorizontalDivider(
                    modifier = Modifier.padding(
                        start = dimensionResource(R.dimen.padding16),
                        end = dimensionResource(R.dimen.padding16)
                    ),
                    thickness = dimensionResource(R.dimen.padding1),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                )
            }
            item {
                Text(
                    text = stringResource(R.string.name),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = dimensionResource(R.dimen.padding16),
                            top = dimensionResource(R.dimen.padding16)
                        )
                )
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = dimensionResource(R.dimen.padding16),
                            end = dimensionResource(R.dimen.padding16),
                            bottom = dimensionResource(R.dimen.padding16)
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = user.name,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    IconButton(
                        onClick = onNameEditClick,
                        modifier = Modifier.size(dimensionResource(R.dimen.padding24))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = stringResource(R.string.edit_name),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
            item {
                Text(
                    text = stringResource(R.string.email),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = dimensionResource(R.dimen.padding16),
                            top = dimensionResource(R.dimen.padding8)
                        )
                )
            }
            item {
                Text(
                    text = user.email,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = dimensionResource(R.dimen.padding16),
                            bottom = dimensionResource(R.dimen.padding16)
                        )
                )
            }
        }
        // Future: add payment information and gamification section here
    }
}

@Preview
@Composable
fun AccountScreenPreview() {
    CoinTrailTheme {
        AccountScreen(
            user = dummyUser,
            onNameEditClick = { /* No-op for preview */ }
        )
    }
}
