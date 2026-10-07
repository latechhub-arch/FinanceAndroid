package com.latechhub.finance.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "pending_sms",
    indices = [
        androidx.room.Index(value = ["fingerprint"], unique = true)
    ]
)
data class PendingSmsEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val sender: String,
    val body: String,
    val receivedAt: Long,
    val status: String = STATUS_PENDING,
    val attempts: Int = 0,
    val lastError: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val fingerprint: String
) {
    companion object {
        const val STATUS_PENDING = "PENDING"
        const val STATUS_PROCESSING = "PROCESSING"
        const val STATUS_COMPLETED = "COMPLETED"
        const val STATUS_FAILED = "FAILED"
    }
}
