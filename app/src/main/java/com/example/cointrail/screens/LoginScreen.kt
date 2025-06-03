package com.example.cointrail.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.example.cointrail.R
import com.example.cointrail.composables.SignInWithGoogleButton
import com.example.cointrail.navigation.Screen
import com.example.cointrail.ui.theme.CoinTrailTheme
import com.example.cointrail.viewModels.LoginViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel,
    navController: NavController,
) {

    var emailText by remember { mutableStateOf("") }
    var passwordText by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.surface
                    )
                )
            )
            .padding(dimensionResource(R.dimen.padding16)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.icon),
            contentDescription = stringResource(id = R.string.appIcon),
            modifier = Modifier
                .fillMaxWidth(0.3f)
                .aspectRatio(1f), // Square aspect ratio
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.tertiary)
        )
        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding16)))
        Text(
            text = stringResource(id = R.string.welcomeBack),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.secondary
        )
        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding16)))

       //Email input
        OutlinedTextField(
            value = emailText,
            onValueChange = { emailText = it },
            label = { Text(text = stringResource(id = R.string.email)) },
            placeholder = { Text(text = stringResource(id = R.string.enterEmail)) },
            keyboardOptions = KeyboardOptions.Default.copy(
                capitalization = KeyboardCapitalization.None,
                keyboardType = KeyboardType.Email
            ),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = stringResource(id = R.string.emailIcon),
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                unfocusedLabelColor = MaterialTheme.colorScheme.onSurface,
                cursorColor = MaterialTheme.colorScheme.secondary,
                focusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurface
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimensionResource(id = R.dimen.padding16))
        )

        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding16)))

        //Password input field

        OutlinedTextField(
            value = passwordText,
            onValueChange = {passwordText=it},
            label = {Text(text = stringResource(id = R.string.password))},
            placeholder = {Text(text = stringResource(id = R.string.enterPassword))},
            keyboardOptions = KeyboardOptions.Default.copy(
                capitalization = KeyboardCapitalization.None,
                keyboardType = KeyboardType.Password
            ),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = stringResource(id = R.string.passwordBox),
                    tint = MaterialTheme.colorScheme.primary)},
            visualTransformation = PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(
                    onClick = {
                        //try to login and if successfully, navigate to next display
                   coroutineScope.launch {
                         try {
                                val result = viewModel.emailLogin(emailText, passwordText).first()
                                if (result.isSuccess) {
                                    Log.d("Login", "Successful login")
                                    navController.navigate(Screen.MainScreen.route)
                                } else {
                                    Log.d("Login", "Login failed")
                                }
                            } catch (e: Exception) {
                                Log.e("LoginError", "Error logging in", e)
                            }
                        }
                    },
                    enabled = emailText.isNotEmpty() && passwordText.isNotEmpty()
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = stringResource(id = R.string.emailIcon),
                    )
                }
            },
            modifier=Modifier
                .fillMaxWidth()
                .padding(horizontal = dimensionResource(id = R.dimen.padding16)),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                unfocusedLabelColor = MaterialTheme.colorScheme.onSurface,
                cursorColor = MaterialTheme.colorScheme.secondary,
                focusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurface
            )
        )
        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding16)))

        Row {
            Text(
                text = stringResource(id = R.string.forgotPassword),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(dimensionResource(id = R.dimen.padding16))
            )
            TextButton(
                onClick = {
                    //navigate to password reset
                },

            ) {
                Text(
                    text = stringResource(id = R.string.reset),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding16)))
        SignInWithGoogleButton(onClick = {}, modifier = Modifier)
        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding16)))

        Row{
            Text(
                text= stringResource(id=R.string.newUser),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(dimensionResource(id = R.dimen.padding16))
            )
            TextButton(
                onClick = {
                    //navigate to signUP screen
                })
            {
                Text(
                    text = stringResource(id = R.string.signUp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}


@Preview
@Composable
fun LoginScreenPreview(){
    CoinTrailTheme {
        //LoginScreen()
    }

}