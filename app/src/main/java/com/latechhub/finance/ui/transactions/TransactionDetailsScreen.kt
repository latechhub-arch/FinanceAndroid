package com.latechhub.finance.ui.transactions

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.latechhub.finance.data.remote.TransactionItem
import com.latechhub.finance.data.remote.FinancialAccount
import com.latechhub.finance.ui.dashboard.TransactionViewModel
import java.util.Locale

@Composable
fun TransactionDetailsScreen(
    transaction: TransactionItem,
    viewModel: TransactionViewModel,
    accounts: List<FinancialAccount>,
    onTransactionUpdated: (TransactionItem) -> Unit
) {
    var showEditForm by remember { mutableStateOf(false) }

    if (showEditForm) {
        EditTransactionForm(
            transaction = transaction,
            viewModel = viewModel,
            accounts = accounts,
            onCancel = { showEditForm = false },
            onUpdated = { success, _, updatedTransaction ->
                if (success && updatedTransaction != null) {
                    onTransactionUpdated(updatedTransaction)
                    showEditForm = false
                }
            }
        )
        return
    }

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

        Spacer(modifier = Modifier.height(16.dp))

        androidx.compose.material3.Button(
            onClick = { showEditForm = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Edit Transaction")
        }
    }
}


@Composable
private fun EditTransactionForm(
    transaction: TransactionItem,
    viewModel: TransactionViewModel,
    accounts: List<FinancialAccount>,
    onCancel: () -> Unit,
    onUpdated: (Boolean, String, TransactionItem?) -> Unit
) {
    var accountId by remember { mutableStateOf(transaction.accountId) }
    var type by remember { mutableStateOf(transaction.type) }
    var category by remember { mutableStateOf(transaction.category ?: "") }
    var amount by remember { mutableStateOf(transaction.amount.toString()) }
    var description by remember { mutableStateOf(transaction.description ?: "") }
    var transactionDate by remember { mutableStateOf(transaction.transactionDate) }
    var reference by remember { mutableStateOf(transaction.reference ?: "") }
    var transferAccountId by remember { mutableStateOf(transaction.transferAccountId ?: "") }
    var expandedAccount by remember { mutableStateOf(false) }
    var expandedType by remember { mutableStateOf(false) }
    var expandedTransferAccount by remember { mutableStateOf(false) }
    var saving by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Edit Transaction",
            style = MaterialTheme.typography.headlineSmall
        )

        Text(
            text = "Account",
            style = MaterialTheme.typography.labelLarge
        )

        Button(
            onClick = { expandedAccount = true },
            modifier = Modifier.fillMaxWidth(),
            enabled = !saving
        ) {
            Text(
                accounts.firstOrNull { it.id == accountId }?.name
                    ?: "Select account"
            )
        }

        DropdownMenu(
            expanded = expandedAccount,
            onDismissRequest = { expandedAccount = false }
        ) {
            accounts.forEach { account ->
                DropdownMenuItem(
                    text = { Text(account.name) },
                    onClick = {
                        accountId = account.id
                        expandedAccount = false
                    }
                )
            }
        }

        Text(
            text = "Type",
            style = MaterialTheme.typography.labelLarge
        )

        Button(
            onClick = { expandedType = true },
            modifier = Modifier.fillMaxWidth(),
            enabled = !saving
        ) {
            Text(type)
        }

        DropdownMenu(
            expanded = expandedType,
            onDismissRequest = { expandedType = false }
        ) {
            listOf("INCOME", "EXPENSE", "TRANSFER").forEach { transactionType ->
                DropdownMenuItem(
                    text = { Text(transactionType) },
                    onClick = {
                        type = transactionType
                        expandedType = false
                    }
                )
            }
        }

        OutlinedTextField(
            value = category,
            onValueChange = { category = it },
            label = { Text("Category") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !saving,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedLabelColor = Color.Black,
                unfocusedLabelColor = Color.Black,
                cursorColor = Color.Black,
                focusedBorderColor = Color.Black,
                unfocusedBorderColor = Color.Gray
            )
        )

        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it },
            label = { Text("Amount") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !saving,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedLabelColor = Color.Black,
                unfocusedLabelColor = Color.Black,
                cursorColor = Color.Black,
                focusedBorderColor = Color.Black,
                unfocusedBorderColor = Color.Gray
            )
        )

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !saving,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedLabelColor = Color.Black,
                unfocusedLabelColor = Color.Black,
                cursorColor = Color.Black,
                focusedBorderColor = Color.Black,
                unfocusedBorderColor = Color.Gray
            )
        )

        OutlinedTextField(
            value = transactionDate,
            onValueChange = { transactionDate = it },
            label = { Text("Transaction Date") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !saving,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedLabelColor = Color.Black,
                unfocusedLabelColor = Color.Black,
                cursorColor = Color.Black,
                focusedBorderColor = Color.Black,
                unfocusedBorderColor = Color.Gray
            )
        )

        OutlinedTextField(
            value = reference,
            onValueChange = { reference = it },
            label = { Text("Reference") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !saving,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedLabelColor = Color.Black,
                unfocusedLabelColor = Color.Black,
                cursorColor = Color.Black,
                focusedBorderColor = Color.Black,
                unfocusedBorderColor = Color.Gray
            )
        )

        if (type == "TRANSFER") {
            Text(
                text = "Transfer Account",
                style = MaterialTheme.typography.labelLarge
            )

            Button(
                onClick = { expandedTransferAccount = true },
                modifier = Modifier.fillMaxWidth(),
                enabled = !saving
            ) {
                Text(
                    accounts.firstOrNull { it.id == transferAccountId }?.name
                        ?: "Select transfer account"
                )
            }

            DropdownMenu(
                expanded = expandedTransferAccount,
                onDismissRequest = { expandedTransferAccount = false }
            ) {
                accounts.filter { it.id != accountId }.forEach { account ->
                    DropdownMenuItem(
                        text = { Text(account.name) },
                        onClick = {
                            transferAccountId = account.id
                            expandedTransferAccount = false
                        }
                    )
                }
            }
        }

        errorMessage?.let { message ->
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onCancel,
                modifier = Modifier.weight(1f),
                enabled = !saving
            ) {
                Text("Cancel")
            }

            Button(
                onClick = {
                    val parsedAmount = amount.toDoubleOrNull()

                    if (accountId.isBlank()) {
                        errorMessage = "Please select an account."
                        return@Button
                    }

                    if (parsedAmount == null || parsedAmount <= 0.0) {
                        errorMessage = "Amount must be a valid positive number."
                        return@Button
                    }

                    if (type == "TRANSFER" && transferAccountId.isBlank()) {
                        errorMessage = "Please select a transfer account."
                        return@Button
                    }

                    saving = true
                    errorMessage = null

                    viewModel.updateTransaction(
                        id = transaction.id,
                        request = com.latechhub.finance.data.remote.UpdateTransactionRequest(
                            accountId = accountId,
                            type = type,
                            category = category.ifBlank { null },
                            amount = parsedAmount,
                            description = description.ifBlank { null },
                            transactionDate = transactionDate.ifBlank { null },
                            reference = reference.ifBlank { null },
                            transferAccountId = if (type == "TRANSFER") {
                                transferAccountId.ifBlank { null }
                            } else {
                                null
                            }
                        ),
                        onResult = { success, message, updatedTransaction ->
                            saving = false
                            if (success) {
                                onUpdated(true, message, updatedTransaction)
                            } else {
                                errorMessage = message
                                onUpdated(false, message, null)
                            }
                        }
                    )
                },
                modifier = Modifier.weight(1f),
                enabled = !saving
            ) {
                Text(if (saving) "Saving..." else "Save Changes")
            }
        }
    }
}


