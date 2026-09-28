package com.latechhub.finance.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.latechhub.finance.ui.session.SessionState
import com.latechhub.finance.ui.session.SessionViewModel

@Composable
fun LoginScreen(
    viewModel: SessionViewModel,
    state: SessionState
) {
    var email by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }

    val isLoggingIn = state is SessionState.LoggingIn
    val errorMessage = (state as? SessionState.Error)?.message

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "LaTech Finance",
            style = MaterialTheme.typography.headlineMedium
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp),
            label = {
                Text("Email")
            },
            singleLine = true,
            enabled = !isLoggingIn,
            colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                focusedTextColor = com.latechhub.finance.ui.theme.LaTechBlack,
                unfocusedTextColor = com.latechhub.finance.ui.theme.LaTechBlack,
                focusedLabelColor = com.latechhub.finance.ui.theme.LaTechBlue,
                unfocusedLabelColor = com.latechhub.finance.ui.theme.LaTechBlack,
                cursorColor = com.latechhub.finance.ui.theme.LaTechBlue,
                focusedBorderColor = com.latechhub.finance.ui.theme.LaTechBlue,
                unfocusedBorderColor = com.latechhub.finance.ui.theme.LaTechBlueDark
            )
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            label = {
                Text("Password")
            },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            enabled = !isLoggingIn,
            colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                focusedTextColor = com.latechhub.finance.ui.theme.LaTechBlack,
                unfocusedTextColor = com.latechhub.finance.ui.theme.LaTechBlack,
                focusedLabelColor = com.latechhub.finance.ui.theme.LaTechBlue,
                unfocusedLabelColor = com.latechhub.finance.ui.theme.LaTechBlack,
                cursorColor = com.latechhub.finance.ui.theme.LaTechBlue,
                focusedBorderColor = com.latechhub.finance.ui.theme.LaTechBlue,
                unfocusedBorderColor = com.latechhub.finance.ui.theme.LaTechBlueDark
            )
        )

        if (errorMessage != null) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
            )
        }

        Button(
            onClick = {
                viewModel.login(
                    email = email.trim(),
                    password = password
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp),
            enabled = !isLoggingIn &&
                email.isNotBlank() &&
                password.isNotBlank()
        ) {
            if (isLoggingIn) {
                CircularProgressIndicator()
            } else {
                Text("Login")
            }
        }
    }
}



