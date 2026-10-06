package com.latechhub.finance.ui.recurringtransactions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.latechhub.finance.data.remote.CreateRecurringTransactionRequest
import com.latechhub.finance.data.remote.FinancialAccount
import com.latechhub.finance.data.remote.RecurringTransaction
import com.latechhub.finance.data.remote.UpdateRecurringTransactionRequest
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale

private val recurringTypes = listOf("INCOME", "EXPENSE")
private val recurringFrequencies = listOf("DAILY", "WEEKLY", "MONTHLY", "YEARLY")
private val recurringStatuses = listOf("ACTIVE", "PAUSED", "COMPLETED")

@Composable
fun RecurringTransactionScreen(
    recurringTransactions: List<RecurringTransaction>,
    accounts: List<FinancialAccount>,
    viewModel: RecurringTransactionViewModel
) {
    var showCreate by remember { mutableStateOf(false) }
    var editing by remember { mutableStateOf<RecurringTransaction?>(null) }
    var deleting by remember { mutableStateOf<RecurringTransaction?>(null) }
    val filters = viewModel.filters.collectAsState().value

    fun reload() = viewModel.loadRecurringTransactions(
        status = filters.status,
        type = filters.type,
        frequency = filters.frequency
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Text("Recurring Transactions", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(8.dp))
        Text("${recurringTransactions.size} recurring transactions")

        Spacer(Modifier.height(12.dp))
        Button(
            onClick = { showCreate = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Create Recurring Transaction")
        }

        Spacer(Modifier.height(8.dp))
        OutlinedButton(
            onClick = ::reload,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Refresh")
        }

        Spacer(Modifier.height(12.dp))
        FilterRow(
            label = "Status",
            values = recurringStatuses,
            selected = filters.status,
            onSelect = {
                viewModel.loadRecurringTransactions(
                    status = it,
                    type = filters.type,
                    frequency = filters.frequency
                )
            }
        )
        FilterRow(
            label = "Type",
            values = recurringTypes,
            selected = filters.type,
            onSelect = {
                viewModel.loadRecurringTransactions(
                    status = filters.status,
                    type = it,
                    frequency = filters.frequency
                )
            }
        )
        FilterRow(
            label = "Frequency",
            values = recurringFrequencies,
            selected = filters.frequency,
            onSelect = {
                viewModel.loadRecurringTransactions(
                    status = filters.status,
                    type = filters.type,
                    frequency = it
                )
            }
        )

        Spacer(Modifier.height(12.dp))
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(recurringTransactions, key = { it.id }) { transaction ->
                RecurringTransactionCard(
                    transaction = transaction,
                    onEdit = { editing = transaction },
                    onDelete = { deleting = transaction }
                )
            }
        }
    }

    if (showCreate) {
        RecurringTransactionFormDialog(
            transaction = null,
            accounts = accounts,
            viewModel = viewModel,
            onDismiss = { showCreate = false }
        )
    }

    editing?.let { transaction ->
        RecurringTransactionFormDialog(
            transaction = transaction,
            accounts = accounts,
            viewModel = viewModel,
            onDismiss = { editing = null }
        )
    }

    deleting?.let { transaction ->
        DeleteRecurringTransactionDialog(
            transaction = transaction,
            viewModel = viewModel,
            onDismiss = { deleting = null }
        )
    }
}

@Composable
private fun FilterRow(
    label: String,
    values: List<String>,
    selected: String?,
    onSelect: (String?) -> Unit
) {
    Text(label, style = MaterialTheme.typography.labelLarge)
    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        item {
            FilterButton("All", selected == null) { onSelect(null) }
        }
        items(values) { value ->
            FilterButton(value, selected == value) { onSelect(value) }
        }
    }
    Spacer(Modifier.height(6.dp))
}

@Composable
private fun FilterButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    if (selected) {
        Button(onClick = onClick) { Text(text) }
    } else {
        OutlinedButton(onClick = onClick) { Text(text) }
    }
}

@Composable
private fun RecurringTransactionFormDialog(
    transaction: RecurringTransaction?,
    accounts: List<FinancialAccount>,
    viewModel: RecurringTransactionViewModel,
    onDismiss: () -> Unit
) {
    val editing = transaction != null
    val initialAccountId = transaction?.accountId
        ?: accounts.firstOrNull { it.isActive }?.id
        ?: ""

    var accountId by remember { mutableStateOf(initialAccountId) }
    var type by remember { mutableStateOf(transaction?.type ?: "EXPENSE") }
    var category by remember { mutableStateOf(transaction?.category ?: "") }
    var amount by remember { mutableStateOf(transaction?.amount?.toString() ?: "") }
    var description by remember { mutableStateOf(transaction?.description ?: "") }
    var frequency by remember { mutableStateOf(transaction?.frequency ?: "MONTHLY") }
    var startDate by remember {
        mutableStateOf(transaction?.let { formatDate(it.startDate) } ?: today())
    }
    var endDate by remember {
        mutableStateOf(transaction?.endDate?.let(::formatDate) ?: "")
    }
    var nextRunDate by remember {
        mutableStateOf(transaction?.let { formatDate(it.nextRunDate) } ?: today())
    }
    var status by remember { mutableStateOf(transaction?.status ?: "ACTIVE") }
    var error by remember { mutableStateOf<String?>(null) }
    var showAccountPicker by remember { mutableStateOf(false) }

    val selectableAccounts = accounts.filter {
        it.isActive || it.id == accountId
    }
    val selectedAccount = selectableAccounts.firstOrNull { it.id == accountId }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(if (editing) "Edit Recurring Transaction" else "Create Recurring Transaction")
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Account", style = MaterialTheme.typography.labelLarge)

                OutlinedButton(
                    onClick = { showAccountPicker = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        selectedAccount?.let { "${it.name} • ${it.currency}" }
                            ?: "Select account"
                    )
                }

                if (selectableAccounts.isEmpty()) {
                    Text(
                        "No active accounts available.",
                        color = MaterialTheme.colorScheme.error
                    )
                }

                SelectorRow("Type", recurringTypes, type) { type = it }
                SelectorRow("Frequency", recurringFrequencies, frequency) { frequency = it }

                if (editing) {
                    SelectorRow("Status", recurringStatuses, status) { status = it }
                }

                Field("Category (optional)", category) {
                    category = it
                    error = null
                }

                Field("Amount", amount) {
                    amount = it
                    error = null
                }

                Field("Description (optional)", description) {
                    description = it
                    error = null
                }

                Field("Start date (YYYY-MM-DD)", startDate) {
                    startDate = it
                    error = null
                }

                Field("End date (optional, YYYY-MM-DD)", endDate) {
                    endDate = it
                    error = null
                }

                Field("Next run date (YYYY-MM-DD)", nextRunDate) {
                    nextRunDate = it
                    error = null
                }

                error?.let {
                    Text(
                        it,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amountValue = amount.toDoubleOrNull()
                    val start = parseDate(startDate)
                    val next = parseDate(nextRunDate)
                    val end = if (endDate.isBlank()) null else parseDate(endDate)

                    error = when {
                        accountId.isBlank() -> "Select an account."
                        amountValue == null || amountValue <= 0.0 ->
                            "Enter a valid amount greater than zero."
                        start == null -> "Start date must use YYYY-MM-DD."
                        next == null -> "Next run date must use YYYY-MM-DD."
                        endDate.isNotBlank() && end == null ->
                            "End date must use YYYY-MM-DD."
                        start != null && next != null && next.isBefore(start) ->
                            "Next run date cannot be before the start date."
                        start != null && end != null && end.isBefore(start) ->
                            "End date cannot be before the start date."
                        next != null && end != null && next.isAfter(end) ->
                            "Next run date cannot be after the end date."
                        else -> null
                    }

                    if (error == null) {
                        if (editing) {
                            viewModel.updateRecurringTransaction(
                                transaction!!.id,
                                UpdateRecurringTransactionRequest(
                                    accountId = accountId,
                                    type = type,
                                    category = category.trim().ifBlank { null },
                                    amount = amountValue!!,
                                    description = description.trim().ifBlank { null },
                                    frequency = frequency,
                                    startDate = toStartDate(start!!),
                                    endDate = end?.let(::toEndDate),
                                    nextRunDate = toStartDate(next!!),
                                    status = status
                                )
                            ) { success, message, _ ->
                                if (success) onDismiss() else error = message
                            }
                        } else {
                            viewModel.createRecurringTransaction(
                                CreateRecurringTransactionRequest(
                                    accountId = accountId,
                                    type = type,
                                    category = category.trim().ifBlank { null },
                                    amount = amountValue!!,
                                    description = description.trim().ifBlank { null },
                                    frequency = frequency,
                                    startDate = toStartDate(start!!),
                                    endDate = end?.let(::toEndDate),
                                    nextRunDate = toStartDate(next!!)
                                )
                            ) { success, message, _ ->
                                if (success) onDismiss() else error = message
                            }
                        }
                    }
                }
            ) {
                Text(if (editing) "Save" else "Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )

    if (showAccountPicker) {
        AccountPickerDialog(
            accounts = selectableAccounts,
            selectedAccountId = accountId,
            onSelect = {
                accountId = it
                showAccountPicker = false
            },
            onDismiss = { showAccountPicker = false }
        )
    }
}

@Composable
private fun Field(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun SelectorRow(
    label: String,
    values: List<String>,
    selected: String,
    onSelect: (String) -> Unit
) {
    Text(label, style = MaterialTheme.typography.labelLarge)

    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        items(values) { value ->
            if (value == selected) {
                Button(onClick = { onSelect(value) }) {
                    Text(value)
                }
            } else {
                OutlinedButton(onClick = { onSelect(value) }) {
                    Text(value)
                }
            }
        }
    }
}

@Composable
private fun AccountPickerDialog(
    accounts: List<FinancialAccount>,
    selectedAccountId: String,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Account") },
        text = {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(accounts, key = { it.id }) { account ->
                    if (account.id == selectedAccountId) {
                        Button(
                            onClick = { onSelect(account.id) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("${account.name} • ${account.currency}")
                        }
                    } else {
                        OutlinedButton(
                            onClick = { onSelect(account.id) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("${account.name} • ${account.currency}")
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun DeleteRecurringTransactionDialog(
    transaction: RecurringTransaction,
    viewModel: RecurringTransactionViewModel,
    onDismiss: () -> Unit
) {
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete Recurring Transaction") },
        text = {
            Column {
                Text(
                    "Delete the ${transaction.type.lowercase()} recurring transaction " +
                        "of ${transaction.account.currency} " +
                        String.format(Locale.US, "%,.2f", transaction.amount) +
                        "?"
                )

                error?.let {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        it,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    viewModel.deleteRecurringTransaction(transaction.id) { success, message ->
                        if (success) onDismiss() else error = message
                    }
                }
            ) {
                Text("Delete")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun RecurringTransactionCard(
    transaction: RecurringTransaction,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text(
                transaction.category?.takeIf { it.isNotBlank() }
                    ?: transaction.description?.takeIf { it.isNotBlank() }
                    ?: transaction.type,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(Modifier.height(6.dp))

            Text(
                String.format(
                    Locale.US,
                    "%s %,.2f",
                    transaction.account.currency,
                    transaction.amount
                ),
                style = MaterialTheme.typography.titleLarge
            )

            Text("${transaction.type} • ${transaction.frequency}")
            Text("Account: ${transaction.account.name}")

            transaction.category?.takeIf { it.isNotBlank() }?.let {
                Text("Category: $it")
            }

            transaction.description?.takeIf { it.isNotBlank() }?.let {
                Text("Description: $it")
            }

            Text("Start: ${formatDate(transaction.startDate)}")

            transaction.endDate?.let {
                Text("End: ${formatDate(it)}")
            }

            Text("Next run: ${formatDate(transaction.nextRunDate)}")
            Text("Status: ${transaction.status}")

            Spacer(Modifier.height(10.dp))

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onEdit,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Edit")
                }

                OutlinedButton(
                    onClick = onDelete,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Delete")
                }
            }
        }
    }
}

private fun today(): String = LocalDate.now().toString()

private fun parseDate(value: String): LocalDate? =
    runCatching {
        LocalDate.parse(value.trim())
    }.getOrNull()

private fun formatDate(value: String): String =
    runCatching {
        OffsetDateTime.parse(value).toLocalDate().toString()
    }.getOrElse {
        value.take(10)
    }

private fun toStartDate(date: LocalDate): String =
    date
        .atStartOfDay()
        .atOffset(ZoneOffset.UTC)
        .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)

private fun toEndDate(date: LocalDate): String =
    date
        .atTime(23, 59, 59)
        .atOffset(ZoneOffset.UTC)
        .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)





