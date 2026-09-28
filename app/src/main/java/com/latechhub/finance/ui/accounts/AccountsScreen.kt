package com.latechhub.finance.ui.accounts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.latechhub.finance.data.remote.FinancialAccount
import java.util.Locale

@Composable
fun AccountsScreen(
    accounts: List<FinancialAccount>
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Text(
            text = "Accounts",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "${accounts.count { it.isActive }} active of ${accounts.size} accounts",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                items = accounts,
                key = { it.id }
            ) { account ->
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = account.name,
                            style = MaterialTheme.typography.titleMedium
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "${account.type} • ${account.currency}",
                            style = MaterialTheme.typography.bodySmall
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = String.format(
                                Locale.US,
                                "%s %,.2f",
                                account.currency,
                                account.balance
                            ),
                            style = MaterialTheme.typography.titleLarge
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = if (account.isActive) "Active" else "Inactive",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}
