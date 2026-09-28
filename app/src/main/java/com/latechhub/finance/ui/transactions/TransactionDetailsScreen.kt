package com.latechhub.finance.ui.transactions

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.latechhub.finance.data.remote.TransactionItem
import java.util.Locale

@Composable
fun TransactionDetailsScreen(
    transaction: TransactionItem
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Text(
            text = "Transaction Details",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = transaction.description
                        ?: transaction.category
                        ?: transaction.type,
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "${transaction.account.currency} ${
                        if (transaction.type == "INCOME") "+" else "-"
                    }${String.format(
                        Locale.US,
                        "%,.2f",
                        transaction.amount
                    )}",
                    style = MaterialTheme.typography.headlineSmall
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Account: ${transaction.account.name}",
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Type: ${transaction.type}",
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Category: ${transaction.category ?: "Not specified"}",
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Source: ${transaction.source}",
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Date: ${
                        transaction.transactionDate
                            .replace("T", " ")
                            .take(16)
                    }",
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Reference: ${transaction.reference ?: "Not available"}",
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "External Reference: ${
                        transaction.externalReference ?: "Not available"
                    }",
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Imported: ${
                        transaction.importedAt
                            ?.replace("T", " ")
                            ?.take(16)
                            ?: "Not imported"
                    }",
                    style = MaterialTheme.typography.bodyMedium
                )

                if (transaction.transferAccountId != null) {
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Transfer Account ID: ${transaction.transferAccountId}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}
