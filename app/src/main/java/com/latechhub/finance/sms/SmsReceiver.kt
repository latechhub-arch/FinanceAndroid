package com.latechhub.finance.sms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.latechhub.finance.data.local.FinanceDatabase
import com.latechhub.finance.data.local.PendingSmsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SmsReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
            return
        }

        val pendingResult = goAsync()
        val appContext = context.applicationContext

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)

                if (messages.isEmpty()) {
                    return@launch
                }

                val sender = messages.firstOrNull()?.originatingAddress
                    ?: return@launch

                val body = messages.joinToString(separator = "") { message ->
                    message.messageBody.orEmpty()
                }

                if (body.isBlank()) {
                    return@launch
                }

                val receivedAt = messages.firstOrNull()?.timestampMillis
                    ?: System.currentTimeMillis()

                val database = FinanceDatabase.getInstance(appContext)
                val repository = PendingSmsRepository(database.pendingSmsDao())

                repository.enqueue(
                    sender = sender,
                    body = body,
                    receivedAt = receivedAt
                )

                val workRequest =
                    OneTimeWorkRequestBuilder<SmsProcessingWorker>()
                        .build()

                WorkManager.getInstance(appContext)
                    .enqueue(workRequest)

            } finally {
                pendingResult.finish()
            }
        }
    }
}
