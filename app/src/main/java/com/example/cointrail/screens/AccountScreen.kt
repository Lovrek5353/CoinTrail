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
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.example.cointrail.R
import com.example.cointrail.navigation.Screen
import com.example.cointrail.viewModels.LoginViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(
    viewModel: LoginViewModel,
    navController: NavController,
    onNameEditClick: () -> Unit,
) {
    val user = viewModel.localUser.collectAsState().value

    if (user == null) {
        // User is null: Show a placeholder UI or logged-out state
        // Optionally you can navigate away, or just show message
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = stringResource(id = R.string.no_user_found))
        }
        return // Early return prevents any further rendering with a null user
    }

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
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.backIcon),
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
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            item {
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding24)))
                Box(
                    modifier = Modifier
                        .size(dimensionResource(R.dimen.padding96))
                        .clip(MaterialTheme.shapes.extraLarge)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = stringResource(R.string.profile_icon),
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(dimensionResource(R.dimen.padding56))
                    )
                }
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding16)))
            }

            item {
                Text(
                    text = stringResource(R.string.profile),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.fillMaxWidth()
                        .padding(
                            start = dimensionResource(R.dimen.padding16),
                            top = dimensionResource(R.dimen.padding8),
                            bottom = dimensionResource(R.dimen.padding8)
                        )
                )
            }

            item {
                Divider(
                    modifier = Modifier.padding(
                        start = dimensionResource(R.dimen.padding16),
                        end = dimensionResource(R.dimen.padding16)
                    ),
                    thickness = dimensionResource(R.dimen.padding1),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                )
            }

            item {
                Text(
                    text = stringResource(R.string.name),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.fillMaxWidth()
                        .padding(
                            start = dimensionResource(R.dimen.padding16),
                            top = dimensionResource(R.dimen.padding16)
                        )
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth()
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
                    modifier = Modifier.fillMaxWidth()
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
                    modifier = Modifier.fillMaxWidth()
                        .padding(start = dimensionResource(R.dimen.padding16), bottom = dimensionResource(R.dimen.padding16))
                )
            }

            item {
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding32)))
            }

            item {
                Button(
                    onClick = {
                        viewModel.signOut()
                        navController.navigate(Screen.LoginScreen.route) {
                            popUpTo(Screen.AccountScreen.route) { inclusive = true }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = dimensionResource(R.dimen.padding16))
                ) {
                    Text(text = stringResource(R.string.sign_out))
                }
            }

            item {
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding16)))
            }

            item {
                Button(
                    onClick = {
                        viewModel.deleteData(userID = user.id)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    ),
                    modifier = Modifier.fillMaxWidth().padding(horizontal = dimensionResource(R.dimen.padding16))
                ) {
                    Text(text = stringResource(R.string.delete_data))
                }
            }

            item {
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding32)))
            }
        }
    }
}
