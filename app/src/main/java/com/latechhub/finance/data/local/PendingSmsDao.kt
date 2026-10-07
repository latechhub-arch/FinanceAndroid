package com.latechhub.finance.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface PendingSmsDao {

    @Insert
    suspend fun insert(sms: PendingSmsEntity): Long

    @Query(
        """
        SELECT * FROM pending_sms
        WHERE status IN ('PENDING', 'FAILED')
        ORDER BY receivedAt ASC
        LIMIT :limit
        """
    )
    suspend fun getPending(limit: Int = 20): List<PendingSmsEntity>

    @Query(
        """
        UPDATE pending_sms
        SET status = 'PROCESSING'
        WHERE id = :id
        """
    )
    suspend fun markProcessing(id: Long)

    @Query(
        """
        UPDATE pending_sms
        SET status = 'COMPLETED',
            lastError = NULL
        WHERE id = :id
        """
    )
    suspend fun markCompleted(id: Long)

    @Query(
        """
        UPDATE pending_sms
        SET status = 'FAILED',
            attempts = attempts + 1,
            lastError = :error
        WHERE id = :id
        """
    )
    suspend fun markFailed(id: Long, error: String)

    @Query("DELETE FROM pending_sms WHERE status = 'COMPLETED'")
    suspend fun deleteCompleted()

    @Query("SELECT COUNT(*) FROM pending_sms WHERE status IN ('PENDING', 'FAILED')")
    suspend fun getPendingCount(): Int
}
