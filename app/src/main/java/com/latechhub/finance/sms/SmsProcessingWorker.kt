package com.latechhub.finance.sms

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.NetworkType
import androidx.work.Constraints
import com.latechhub.finance.data.local.FinanceDatabase
import com.latechhub.finance.data.local.PendingSmsRepository
import com.latechhub.finance.data.local.SmsSettingsStore
import com.latechhub.finance.data.remote.ApiErrorHandler
import com.latechhub.finance.data.remote.BankSmsImportRequest
import com.latechhub.finance.data.remote.FulizaImportRequest
import com.latechhub.finance.data.remote.FulizaRepaymentSmsImportRequest
import com.latechhub.finance.data.remote.MpesaSmsImportRequest
import com.latechhub.finance.data.remote.TransactionRepository
import kotlinx.coroutines.flow.first
import retrofit2.HttpException

class SmsProcessingWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    private val database = FinanceDatabase.getInstance(appContext)
    private val pendingSmsRepository =
        PendingSmsRepository(database.pendingSmsDao())
    private val smsSettingsStore = SmsSettingsStore(appContext)
    private val transactionRepository = TransactionRepository()

    override suspend fun doWork(): Result {
        val pendingSms = pendingSmsRepository.getPending(limit = BATCH_SIZE)

        if (pendingSms.isEmpty()) {
            return Result.success()
        }

        var shouldRetry = false

        for (sms in pendingSms) {
            try {
                pendingSmsRepository.markProcessing(sms.id)

                val type = SmsClassifier.classify(
                    sender = sms.sender,
                    body = sms.body
                )

                when (type) {
                    SmsClassifier.Type.MPESA -> {
                        val accountId = smsSettingsStore.mpesaAccountId.first()

                        if (accountId.isNullOrBlank()) {
                            pendingSmsRepository.markFailed(
                                sms.id,
                                "M-PESA account mapping is not configured."
                            )
                            continue
                        }

                        transactionRepository.importMpesaSmsTransaction(
                            MpesaSmsImportRequest(
                                accountId = accountId,
                                sms = sms.body
                            )
                        )
                    }

                    SmsClassifier.Type.FULIZA -> {
                        val accountId = smsSettingsStore.mpesaAccountId.first()

                        if (accountId.isNullOrBlank()) {
                            pendingSmsRepository.markFailed(
                                sms.id,
                                "M-PESA account mapping is not configured for Fuliza."
                            )
                            continue
                        }

                        transactionRepository.importFulizaTransaction(
                            FulizaImportRequest(
                                accountId = accountId,
                                sms = sms.body
                            )
                        )
                    }

                    SmsClassifier.Type.FULIZA_REPAYMENT -> {
                        val accountId = smsSettingsStore.mpesaAccountId.first()

                        if (accountId.isNullOrBlank()) {
                            pendingSmsRepository.markFailed(
                                sms.id,
                                "M-PESA account mapping is not configured for Fuliza repayment."
                            )
                            continue
                        }

                        transactionRepository.importFulizaRepaymentSmsTransaction(
                            FulizaRepaymentSmsImportRequest(
                                accountId = accountId,
                                sms = sms.body
                            )
                        )
                    }
                    SmsClassifier.Type.BANK -> {
                        val accountId = smsSettingsStore.bankAccountId.first()

                        if (accountId.isNullOrBlank()) {
                            pendingSmsRepository.markFailed(
                                sms.id,
                                "Bank account mapping is not configured."
                            )
                            continue
                        }

                        transactionRepository.importBankSmsTransaction(
                            BankSmsImportRequest(
                                accountId = accountId,
                                sender = sms.sender,
                                sms = sms.body
                            )
                        )
                    }

                    SmsClassifier.Type.UNKNOWN -> {
                        pendingSmsRepository.markFailed(
                            sms.id,
                            "Unable to classify SMS."
                        )
                        continue
                    }
                }

                pendingSmsRepository.markCompleted(sms.id)
                Log.d(TAG, "SMS processed successfully: ${sms.id}")

            } catch (error: Exception) {
                val message = ApiErrorHandler.getMessage(
                    error,
                    "SMS processing failed."
                )

                pendingSmsRepository.markFailed(
                    sms.id,
                    message
                )

                if (isRetryable(error)) {
                    shouldRetry = true
                }

                Log.e(
                    TAG,
                    "Failed to process SMS ${sms.id}: $message",
                    error
                )
            }
        }

        return if (shouldRetry) {
            Result.retry()
        } else {
            Result.success()
        }
    }

    private fun isRetryable(error: Throwable): Boolean {
        if (error is HttpException) {
            val code = error.code()
            return code == 408 || code == 429 || code >= 500
        }

        return error is java.io.IOException
    }

    companion object {
        private const val TAG = "SmsProcessingWorker"
        private const val BATCH_SIZE = 20
    }
}

