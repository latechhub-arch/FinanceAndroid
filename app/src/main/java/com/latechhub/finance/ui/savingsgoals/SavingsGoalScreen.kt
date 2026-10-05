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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.latechhub.finance.data.remote.CreateSavingsGoalRequest
import com.latechhub.finance.data.remote.SavingsGoal
import com.latechhub.finance.data.remote.UpdateSavingsGoalRequest
import java.util.Locale

@Composable
fun SavingsGoalScreen(
    goals: List<SavingsGoal>,
    viewModel: SavingsGoalViewModel,
    onGoalClick: (SavingsGoal) -> Unit
) {
    var showCreateDialog by remember { mutableStateOf(false) }
    var editingGoal by remember { mutableStateOf<SavingsGoal?>(null) }
    var deletingGoal by remember { mutableStateOf<SavingsGoal?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Text(
            text = "Savings Goals",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "${goals.size} savings goals",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                showCreateDialog = true
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Create Savings Goal")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                viewModel.loadGoals()
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
                items = goals,
                key = { it.id }
            ) { goal ->
                SavingsGoalCard(
                    goal = goal,
                    onView = {
                        onGoalClick(goal)
                    },
                    onEdit = {
                        editingGoal = goal
                    },
                    onDelete = {
                        deletingGoal = goal
                    }
                )
            }
        }
    }

    if (showCreateDialog) {
        CreateSavingsGoalDialog(
            viewModel = viewModel,
            onDismiss = {
                showCreateDialog = false
            }
        )
    }

    editingGoal?.let { goal ->
        EditSavingsGoalDialog(
            goal = goal,
            viewModel = viewModel,
            onDismiss = {
                editingGoal = null
            }
        )
    }

    deletingGoal?.let { goal ->
        DeleteSavingsGoalDialog(
            goal = goal,
            viewModel = viewModel,
            onDismiss = {
                deletingGoal = null
            }
        )
    }
}

@Composable
private fun SavingsGoalCard(
    goal: SavingsGoal,
    onView: () -> Unit,
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
                text = goal.name,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = String.format(
                    Locale.US,
                    "Target: KES %,.2f",
                    goal.targetAmount
                )
            )

            Text(
                text = String.format(
                    Locale.US,
                    "Saved: KES %,.2f",
                    goal.currentAmount
                )
            )

            Text(
                text = String.format(
                    Locale.US,
                    "Remaining: KES %,.2f",
                    goal.remainingAmount
                )
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = String.format(
                    Locale.US,
                    "Progress: %.2f%%",
                    goal.percentageComplete
                ),
                style = MaterialTheme.typography.bodySmall
            )

            Text(
                text = "Status: ${goal.status}",
                style = MaterialTheme.typography.bodySmall
            )

            Text(
                text = "Contributions: ${goal.contributionCount}",
                style = MaterialTheme.typography.bodySmall
            )

            Text(
                text = "Target date: ${goal.targetDate}",
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onView,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("View")
                }

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
private fun CreateSavingsGoalDialog(
    viewModel: SavingsGoalViewModel,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var targetAmount by remember { mutableStateOf("") }
    var targetDate by remember { mutableStateOf(defaultSavingsGoalDate()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Create Savings Goal")
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        errorMessage = null
                    },
                    label = {
                        Text("Goal Name")
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = targetAmount,
                    onValueChange = {
                        targetAmount = it
                        errorMessage = null
                    },
                    label = {
                        Text("Target Amount")
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = targetDate,
                    onValueChange = {
                        targetDate = it
                        errorMessage = null
                    },
                    label = {
                        Text("Target Date")
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
                    val parsedAmount = targetAmount.toDoubleOrNull()

                    when {
                        name.trim().isEmpty() -> {
                            errorMessage = "Goal name is required."
                        }

                        parsedAmount == null || parsedAmount <= 0.0 -> {
                            errorMessage =
                                "Enter a valid target amount greater than zero."
                        }

                        targetDate.isBlank() -> {
                            errorMessage = "Target date is required."
                        }

                        else -> {
                            viewModel.createGoal(
                                CreateSavingsGoalRequest(
                                    name = name.trim(),
                                    targetAmount = parsedAmount,
                                    targetDate = targetDate.trim()
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
private fun EditSavingsGoalDialog(
    goal: SavingsGoal,
    viewModel: SavingsGoalViewModel,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(goal.name) }
    var targetAmount by remember {
        mutableStateOf(goal.targetAmount.toString())
    }
    var targetDate by remember {
        mutableStateOf(goal.targetDate)
    }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Edit Savings Goal")
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        errorMessage = null
                    },
                    label = {
                        Text("Goal Name")
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = targetAmount,
                    onValueChange = {
                        targetAmount = it
                        errorMessage = null
                    },
                    label = {
                        Text("Target Amount")
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = targetDate,
                    onValueChange = {
                        targetDate = it
                        errorMessage = null
                    },
                    label = {
                        Text("Target Date")
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
                    val parsedAmount = targetAmount.toDoubleOrNull()

                    when {
                        name.trim().isEmpty() -> {
                            errorMessage = "Goal name is required."
                        }

                        parsedAmount == null || parsedAmount <= 0.0 -> {
                            errorMessage =
                                "Enter a valid target amount greater than zero."
                        }

                        targetDate.isBlank() -> {
                            errorMessage = "Target date is required."
                        }

                        else -> {
                            viewModel.updateGoal(
                                id = goal.id,
                                request = UpdateSavingsGoalRequest(
                                    name = name.trim(),
                                    targetAmount = parsedAmount,
                                    targetDate = targetDate.trim()
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
private fun DeleteSavingsGoalDialog(
    goal: SavingsGoal,
    viewModel: SavingsGoalViewModel,
    onDismiss: () -> Unit
) {
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Delete Savings Goal")
        },
        text = {
            Column {
                Text(
                    text = "Are you sure you want to delete the \"${goal.name}\" savings goal?"
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
                    viewModel.deleteGoal(
                        id = goal.id
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

private fun defaultSavingsGoalDate(): String {
    return java.time.LocalDate.now()
        .plusMonths(6)
        .atTime(23, 59, 59)
        .atOffset(java.time.ZoneOffset.UTC)
        .format(java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME)
}
