package com.latechhub.finance.ui.transactions

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.latechhub.finance.data.remote.TransactionItem
import com.latechhub.finance.ui.dashboard.TransactionState
import com.latechhub.finance.ui.dashboard.TransactionViewModel
import java.util.Locale

@Composable
fun TransactionsScreen(
    transactionViewModel: TransactionViewModel,
    onTransactionClick: (TransactionItem) -> Unit
) {
    val state by transactionViewModel.state.collectAsState()

    when (val currentState = state) {
        TransactionState.Loading -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is TransactionState.Error -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Unable to load transactions",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = currentState.message,
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        transactionViewModel.loadTransactions()
                    }
                ) {
                    Text("Retry")
                }
            }
        }

        is TransactionState.Success -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                Text(
                    text = "Transactions",
                    style = MaterialTheme.typography.headlineMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "${currentState.total} transactions",
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = currentState.transactions,
                        key = { it.id }
                    ) { transaction ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onTransactionClick(transaction)
                                }
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = transaction.description
                                        ?: transaction.category
                                        ?: transaction.type,
                                    style = MaterialTheme.typography.titleSmall
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = transaction.account.name,
                                    style = MaterialTheme.typography.bodySmall
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = "${transaction.account.currency} ${
                                        if (transaction.type == "INCOME") "+" else "-"
                                    }${String.format(
                                        Locale.US,
                                        "%,.2f",
                                        transaction.amount
                                    )}",
                                    style = MaterialTheme.typography.titleMedium
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = transaction.transactionDate
                                        .replace("T", " ")
                                        .take(16),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        modifier = Modifier.weight(1f),
                        enabled = currentState.page > 1,
                        onClick = {
                            transactionViewModel.loadPage(
                                currentState.page - 1
                            )
                        }
                    ) {
                        Text("Previous")
                    }

                    Button(
                        modifier = Modifier.weight(1f),
                        enabled = currentState.page < currentState.totalPages,
                        onClick = {
                            transactionViewModel.loadPage(
                                currentState.page + 1
                            )
                        }
                    ) {
                        Text("Next")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Page ${currentState.page} of ${currentState.totalPages}",
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

