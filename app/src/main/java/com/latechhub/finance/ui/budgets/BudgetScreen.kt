package com.latechhub.finance.ui.budgets

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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.latechhub.finance.data.remote.Budget
import com.latechhub.finance.data.remote.CreateBudgetRequest
import com.latechhub.finance.data.remote.UpdateBudgetRequest
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun BudgetScreen(
    budgets: List<Budget>,
    viewModel: BudgetViewModel
) {
    var showCreateDialog by remember { mutableStateOf(false) }
    var editingBudget by remember { mutableStateOf<Budget?>(null) }
    var deletingBudget by remember { mutableStateOf<Budget?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Text(
            text = "Budgets",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "${budgets.size} budgets",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                showCreateDialog = true
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Create Budget")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                viewModel.loadBudgets()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Refresh")
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                items = budgets,
                key = { it.id }
            ) { budget ->
                BudgetCard(
                    budget = budget,
                    onEdit = {
                        editingBudget = budget
                    },
                    onDelete = {
                        deletingBudget = budget
                    }
                )
            }
        }
    }

    if (showCreateDialog) {
        CreateBudgetDialog(
            viewModel = viewModel,
            onDismiss = {
                showCreateDialog = false
            }
        )
    }

    editingBudget?.let { budget ->
        EditBudgetDialog(
            budget = budget,
            viewModel = viewModel,
            onDismiss = {
                editingBudget = null
            }
        )
    }

    deletingBudget?.let { budget ->
        DeleteBudgetDialog(
            budget = budget,
            viewModel = viewModel,
            onDismiss = {
                deletingBudget = null
            }
        )
    }
}

@Composable
private fun CreateBudgetDialog(
    viewModel: BudgetViewModel,
    onDismiss: () -> Unit
) {
    var category by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf(defaultStartDate()) }
    var endDate by remember { mutableStateOf(defaultEndDate()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Create Budget")
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = category,
                    onValueChange = {
                        category = it
                        errorMessage = null
                    },
                    label = {
                        Text("Category")
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = amount,
                    onValueChange = {
                        amount = it
                        errorMessage = null
                    },
                    label = {
                        Text("Amount")
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = startDate,
                    onValueChange = {
                        startDate = it
                        errorMessage = null
                    },
                    label = {
                        Text("Start Date")
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = endDate,
                    onValueChange = {
                        endDate = it
                        errorMessage = null
                    },
                    label = {
                        Text("End Date")
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                errorMessage?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val parsedAmount = amount.toDoubleOrNull()

                    when {
                        category.trim().isEmpty() -> {
                            errorMessage = "Category is required."
                        }

                        parsedAmount == null || parsedAmount <= 0.0 -> {
                            errorMessage = "Enter a valid amount greater than zero."
                        }

                        startDate.isBlank() || endDate.isBlank() -> {
                            errorMessage = "Start and end dates are required."
                        }

                        else -> {
                            viewModel.createBudget(
                                CreateBudgetRequest(
                                    category = category.trim(),
                                    amount = parsedAmount,
                                    startDate = startDate.trim(),
                                    endDate = endDate.trim()
                                )
                            ) { success, message, _ ->
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
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun EditBudgetDialog(
    budget: Budget,
    viewModel: BudgetViewModel,
    onDismiss: () -> Unit
) {
    var category by remember { mutableStateOf(budget.category) }
    var amount by remember {
        mutableStateOf(budget.budgetAmount.toString())
    }
    var startDate by remember { mutableStateOf(budget.startDate) }
    var endDate by remember { mutableStateOf(budget.endDate) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Edit Budget")
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = category,
                    onValueChange = {
                        category = it
                        errorMessage = null
                    },
                    label = {
                        Text("Category")
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = amount,
                    onValueChange = {
                        amount = it
                        errorMessage = null
                    },
                    label = {
                        Text("Amount")
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = startDate,
                    onValueChange = {
                        startDate = it
                        errorMessage = null
                    },
                    label = {
                        Text("Start Date")
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = endDate,
                    onValueChange = {
                        endDate = it
                        errorMessage = null
                    },
                    label = {
                        Text("End Date")
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                errorMessage?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val parsedAmount = amount.toDoubleOrNull()

                    when {
                        category.trim().isEmpty() -> {
                            errorMessage = "Category is required."
                        }

                        parsedAmount == null || parsedAmount <= 0.0 -> {
                            errorMessage = "Enter a valid amount greater than zero."
                        }

                        startDate.isBlank() || endDate.isBlank() -> {
                            errorMessage = "Start and end dates are required."
                        }

                        else -> {
                            viewModel.updateBudget(
                                id = budget.id,
                                request = UpdateBudgetRequest(
                                    category = category.trim(),
                                    amount = parsedAmount,
                                    startDate = startDate.trim(),
                                    endDate = endDate.trim()
                                )
                            ) { success, message, _ ->
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
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun DeleteBudgetDialog(
    budget: Budget,
    viewModel: BudgetViewModel,
    onDismiss: () -> Unit
) {
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Delete Budget")
        },
        text = {
            Column {
                Text(
                    text = "Are you sure you want to delete the \"${budget.category}\" budget of KES ${
                        String.format(
                            Locale.US,
                            "%,.2f",
                            budget.budgetAmount
                        )
                    }?"
                )

                errorMessage?.let {
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    viewModel.deleteBudget(
                        id = budget.id
                    ) { success, message ->
                        if (success) {
                            onDismiss()
                        } else {
                            errorMessage = message
                        }
                    }
                }
            ) {
                Text("Delete")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun BudgetCard(
    budget: Budget,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = budget.category,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = String.format(
                    Locale.US,
                    "Budget: KES %,.2f",
                    budget.budgetAmount
                ),
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = String.format(
                    Locale.US,
                    "Spent: KES %,.2f",
                    budget.actualSpending
                ),
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = String.format(
                    Locale.US,
                    "Remaining: KES %,.2f",
                    budget.remainingAmount
                ),
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = String.format(
                    Locale.US,
                    "Used: %.2f%%",
                    budget.percentageUsed
                ),
                style = MaterialTheme.typography.bodySmall
            )

            Text(
                text = "Status: ${budget.status}",
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Transactions: ${budget.transactionCount}",
                style = MaterialTheme.typography.bodySmall
            )

            Text(
                text = "${budget.startDate} - ${budget.endDate}",
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
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

private fun defaultStartDate(): String {
    return LocalDate.now()
        .withDayOfMonth(1)
        .atStartOfDay()
        .atOffset(ZoneOffset.UTC)
        .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
}

private fun defaultEndDate(): String {
    return LocalDate.now()
        .withDayOfMonth(1)
        .plusMonths(1)
        .minusDays(1)
        .atTime(23, 59, 59)
        .atOffset(ZoneOffset.UTC)
        .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
}
