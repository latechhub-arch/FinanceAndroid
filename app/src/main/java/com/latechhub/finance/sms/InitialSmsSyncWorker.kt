package com.latechhub.finance.sms

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.provider.Telephony
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.latechhub.finance.data.local.SmsSettingsStore
import com.latechhub.finance.data.remote.ApiErrorHandler
import com.latechhub.finance.data.remote.BankSmsImportRequest
import com.latechhub.finance.data.remote.FulizaImportRequest
import com.latechhub.finance.data.remote.FulizaRepaymentSmsImportRequest
import com.latechhub.finance.data.remote.MpesaSmsImportRequest
import com.latechhub.finance.data.remote.TransactionRepository
import kotlinx.coroutines.flow.first
import retrofit2.HttpException

class InitialSmsSyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    private val smsSettingsStore = SmsSettingsStore(appContext)
    private val transactionRepository = TransactionRepository()

    override suspend fun doWork(): Result {
        if (
            ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.READ_SMS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.w(TAG, "READ_SMS permission has not been granted.")
            return Result.failure()
        }

        val mpesaAccountId = smsSettingsStore.mpesaAccountId.first()
        val bankAccountId = smsSettingsStore.bankAccountId.first()

        if (mpesaAccountId.isNullOrBlank() && bankAccountId.isNullOrBlank()) {
            Log.w(
                TAG,
                "Neither M-PESA nor bank account mapping is configured."
            )
            return Result.failure()
        }

        return try {
            val messages = readRelevantSms()

            for (message in messages) {
                when (
                    SmsClassifier.classify(
                        sender = message.sender,
                        body = message.body
                    )
                ) {
                    SmsClassifier.Type.MPESA -> {
                        if (mpesaAccountId.isNullOrBlank()) {
                            Log.w(
                                TAG,
                                "Skipping historical M-PESA SMS because M-PESA account mapping is not configured."
                            )
                            continue
                        }

                        transactionRepository.importMpesaSmsTransaction(
                            MpesaSmsImportRequest(
                                accountId = mpesaAccountId,
                                sms = message.body
                            )
                        )
                    }

                    SmsClassifier.Type.FULIZA -> {
                        if (mpesaAccountId.isNullOrBlank()) {
                            Log.w(
                                TAG,
                                "Skipping historical Fuliza SMS because M-PESA account mapping is not configured."
                            )
                            continue
                        }

                        transactionRepository.importFulizaTransaction(
                            FulizaImportRequest(
                                accountId = mpesaAccountId,
                                sms = message.body
                            )
                        )
                    }

                    SmsClassifier.Type.FULIZA_REPAYMENT -> {
                        if (mpesaAccountId.isNullOrBlank()) {
                            Log.w(
                                TAG,
                                "Skipping historical Fuliza repayment SMS because M-PESA account mapping is not configured."
                            )
                            continue
                        }

                        transactionRepository.importFulizaRepaymentSmsTransaction(
                            FulizaRepaymentSmsImportRequest(
                                accountId = mpesaAccountId,
                                sms = message.body
                            )
                        )
                    }

                    SmsClassifier.Type.BANK -> {
                        if (bankAccountId.isNullOrBlank()) {
                            Log.w(
                                TAG,
                                "Skipping historical bank SMS because bank account mapping is not configured."
                            )
                            continue
                        }

                        transactionRepository.importBankSmsTransaction(
                            BankSmsImportRequest(
                                accountId = bankAccountId,
                                sender = message.sender,
                                sms = message.body
                            )
                        )
                    }

                    else -> Unit
                }
            }

            smsSettingsStore.markInitialSyncCompleted()

            Log.d(
                TAG,
                "Initial SMS synchronization completed. Processed ${messages.size} relevant SMS messages."
            )

            Result.success()
        } catch (error: Exception) {
            val message = ApiErrorHandler.getMessage(
                error,
                "Initial SMS synchronization failed."
            )

            Log.e(TAG, message, error)

            if (isRetryable(error)) {
                Result.retry()
            } else {
                Result.failure()
            }
        }
    }

    private fun readRelevantSms(): List<HistoricalSms> {
        val cutoff = System.currentTimeMillis() - HISTORY_WINDOW_MILLIS

        val projection = arrayOf(
            Telephony.Sms.Inbox.ADDRESS,
            Telephony.Sms.Inbox.BODY,
            Telephony.Sms.Inbox.DATE
        )

        val selection = "${Telephony.Sms.Inbox.DATE} >= ?"
        val selectionArgs = arrayOf(cutoff.toString())
        val sortOrder = "${Telephony.Sms.Inbox.DATE} ASC"

        val messages = mutableListOf<HistoricalSms>()

        applicationContext.contentResolver.query(
            Telephony.Sms.Inbox.CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )?.use { cursor ->

            val addressIndex = cursor.getColumnIndex(Telephony.Sms.Inbox.ADDRESS)
            val bodyIndex = cursor.getColumnIndex(Telephony.Sms.Inbox.BODY)
            val dateIndex = cursor.getColumnIndex(Telephony.Sms.Inbox.DATE)

            if (addressIndex < 0 || bodyIndex < 0 || dateIndex < 0) {
                return@use
            }

            while (cursor.moveToNext()) {
                val sender = cursor.getString(addressIndex).orEmpty()
                val body = cursor.getString(bodyIndex).orEmpty()
                val receivedAt = cursor.getLong(dateIndex)

                if (body.isBlank()) {
                    continue
                }

                val type = SmsClassifier.classify(
                    sender = sender,
                    body = body
                )

                val preview = body
                    .replace(Regex("\\s+"), " ")
                    .trim()
                    .take(180)

                Log.d(
                    TAG,
                    "SMS candidate | sender=$sender | type=$type | receivedAt=$receivedAt | preview=$preview"
                )

                if (
                    type == SmsClassifier.Type.MPESA ||
                    type == SmsClassifier.Type.FULIZA ||
                    type == SmsClassifier.Type.FULIZA_REPAYMENT ||
                    type == SmsClassifier.Type.BANK
                ) {
                    messages += HistoricalSms(
                        sender = sender,
                        body = body,
                        receivedAt = receivedAt
                    )
                }
            }
        }

        return messages
    }

    private fun isRetryable(error: Throwable): Boolean {
        if (error is HttpException) {
            val code = error.code()
            return code == 408 || code == 429 || code >= 500
        }

        return error is java.io.IOException
    }

    private data class HistoricalSms(
        val sender: String,
        val body: String,
        val receivedAt: Long
    )

    companion object {
        private const val TAG = "InitialSmsSyncWorker"
        private const val HISTORY_WINDOW_MILLIS =
            90L * 24L * 60L * 60L * 1000L
    }
}
