package com.latechhub.finance.ui.savingsgoals

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.latechhub.finance.data.remote.CreateSavingsContributionRequest
import com.latechhub.finance.data.remote.SavingsContribution
import com.latechhub.finance.data.remote.SavingsGoal
import com.latechhub.finance.data.remote.UpdateSavingsContributionRequest
import java.util.Locale

@Composable
fun SavingsGoalDetailsScreen(
    goal: SavingsGoal,
    viewModel: SavingsGoalViewModel,
    onBack: () -> Unit
) {
    var currentGoal by remember { mutableStateOf(goal) }
    var showAddDialog by remember { mutableStateOf(false) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    var editingContribution by remember {
        mutableStateOf<SavingsContribution?>(null)
    }
    var deletingContribution by remember {
        mutableStateOf<SavingsContribution?>(null)
    }

    LaunchedEffect(goal.id) {
        viewModel.loadGoal(goal.id) { refreshedGoal ->
            if (refreshedGoal != null) {
                currentGoal = refreshedGoal
            }
        }
        viewModel.loadContributions(goal.id)
    }

    when (val contributionState = viewModel.contributionsState) {
        is ContributionsState.Loading -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                Text(
                    text = "Savings Goal",
                    style = MaterialTheme.typography.headlineMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text("Loading contributions...")
            }
        }

        is ContributionsState.Error -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                Text(
                    text = "Savings Goal",
                    style = MaterialTheme.typography.headlineMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = contributionState.message,
                    color = MaterialTheme.colorScheme.error
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        viewModel.loadContributions(currentGoal.id)
                    }
                ) {
                    Text("Retry")
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = onBack
                ) {
                    Text("Back")
                }
            }
        }

        is ContributionsState.Success -> {
            val contributions = contributionState.contributions

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                Text(
                    text = currentGoal.name,
                    style = MaterialTheme.typography.headlineMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = String.format(
                        Locale.US,
                        "Target: KES %,.2f",
                        currentGoal.targetAmount
                    )
                )

                Text(
                    text = String.format(
                        Locale.US,
                        "Saved: KES %,.2f",
                        currentGoal.currentAmount
                    )
                )

                Text(
                    text = String.format(
                        Locale.US,
                        "Remaining: KES %,.2f",
                        currentGoal.remainingAmount
                    )
                )

                Text(
                    text = String.format(
                        Locale.US,
                        "Progress: %.2f%%",
                        currentGoal.percentageComplete
                    )
                )

                Text(
                    text = "Status: ${currentGoal.status}"
                )

                Text(
                    text = "Target date: ${currentGoal.targetDate}",
                    style = MaterialTheme.typography.bodySmall
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            showAddDialog = true
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Add Contribution")
                    }

                    OutlinedButton(
                        onClick = {
                            viewModel.loadContributions(currentGoal.id)
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Refresh")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = onBack,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Back")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Contributions (${contributions.size})",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (contributions.isEmpty()) {
                    Text(
                        text = "No contributions yet.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(
                            items = contributions,
                            key = { it.id }
                        ) { contribution ->
                            ContributionCard(
                                contribution = contribution,
                                onEdit = {
                                    editingContribution = contribution
                                },
                                onDelete = {
                                    deletingContribution = contribution
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddContributionDialog(
            goal = currentGoal,
            viewModel = viewModel,
            onDismiss = {
                showAddDialog = false
            },
            onSuccess = {
                viewModel.loadContributions(currentGoal.id)
            }
        )
    }

    editingContribution?.let { contribution ->
        EditContributionDialog(
            goal = currentGoal,
            contribution = contribution,
            viewModel = viewModel,
            onDismiss = {
                editingContribution = null
            },
            onSuccess = {
                viewModel.loadContributions(currentGoal.id)
            }
        )
    }

    deletingContribution?.let { contribution ->
        DeleteContributionDialog(
            goal = currentGoal,
            contribution = contribution,
            viewModel = viewModel,
            onDismiss = {
                deletingContribution = null
            },
            onSuccess = {
                successMessage = "Contribution deleted successfully"
            }
        )
    }
}

@Composable
private fun ContributionCard(
    contribution: SavingsContribution,
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
                text = String.format(
                    Locale.US,
                    "KES %,.2f",
                    contribution.amount
                ),
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Date: ${contribution.contributionDate}",
                style = MaterialTheme.typography.bodySmall
            )

            contribution.note?.takeIf { it.isNotBlank() }?.let {
                Text(
                    text = "Note: $it",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

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

@Composable
private fun AddContributionDialog(
    goal: SavingsGoal,
    viewModel: SavingsGoalViewModel,
    onDismiss: () -> Unit,
    onSuccess: () -> Unit
) {
    var amount by remember { mutableStateOf("") }
    var contributionDate by remember {
        mutableStateOf(defaultContributionDate())
    }
    var note by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Add Contribution")
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
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
                    value = contributionDate,
                    onValueChange = {
                        contributionDate = it
                        errorMessage = null
                    },
                    label = {
                        Text("Contribution Date")
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = note,
                    onValueChange = {
                        note = it
                        errorMessage = null
                    },
                    label = {
                        Text("Note")
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = String.format(
                        Locale.US,
                        "Remaining: KES %,.2f",
                        goal.remainingAmount
                    ),
                    style = MaterialTheme.typography.bodySmall
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
                        parsedAmount == null || parsedAmount <= 0.0 -> {
                            errorMessage =
                                "Enter a valid contribution amount greater than zero."
                        }

                        contributionDate.isBlank() -> {
                            errorMessage = "Contribution date is required."
                        }

                        else -> {
                            viewModel.createContribution(
                                goalId = goal.id,
                                request = CreateSavingsContributionRequest(
                                    amount = parsedAmount,
                                    contributionDate = contributionDate.trim(),
                                    note = note.trim().ifBlank { null }
                                )
                            ) { success, message, _ ->
                                if (success) {
                                    onSuccess()
                                    onDismiss()
                                } else {
                                    errorMessage = message
                                }
                            }
                        }
                    }
                }
            ) {
                Text("Add")
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
private fun EditContributionDialog(
    goal: SavingsGoal,
    contribution: SavingsContribution,
    viewModel: SavingsGoalViewModel,
    onDismiss: () -> Unit,
    onSuccess: () -> Unit
) {
    var amount by remember {
        mutableStateOf(contribution.amount.toString())
    }
    var contributionDate by remember {
        mutableStateOf(contribution.contributionDate)
    }
    var note by remember {
        mutableStateOf(contribution.note ?: "")
    }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Edit Contribution")
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
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
                    value = contributionDate,
                    onValueChange = {
                        contributionDate = it
                        errorMessage = null
                    },
                    label = {
                        Text("Contribution Date")
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = note,
                    onValueChange = {
                        note = it
                        errorMessage = null
                    },
                    label = {
                        Text("Note")
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = String.format(
                        Locale.US,
                        "Current remaining: KES %,.2f",
                        goal.remainingAmount
                    ),
                    style = MaterialTheme.typography.bodySmall
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
                        parsedAmount == null || parsedAmount <= 0.0 -> {
                            errorMessage =
                                "Enter a valid contribution amount greater than zero."
                        }

                        contributionDate.isBlank() -> {
                            errorMessage = "Contribution date is required."
                        }

                        else -> {
                            viewModel.updateContribution(
                                goalId = goal.id,
                                contributionId = contribution.id,
                                request = UpdateSavingsContributionRequest(
                                    amount = parsedAmount,
                                    contributionDate = contributionDate.trim(),
                                    note = note.trim().ifBlank { null }
                                )
                            ) { success, message, _ ->
                                if (success) {
                                    onSuccess()
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
private fun DeleteContributionDialog(
    goal: SavingsGoal,
    contribution: SavingsContribution,
    viewModel: SavingsGoalViewModel,
    onDismiss: () -> Unit,
    onSuccess: () -> Unit
) {
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Delete Contribution")
        },
        text = {
            Column {
                Text(
                    text = String.format(
                        Locale.US,
                        "Delete this contribution of KES %,.2f?",
                        contribution.amount
                    )
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
                    viewModel.deleteContribution(
                        goalId = goal.id,
                        contributionId = contribution.id
                    ) { success, message ->
                        if (success) {
                            onSuccess()
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

private fun defaultContributionDate(): String {
    return java.time.LocalDate.now()
        .atStartOfDay()
        .atOffset(java.time.ZoneOffset.UTC)
        .format(java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME)
}
