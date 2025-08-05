package com.example.cointrail.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.example.cointrail.R
import com.example.cointrail.navigation.Screen
import com.example.cointrail.repository.RepositoryImpl
import com.example.cointrail.ui.theme.CoinTrailTheme
import com.example.cointrail.viewModels.LoginViewModel
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(
    viewModel: LoginViewModel,
    modifier: Modifier =Modifier,
    onForgotPasswordClick: () -> Unit
    ){
    val snackbarHostState=remember{SnackbarHostState()}
    LaunchedEffect(Unit) {
        viewModel.eventFlow.collectLatest {
            when (it) {
                is LoginViewModel.UiEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(it.message)
                }
                LoginViewModel.UiEvent.ForgotPasswordSuccess -> {
                    onForgotPasswordClick()
                }
                LoginViewModel.UiEvent.SignUpSuccess -> {}
                else -> {}
            }
        }
    }

    Scaffold(
      topBar = {
          CenterAlignedTopAppBar(
              title = { Text(text = stringResource(R.string.forgotYourPassword)) }
          )
      }
    ) {innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = dimensionResource(R.dimen.padding24)),
            verticalArrangement = Arrangement.Top, // Items start at the top
            horizontalAlignment = Alignment.CenterHorizontally // Items are centered horizontally
        ) {
            item {
                Text(
                    text = "We'll send you a link to the email\naddress you signed up with.",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(dimensionResource(R.dimen.padding16)),
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center
                )
            }
            item {
                Spacer(modifier=modifier.height(dimensionResource(R.dimen.padding24)))
            }
            item {
                OutlinedTextField(
                    value = viewModel.forgotEmail,
                    onValueChange = viewModel::onForgotEmailChange,
                    label = { Text("Email") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = "Email"
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.None,
                        keyboardType = KeyboardType.Email
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = dimensionResource(R.dimen.padding8)),
                    singleLine = true
                )
            }
            item {
                Spacer(modifier=modifier.height(dimensionResource(R.dimen.padding24)))
            }
            item {
                Button(
                    onClick = {
                        viewModel.sendPasswordResetEmail()
                        onForgotPasswordClick
                    },
                    modifier = modifier
                        .fillMaxWidth(2f / 3f)
                        .aspectRatio(5f)
                ) {
                    Text(text = stringResource(id = R.string.sendLink))
                }

            }
        }

    }
}

@Preview
@Composable
fun ForgotPasswordScreenPreview(){
//    val viewModel=LoginViewModel(repository = RepositoryImpl())
//    CoinTrailTheme {
//        ForgotPasswordScreen(
//            viewModel = viewModel,
//            onForgotPasswordClick = {}
//        )
//
//    }
}