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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.latechhub.finance.data.remote.CreateTransactionRequest
import com.latechhub.finance.data.remote.MpesaImportRequest
import com.latechhub.finance.data.remote.BankSmsImportRequest
import com.latechhub.finance.data.remote.FulizaImportRequest
import com.latechhub.finance.data.remote.FinancialAccount
import com.latechhub.finance.data.remote.TransactionItem
import com.latechhub.finance.ui.dashboard.TransactionFilters
import com.latechhub.finance.ui.dashboard.TransactionState
import com.latechhub.finance.ui.dashboard.TransactionViewModel
import java.util.Locale

@Composable
fun TransactionsScreen(
    transactionViewModel: TransactionViewModel,
    accounts: List<FinancialAccount>,
    onTransactionClick: (TransactionItem) -> Unit
) {
    val state by transactionViewModel.state.collectAsState()
    val filters by transactionViewModel.filters.collectAsState()

    var showCreateDialog by remember { mutableStateOf(false) }
    var showImportDialog by remember { mutableStateOf(false) }
    var showFilters by remember { mutableStateOf(false) }

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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Transactions",
                        style = MaterialTheme.typography.headlineMedium
                    )

                    Button(
                        onClick = {
                            showImportDialog = true
                        }
                    ) {
                        Text("Import")
                    }

                    Button(
                        onClick = {
                            showCreateDialog = true
                        }
                    ) {
                        Text("Add")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "${currentState.total} transactions",
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        showFilters = !showFilters
                    }
                ) {
                    Text(
                        if (showFilters) {
                            "Hide Filters"
                        } else {
                            "Show Filters"
                        }
                    )
                }

                if (showFilters) {
                    TransactionFilterPanel(
                        filters = filters,
                        accounts = accounts,
                        onApply = { category, type, accountId, startDate, endDate, minAmount, maxAmount ->
                            transactionViewModel.setFilters(
                                category = category,
                                type = type,
                                accountId = accountId,
                                startDate = startDate,
                                endDate = endDate,
                                minAmount = minAmount,
                                maxAmount = maxAmount
                            )
                        },
                        onClear = {
                            transactionViewModel.clearFilters()
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                }

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

    if (showCreateDialog) {
        CreateTransactionDialog(
            accounts = accounts,
            onDismiss = {
                showCreateDialog = false
            },
            onCreate = { request, onResult ->
                transactionViewModel.createTransaction(
                    request = request,
                    onResult = onResult
                )
            }
        )
    }
 
    if (showImportDialog) {
        ImportTransactionsDialog(
            accounts = accounts,
            viewModel = transactionViewModel,
            onDismiss = {
                showImportDialog = false
            }
        )
    }
}

@Composable
private fun ImportTransactionsDialog(
    accounts: List<FinancialAccount>,
    viewModel: TransactionViewModel,
    onDismiss: () -> Unit
) {
    val activeAccounts = accounts.filter { it.isActive }

    var source by remember { mutableStateOf("MPESA") }
    var accountId by remember {
        mutableStateOf(activeAccounts.firstOrNull()?.id ?: "")
    }

    var mpesaType by remember { mutableStateOf("INCOME") }
    var amount by remember { mutableStateOf("") }
    var transactionDate by remember { mutableStateOf("") }
    var externalReference by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var reference by remember { mutableStateOf("") }
    var sms by remember { mutableStateOf("") }
    var bankSmsSender by remember { mutableStateOf("") }

    var expandedSource by remember { mutableStateOf(false) }
    var expandedAccount by remember { mutableStateOf(false) }
    var expandedMpesaType by remember { mutableStateOf(false) }

    var saving by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = {
            if (!saving) {
                onDismiss()
            }
        },
        title = {
            Text("Import Transaction")
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Import Source",
                    style = MaterialTheme.typography.labelLarge
                )

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !saving,
                    onClick = {
                        expandedSource = true
                    }
                ) {
                    Text(
                        when (source) {
                            "MPESA" -> "M-PESA"
                            "BANK_SMS" -> "Bank SMS"
                            else -> "Fuliza"
                        }
                    )
                }

                DropdownMenu(
                    expanded = expandedSource,
                    onDismissRequest = {
                        expandedSource = false
                    }
                ) {
                    DropdownMenuItem(
                        text = { Text("M-PESA") },
                        onClick = {
                            source = "MPESA"
                            expandedSource = false
                            errorMessage = null
                        }
                    )

                    DropdownMenuItem(
                        text = { Text("Bank SMS") },
                        onClick = {
                            source = "BANK_SMS"
                            expandedSource = false
                            errorMessage = null
                        }
                    )

                    DropdownMenuItem(
                        text = { Text("Fuliza") },
                        onClick = {
                            source = "FULIZA"
                            expandedSource = false
                            errorMessage = null
                        }
                    )
                }

                Text(
                    text = "Account",
                    style = MaterialTheme.typography.labelLarge
                )

                if (activeAccounts.isEmpty()) {
                    Text(
                        text = "No active accounts available.",
                        color = MaterialTheme.colorScheme.error
                    )
                } else {
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !saving,
                        onClick = {
                            expandedAccount = true
                        }
                    ) {
                        Text(
                            activeAccounts
                                .firstOrNull { it.id == accountId }
                                ?.name
                                ?: "Select Account"
                        )
                    }

                    DropdownMenu(
                        expanded = expandedAccount,
                        onDismissRequest = {
                            expandedAccount = false
                        }
                    ) {
                        activeAccounts.forEach { account ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        "${account.name} (${account.currency})"
                                    )
                                },
                                onClick = {
                                    accountId = account.id
                                    expandedAccount = false
                                    errorMessage = null
                                }
                            )
                        }
                    }
                }

                if (source == "MPESA") {
                    Text(
                        text = "Transaction Type",
                        style = MaterialTheme.typography.labelLarge
                    )

                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !saving,
                        onClick = {
                            expandedMpesaType = true
                        }
                    ) {
                        Text(mpesaType)
                    }

                    DropdownMenu(
                        expanded = expandedMpesaType,
                        onDismissRequest = {
                            expandedMpesaType = false
                        }
                    ) {
                        DropdownMenuItem(
                            text = { Text("INCOME") },
                            onClick = {
                                mpesaType = "INCOME"
                                expandedMpesaType = false
                            }
                        )

                        DropdownMenuItem(
                            text = { Text("EXPENSE") },
                            onClick = {
                                mpesaType = "EXPENSE"
                                expandedMpesaType = false
                            }
                        )
                    }

                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = amount,
                        onValueChange = {
                            amount = it
                        },
                        label = {
                            Text("Amount")
                        },
                        singleLine = true
                    )

                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = transactionDate,
                        onValueChange = {
                            transactionDate = it
                        },
                        label = {
                            Text("Transaction Date")
                        },
                        placeholder = {
                            Text("2026-10-06T10:30:00+03:00")
                        },
                        singleLine = true
                    )

                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = externalReference,
                        onValueChange = {
                            externalReference = it
                        },
                        label = {
                            Text("M-PESA Transaction Reference")
                        },
                        singleLine = true
                    )

                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = category,
                        onValueChange = {
                            category = it
                        },
                        label = {
                            Text("Category (optional)")
                        },
                        singleLine = true
                    )

                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = description,
                        onValueChange = {
                            description = it
                        },
                        label = {
                            Text("Description (optional)")
                        },
                        singleLine = true
                    )

                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = reference,
                        onValueChange = {
                            reference = it
                        },
                        label = {
                            Text("Reference (optional)")
                        },
                        singleLine = true
                    )
                } else {
                    if (source == "BANK_SMS") {
                        OutlinedTextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = bankSmsSender,
                            onValueChange = {
                                bankSmsSender = it
                            },
                            label = {
                                Text("Bank SMS Sender")
                            },
                            placeholder = {
                                Text("e.g. CoopBank, KCB, Equity Bank")
                            },
                            singleLine = true
                        )
                    }
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = sms,
                        onValueChange = {
                            sms = it
                        },
                        label = {
                            Text(
                                if (source == "BANK_SMS") {
                                    "Bank SMS"
                                } else {
                                    "Fuliza SMS"
                                }
                            )
                        },
                        placeholder = {
                            Text("Paste the complete SMS message")
                        },
                        minLines = 5,
                        maxLines = 8
                    )
                }

                errorMessage?.let { message ->
                    Text(
                        text = message,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        },
        confirmButton = {
            Button(
                enabled = !saving && activeAccounts.isNotEmpty(),
                onClick = {
                    errorMessage = null

                    if (accountId.isBlank()) {
                        errorMessage = "Please select an account."
                        return@Button
                    }

                    if (source == "MPESA") {
                        val parsedAmount = amount.trim().toDoubleOrNull()

                        when {
                            parsedAmount == null || parsedAmount <= 0.0 -> {
                                errorMessage = "Enter a valid amount greater than zero."
                            }

                            transactionDate.trim().isEmpty() -> {
                                errorMessage = "Enter the transaction date."
                            }

                            externalReference.trim().isEmpty() -> {
                                errorMessage = "Enter the M-PESA transaction reference."
                            }

                            else -> {
                                saving = true

                                viewModel.importMpesaTransaction(
                                    request = MpesaImportRequest(
                                        accountId = accountId,
                                        type = mpesaType,
                                        amount = parsedAmount,
                                        transactionDate = transactionDate.trim(),
                                        externalReference = externalReference.trim(),
                                        description = description
                                            .trim()
                                            .takeIf { it.isNotEmpty() },
                                        category = category
                                            .trim()
                                            .takeIf { it.isNotEmpty() },
                                        reference = reference
                                            .trim()
                                            .takeIf { it.isNotEmpty() }
                                    )
                                ) { success, message, _ ->
                                    saving = false

                                    if (success) {
                                        onDismiss()
                                    } else {
                                        errorMessage = message
                                    }
                                }
                            }
                        }
                    } else {
                        if (sms.trim().isEmpty()) {
                            errorMessage = "Paste the SMS message to import."
                        } else {
                            saving = true

                            if (source == "BANK_SMS") {
                                viewModel.importBankSmsTransaction(
                                    request = BankSmsImportRequest(
                                        accountId = accountId,
                                        sender = bankSmsSender.trim(),
                                        sms = sms.trim()
                                    )
                                ) { success, message, _ ->
                                    saving = false

                                    if (success) {
                                        onDismiss()
                                    } else {
                                        errorMessage = message
                                    }
                                }
                            } else {
                                viewModel.importFulizaTransaction(
                                    request = FulizaImportRequest(
                                        accountId = accountId,
                                        sms = sms.trim()
                                    )
                                ) { success, message, _ ->
                                    saving = false

                                    if (success) {
                                        onDismiss()
                                    } else {
                                        errorMessage = message
                                    }
                                }
                            }
                        }
                    }
                }
            ) {
                Text(
                    if (saving) "Importing..." else "Import"
                )
            }
        },
        dismissButton = {
            TextButton(
                enabled = !saving,
                onClick = onDismiss
            ) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun TransactionFilterPanel(
    filters: TransactionFilters,
    accounts: List<FinancialAccount>,
    onApply: (
        String?,
        String?,
        String?,
        String?,
        String?,
        Double?,
        Double?
    ) -> Unit,
    onClear: () -> Unit
) {
    var category by remember(filters) {
        mutableStateOf(filters.category ?: "")
    }

    var type by remember(filters) {
        mutableStateOf(filters.type ?: "ALL")
    }

    var accountId by remember(filters) {
        mutableStateOf(filters.accountId)
    }

    var startDate by remember(filters) {
        mutableStateOf(filters.startDate ?: "")
    }

    var endDate by remember(filters) {
        mutableStateOf(filters.endDate ?: "")
    }

    var minAmount by remember(filters) {
        mutableStateOf(
            filters.minAmount?.toString() ?: ""
        )
    }

    var maxAmount by remember(filters) {
        mutableStateOf(
            filters.maxAmount?.toString() ?: ""
        )
    }

    var expandedType by remember { mutableStateOf(false) }
    var expandedAccount by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Transaction Filters",
                style = MaterialTheme.typography.titleMedium
            )

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = category,
                onValueChange = {
                    category = it
                },
                label = {
                    Text("Category")
                },
                singleLine = true
            )

            Text(
                text = "Type",
                style = MaterialTheme.typography.labelLarge
            )

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    expandedType = true
                }
            ) {
                Text(type)
            }

            DropdownMenu(
                expanded = expandedType,
                onDismissRequest = {
                    expandedType = false
                }
            ) {
                listOf(
                    "ALL",
                    "INCOME",
                    "EXPENSE",
                    "TRANSFER"
                ).forEach { transactionType ->
                    DropdownMenuItem(
                        text = {
                            Text(transactionType)
                        },
                        onClick = {
                            type = transactionType
                            expandedType = false
                        }
                    )
                }
            }

            Text(
                text = "Account",
                style = MaterialTheme.typography.labelLarge
            )

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    expandedAccount = true
                }
            ) {
                Text(
                    accounts.firstOrNull {
                        it.id == accountId
                    }?.name ?: "All accounts"
                )
            }

            DropdownMenu(
                expanded = expandedAccount,
                onDismissRequest = {
                    expandedAccount = false
                }
            ) {
                DropdownMenuItem(
                    text = {
                        Text("All accounts")
                    },
                    onClick = {
                        accountId = null
                        expandedAccount = false
                    }
                )

                accounts.forEach { account ->
                    DropdownMenuItem(
                        text = {
                            Text(account.name)
                        },
                        onClick = {
                            accountId = account.id
                            expandedAccount = false
                        }
                    )
                }
            }

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = startDate,
                onValueChange = {
                    startDate = it
                },
                label = {
                    Text("Start date")
                },
                placeholder = {
                    Text("2026-10-01T00:00:00+03:00")
                },
                singleLine = true
            )

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = endDate,
                onValueChange = {
                    endDate = it
                },
                label = {
                    Text("End date")
                },
                placeholder = {
                    Text("2026-10-31T23:59:59+03:00")
                },
                singleLine = true
            )

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = minAmount,
                onValueChange = {
                    minAmount = it
                },
                label = {
                    Text("Minimum amount")
                },
                singleLine = true
            )

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = maxAmount,
                onValueChange = {
                    maxAmount = it
                },
                label = {
                    Text("Maximum amount")
                },
                singleLine = true
            )

            errorMessage?.let { message ->
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        val parsedMin = if (minAmount.isBlank()) {
                            null
                        } else {
                            minAmount.toDoubleOrNull()
                        }

                        val parsedMax = if (maxAmount.isBlank()) {
                            null
                        } else {
                            maxAmount.toDoubleOrNull()
                        }

                        if (minAmount.isNotBlank() && parsedMin == null) {
                            errorMessage = "Minimum amount must be a valid number."
                            return@Button
                        }

                        if (maxAmount.isNotBlank() && parsedMax == null) {
                            errorMessage = "Maximum amount must be a valid number."
                            return@Button
                        }

                        if (
                            parsedMin != null &&
                            parsedMax != null &&
                            parsedMin > parsedMax
                        ) {
                            errorMessage =
                                "Minimum amount cannot exceed maximum amount."
                            return@Button
                        }

                        errorMessage = null

                        onApply(
                            category.trim().takeIf { it.isNotEmpty() },
                            type.takeIf { it != "ALL" },
                            accountId,
                            startDate.trim().takeIf { it.isNotEmpty() },
                            endDate.trim().takeIf { it.isNotEmpty() },
                            parsedMin,
                            parsedMax
                        )
                    }
                ) {
                    Text("Apply")
                }

                TextButton(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        errorMessage = null
                        onClear()
                    }
                ) {
                    Text("Clear")
                }
            }
        }
    }
}

@Composable
private fun CreateTransactionDialog(
    accounts: List<FinancialAccount>,
    onDismiss: () -> Unit,
    onCreate: (
        CreateTransactionRequest,
        (Boolean, String, TransactionItem?) -> Unit
    ) -> Unit
) {
    val activeAccounts = accounts.filter { it.isActive }

    var accountId by remember {
        mutableStateOf(
            activeAccounts.firstOrNull()?.id ?: ""
        )
    }

    var type by remember {
        mutableStateOf("EXPENSE")
    }

    var category by remember {
        mutableStateOf("")
    }

    var amount by remember {
        mutableStateOf("")
    }

    var description by remember {
        mutableStateOf("")
    }

    var transactionDate by remember {
        mutableStateOf("")
    }

    var reference by remember {
        mutableStateOf("")
    }

    var transferAccountId by remember {
        mutableStateOf<String?>(null)
    }

    var expandedAccount by remember {
        mutableStateOf(false)
    }

    var expandedType by remember {
        mutableStateOf(false)
    }

    var expandedTransferAccount by remember {
        mutableStateOf(false)
    }

    var errorMessage by remember {
        mutableStateOf<String?>(null)
    }

    var saving by remember {
        mutableStateOf(false)
    }

    AlertDialog(
        onDismissRequest = {
            if (!saving) {
                onDismiss()
            }
        },
        title = {
            Text("Add Transaction")
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Account",
                    style = MaterialTheme.typography.labelLarge
                )

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !saving,
                    onClick = {
                        expandedAccount = true
                    }
                ) {
                    Text(
                        activeAccounts.firstOrNull {
                            it.id == accountId
                        }?.name ?: "Select account"
                    )
                }

                DropdownMenu(
                    expanded = expandedAccount,
                    onDismissRequest = {
                        expandedAccount = false
                    }
                ) {
                    activeAccounts.forEach { account ->
                        DropdownMenuItem(
                            text = {
                                Text(account.name)
                            },
                            onClick = {
                                accountId = account.id
                                expandedAccount = false

                                if (
                                    transferAccountId == account.id
                                ) {
                                    transferAccountId = null
                                }
                            }
                        )
                    }
                }

                Text(
                    text = "Type",
                    style = MaterialTheme.typography.labelLarge
                )

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !saving,
                    onClick = {
                        expandedType = true
                    }
                ) {
                    Text(type)
                }

                DropdownMenu(
                    expanded = expandedType,
                    onDismissRequest = {
                        expandedType = false
                    }
                ) {
                    listOf(
                        "INCOME",
                        "EXPENSE",
                        "TRANSFER"
                    ).forEach { transactionType ->
                        DropdownMenuItem(
                            text = {
                                Text(transactionType)
                            },
                            onClick = {
                                type = transactionType
                                expandedType = false

                                if (transactionType != "TRANSFER") {
                                    transferAccountId = null
                                }
                            }
                        )
                    }
                }

                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = category,
                    onValueChange = {
                        category = it
                    },
                    label = {
                        Text("Category")
                    },
                    enabled = !saving,
                    singleLine = true
                )

                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = amount,
                    onValueChange = {
                        amount = it
                    },
                    label = {
                        Text("Amount")
                    },
                    enabled = !saving,
                    singleLine = true
                )

                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = description,
                    onValueChange = {
                        description = it
                    },
                    label = {
                        Text("Description")
                    },
                    enabled = !saving,
                    singleLine = true
                )

                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = reference,
                    onValueChange = {
                        reference = it
                    },
                    label = {
                        Text("Reference")
                    },
                    enabled = !saving,
                    singleLine = true
                )

                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = transactionDate,
                    onValueChange = {
                        transactionDate = it
                    },
                    label = {
                        Text("Transaction date")
                    },
                    placeholder = {
                        Text("2026-10-06T11:30:00+03:00")
                    },
                    enabled = !saving,
                    singleLine = true
                )

                if (type == "TRANSFER") {
                    Text(
                        text = "Transfer account",
                        style = MaterialTheme.typography.labelLarge
                    )

                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !saving,
                        onClick = {
                            expandedTransferAccount = true
                        }
                    ) {
                        Text(
                            activeAccounts.firstOrNull {
                                it.id == transferAccountId
                            }?.name ?: "Select transfer account"
                        )
                    }

                    DropdownMenu(
                        expanded = expandedTransferAccount,
                        onDismissRequest = {
                            expandedTransferAccount = false
                        }
                    ) {
                        activeAccounts
                            .filter { it.id != accountId }
                            .forEach { account ->
                                DropdownMenuItem(
                                    text = {
                                        Text(account.name)
                                    },
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
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Button(
                enabled = !saving,
                onClick = {
                    val parsedAmount = amount.toDoubleOrNull()

                    when {
                        activeAccounts.isEmpty() -> {
                            errorMessage =
                                "Create an active account before adding a transaction."
                        }

                        accountId.isBlank() -> {
                            errorMessage = "Please select an account."
                        }

                        parsedAmount == null || parsedAmount <= 0.0 -> {
                            errorMessage =
                                "Amount must be greater than zero."
                        }

                        type == "TRANSFER" &&
                            transferAccountId.isNullOrBlank() -> {
                            errorMessage =
                                "Please select a transfer account."
                        }

                        type == "TRANSFER" &&
                            transferAccountId == accountId -> {
                            errorMessage =
                                "Transfer accounts must be different."
                        }

                        else -> {
                            errorMessage = null
                            saving = true

                            val request = CreateTransactionRequest(
                                accountId = accountId,
                                type = type,
                                amount = parsedAmount,
                                category = category
                                    .trim()
                                    .takeIf { it.isNotEmpty() },
                                description = description
                                    .trim()
                                    .takeIf { it.isNotEmpty() },
                                transactionDate = transactionDate
                                    .trim()
                                    .takeIf { it.isNotEmpty() },
                                reference = reference
                                    .trim()
                                    .takeIf { it.isNotEmpty() },
                                transferAccountId =
                                    transferAccountId
                            )

                            onCreate(
                                request
                            ) { success, message, _ ->
                                saving = false

                                if (success) {
                                    onDismiss()
                                } else {
                                    errorMessage = message
                                }
                            }
                        }
                    }
                }
            ) {
                Text(
                    if (saving) "Saving..." else "Create"
                )
            }
        },
        dismissButton = {
            TextButton(
                enabled = !saving,
                onClick = onDismiss
            ) {
                Text("Cancel")
            }
        }
    )
}
