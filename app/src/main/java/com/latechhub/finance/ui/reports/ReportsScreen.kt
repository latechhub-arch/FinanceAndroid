package com.latechhub.finance.ui.reports

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
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ReportsScreen(
    viewModel: ReportViewModel
) {
    val state by viewModel.state.collectAsState()

    when (val currentState = state) {
        ReportState.Loading -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(16.dp))
                Text("Loading reports...")
            }
        }

        is ReportState.Error -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Unable to load reports",
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(currentState.message)
            }
        }

        is ReportState.Success -> {
            ReportsContent(
                summary = currentState.summary,
                expensesByCategory = currentState.expensesByCategory,
                accounts = currentState.accounts
            )
        }
    }
}

@Composable
private fun ReportsContent(
    summary: com.latechhub.finance.data.remote.ReportSummary,
    expensesByCategory: List<com.latechhub.finance.data.remote.ExpenseByCategory>,
    accounts: List<com.latechhub.finance.data.remote.AccountReport>
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Financial Reports",
                style = MaterialTheme.typography.headlineMedium
            )
        }

        item {
            SummaryCard(summary)
        }

        item {
            Text(
                text = "Expenses by Category",
                style = MaterialTheme.typography.titleLarge
            )
        }

        if (expensesByCategory.isEmpty()) {
            item {
                Text("No expense data available.")
            }
        } else {
            items(expensesByCategory) { item ->
                CategoryReportCard(item)
            }
        }

        item {
            Text(
                text = "Activity by Account",
                style = MaterialTheme.typography.titleLarge
            )
        }

        if (accounts.isEmpty()) {
            item {
                Text("No account activity available.")
            }
        } else {
            items(accounts) { account ->
                AccountReportCard(account)
            }
        }
    }
}

@Composable
private fun SummaryCard(
    summary: com.latechhub.finance.data.remote.ReportSummary
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ReportRow("Total Income", summary.totalIncome)
            ReportRow("Total Expenses", summary.totalExpenses)
            ReportRow("Net Cash Flow", summary.netCashFlow)

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Fuliza Activity",
                style = MaterialTheme.typography.titleMedium
            )

            ReportRow("Fuliza Borrowed", summary.fulizaBorrowing)
            ReportRow("Fuliza Repaid", summary.fulizaRepayment)
            ReportRow("Fuliza Charges", summary.fulizaCharges)

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Transactions: ${summary.transactionCount}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun CategoryReportCard(
    item: com.latechhub.finance.data.remote.ExpenseByCategory
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = item.category,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text("Amount: ${formatAmount(item.totalAmount)}")
            Text("Transactions: ${item.transactionCount}")
        }
    }
}

@Composable
private fun AccountReportCard(
    account: com.latechhub.finance.data.remote.AccountReport
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = account.accountName,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(6.dp))

            ReportRow("Income", account.totalIncome)
            ReportRow("Expenses", account.totalExpenses)
            ReportRow("Net Cash Flow", account.netCashFlow)

            Text("Transactions: ${account.transactionCount}")
        }
    }
}

@Composable
private fun ReportRow(
    label: String,
    amount: Double
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label)
        Text(formatAmount(amount))
    }
}

private fun formatAmount(amount: Double): String {
    return "KES ${"%,.2f".format(amount)}"
}
