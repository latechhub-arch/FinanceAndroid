package com.latechhub.finance.ui.accounts

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
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.latechhub.finance.data.remote.CreateAccountRequest
import com.latechhub.finance.data.remote.FinancialAccount
import com.latechhub.finance.data.remote.UpdateAccountRequest
import java.util.Locale

private val accountTypes = listOf(
    "CASH",
    "BANK",
    "MOBILE_MONEY",
    "CREDIT_CARD",
    "SAVINGS",
    "INVESTMENT",
    "OTHER"
)

@Composable
fun AccountsScreen(
    accounts: List<FinancialAccount>,
    viewModel: AccountViewModel
) {
    var showCreateForm by remember { mutableStateOf(false) }
    var accountToEdit by remember { mutableStateOf<FinancialAccount?>(null) }
    var accountToDelete by remember { mutableStateOf<FinancialAccount?>(null) }
    var dialogMessage by remember { mutableStateOf<String?>(null) }
    var dialogSuccess by remember { mutableStateOf(false) }

    accountToDelete?.let { account ->
        AlertDialog(
            onDismissRequest = { accountToDelete = null },
            title = { Text("Delete Account") },
            text = {
                Text("Are you sure you want to delete \"${account.name}\"?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteAccount(account.id) { success, responseMessage ->
                            dialogSuccess = success
                            dialogMessage = responseMessage
                            if (success) {
                                accountToDelete = null
                            }
                        }
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { accountToDelete = null }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
    dialogMessage?.let { message ->
        AlertDialog(
            onDismissRequest = {
                dialogMessage = null
            },
            title = {
                Text(
                    if (dialogSuccess) {
                        "Account Created"
                    } else {
                        "Account Error"
                    }
                )
            },
            text = {
                Text(message)
            },
            confirmButton = {
                Button(
                    onClick = {
                        dialogMessage = null

                        if (dialogSuccess) {
                            showCreateForm = false
                        }
                    }
                ) {
                    Text("OK")
                }
            }
        )
    }

    if (showCreateForm) {
        CreateAccountForm(
            viewModel = viewModel,
            onCancel = {
                showCreateForm = false
            },
            onCreated = { success, responseMessage ->
                dialogSuccess = success
                dialogMessage = responseMessage
            }
        )
        return
    }


    accountToEdit?.let { account ->
        EditAccountForm(
            account = account,
            viewModel = viewModel,
            onCancel = {
                accountToEdit = null
            },
            onUpdated = { success, responseMessage ->
                dialogSuccess = success
                dialogMessage = responseMessage
                if (success) {
                    accountToEdit = null
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
            text = "Accounts",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "${accounts.count { it.isActive }} active of ${accounts.size} accounts",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                showCreateForm = true
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Account")
        }

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

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedButton(
                            onClick = {
                                accountToEdit = account
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Edit")
                        }

                        OutlinedButton(
                            onClick = {
                                accountToDelete = account
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Delete")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CreateAccountForm(
    viewModel: AccountViewModel,
    onCancel: () -> Unit,
    onCreated: (Boolean, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("BANK") }
    var institution by remember { mutableStateOf("") }
    var accountNumber by remember { mutableStateOf("") }
    var currency by remember { mutableStateOf("KES") }
    var balance by remember { mutableStateOf("0") }
    var message by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Text(
            text = "Add Account",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = name,
            onValueChange = {
                name = it
            },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("Account name")
            },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Account type",
            style = MaterialTheme.typography.labelLarge
        )

        Spacer(modifier = Modifier.height(6.dp))

        LazyColumn(
            modifier = Modifier.height(150.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(accountTypes) { accountType ->
                if (type == accountType) {
                    Button(
                        onClick = {
                            type = accountType
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(accountType)
                    }
                } else {
                    OutlinedButton(
                        onClick = {
                            type = accountType
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(accountType)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        TextField(
            value = institution,
            onValueChange = {
                institution = it
            },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("Institution (optional)")
            },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        TextField(
            value = accountNumber,
            onValueChange = {
                accountNumber = it
            },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("Account number (optional)")
            },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        TextField(
            value = currency,
            onValueChange = {
                currency = it.uppercase().take(3)
            },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("Currency")
            },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        TextField(
            value = balance,
            onValueChange = {
                balance = it
            },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("Opening balance")
            },
            singleLine = true
        )

        message?.let {
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = it,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.weight(1f)
            ) {
                Text("Cancel")
            }

            Button(
                onClick = {
                    val parsedBalance = balance.toDoubleOrNull()

                    if (name.trim().length < 2) {
                        message = "Account name must be at least 2 characters"
                    } else if (currency.length != 3) {
                        message = "Currency must be a 3-letter code"
                    } else if (parsedBalance == null) {
                        message = "Opening balance must be a valid number"
                    } else {
                        viewModel.createAccount(
                            CreateAccountRequest(
                                name = name.trim(),
                                type = type,
                                institution = institution.trim().ifBlank {
                                    null
                                },
                                accountNumber = accountNumber.trim().ifBlank {
                                    null
                                },
                                currency = currency,
                                balance = parsedBalance
                            )
                        ) { success, responseMessage ->
                            onCreated(success, responseMessage)
                        }
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Create")
            }
        }
    }
}



@Composable
private fun EditAccountForm(
    account: FinancialAccount,
    viewModel: AccountViewModel,
    onCancel: () -> Unit,
    onUpdated: (Boolean, String) -> Unit
) {
    var name by remember { mutableStateOf(account.name) }
    var type by remember { mutableStateOf(account.type) }
    var institution by remember { mutableStateOf(account.institution ?: "") }
    var accountNumber by remember { mutableStateOf(account.accountNumber ?: "") }
    var currency by remember { mutableStateOf(account.currency) }
    var balance by remember { mutableStateOf(account.balance.toString()) }
    var isActive by remember { mutableStateOf(account.isActive) }
    var message by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Text(
            text = "Edit Account",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = name,
            onValueChange = {
                name = it
            },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("Account name")
            },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Account type",
            style = MaterialTheme.typography.labelLarge
        )

        Spacer(modifier = Modifier.height(6.dp))

        LazyColumn(
            modifier = Modifier.height(150.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(accountTypes) { accountType ->
                if (type == accountType) {
                    Button(
                        onClick = {
                            type = accountType
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(accountType)
                    }
                } else {
                    OutlinedButton(
                        onClick = {
                            type = accountType
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(accountType)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        TextField(
            value = institution,
            onValueChange = {
                institution = it
            },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("Institution (optional)")
            },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        TextField(
            value = accountNumber,
            onValueChange = {
                accountNumber = it
            },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("Account number (optional)")
            },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        TextField(
            value = currency,
            onValueChange = {
                currency = it.uppercase().take(3)
            },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("Currency")
            },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        TextField(
            value = balance,
            onValueChange = {
                balance = it
            },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("Balance")
            },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = {
                    isActive = !isActive
                },
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    if (isActive) {
                        "Active"
                    } else {
                        "Inactive"
                    }
                )
            }
        }

        message?.let {
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = it,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.weight(1f)
            ) {
                Text("Cancel")
            }

            Button(
                onClick = {
                    val parsedBalance = balance.toDoubleOrNull()

                    if (name.trim().length < 2) {
                        message = "Account name must be at least 2 characters"
                    } else if (currency.length != 3) {
                        message = "Currency must be a 3-letter code"
                    } else if (parsedBalance == null) {
                        message = "Balance must be a valid number"
                    } else {
                        viewModel.updateAccount(
                            id = account.id,
                            request = UpdateAccountRequest(
                                name = name.trim(),
                                type = type,
                                institution = institution.trim().ifBlank {
                                    null
                                },
                                accountNumber = accountNumber.trim().ifBlank {
                                    null
                                },
                                currency = currency,
                                balance = parsedBalance,
                                isActive = isActive
                            )
                        ) { success, responseMessage ->
                            onUpdated(success, responseMessage)
                        }
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Update")
            }
        }
    }
}



