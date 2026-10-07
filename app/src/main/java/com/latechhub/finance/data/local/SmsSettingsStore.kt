package com.latechhub.finance.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.smsDataStore by preferencesDataStore(name = "sms_preferences")

class SmsSettingsStore(
    private val context: Context
) {

    private object Keys {
        val MPESA_ACCOUNT_ID = stringPreferencesKey("mpesa_account_id")
        val BANK_ACCOUNT_ID = stringPreferencesKey("bank_account_id")
        val INITIAL_SYNC_COMPLETED = booleanPreferencesKey("initial_sync_completed")
    }

    val mpesaAccountId: Flow<String?> =
        context.smsDataStore.data.map { preferences ->
            preferences[Keys.MPESA_ACCOUNT_ID]
        }

    val bankAccountId: Flow<String?> =
        context.smsDataStore.data.map { preferences ->
            preferences[Keys.BANK_ACCOUNT_ID]
        }

    val initialSyncCompleted: Flow<Boolean> =
        context.smsDataStore.data.map { preferences ->
            preferences[Keys.INITIAL_SYNC_COMPLETED] ?: false
        }

    suspend fun saveMpesaAccountId(accountId: String) {
        context.smsDataStore.edit { preferences ->
            preferences[Keys.MPESA_ACCOUNT_ID] = accountId
        }
    }

    suspend fun saveBankAccountId(accountId: String) {
        context.smsDataStore.edit { preferences ->
            preferences[Keys.BANK_ACCOUNT_ID] = accountId
        }
    }

    suspend fun markInitialSyncCompleted() {
        context.smsDataStore.edit { preferences ->
            preferences[Keys.INITIAL_SYNC_COMPLETED] = true
        }
    }

    suspend fun clearMappings() {
        context.smsDataStore.edit { preferences ->
            preferences.remove(Keys.MPESA_ACCOUNT_ID)
            preferences.remove(Keys.BANK_ACCOUNT_ID)
            preferences.remove(Keys.INITIAL_SYNC_COMPLETED)
        }
    }
}
