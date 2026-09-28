package com.latechhub.finance

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModelProvider
import com.latechhub.finance.data.local.TokenStorage
import com.latechhub.finance.data.remote.AuthRepository
import com.latechhub.finance.ui.navigation.FinanceNavHost
import com.latechhub.finance.ui.session.SessionState
import com.latechhub.finance.ui.session.SessionViewModel
import com.latechhub.finance.ui.session.SessionViewModelFactory
import com.latechhub.finance.ui.theme.FinanceTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repository = AuthRepository(
            TokenStorage(applicationContext)
        )

        val viewModel = ViewModelProvider(
            this,
            SessionViewModelFactory(repository)
        )[SessionViewModel::class.java]

        setContent {
            FinanceTheme {
                SessionRoot(viewModel)
            }
        }
    }
}

@Composable
fun SessionRoot(
    viewModel: SessionViewModel
) {
    val state by viewModel.state.collectAsState()

    when (val currentState = state) {
        SessionState.Loading -> {
            Text("Checking session...")
        }

        SessionState.Unauthenticated -> {
            Text("Login")
        }

        is SessionState.Authenticated -> {
            FinanceNavHost()
        }

        is SessionState.Error -> {
            Text("Session error: ${currentState.message}")
        }
    }
}



