package com.latechhub.finance.data.local

import java.security.MessageDigest

class PendingSmsRepository(
    private val dao: PendingSmsDao
) {

    suspend fun enqueue(
        sender: String,
        body: String,
        receivedAt: Long
    ): Long {
        val fingerprint = createFingerprint(sender, body, receivedAt)

        return dao.insert(
            PendingSmsEntity(
                sender = sender,
                body = body,
                receivedAt = receivedAt,
                fingerprint = fingerprint
            )
        )
    }

    suspend fun getPending(limit: Int = 20): List<PendingSmsEntity> {
        return dao.getPending(limit)
    }

    suspend fun markProcessing(id: Long) {
        dao.markProcessing(id)
    }

    suspend fun markCompleted(id: Long) {
        dao.markCompleted(id)
    }

    suspend fun markFailed(id: Long, error: String) {
        dao.markFailed(id, error)
    }

    suspend fun deleteCompleted() {
        dao.deleteCompleted()
    }

    suspend fun getPendingCount(): Int {
        return dao.getPendingCount()
    }

    private fun createFingerprint(
        sender: String,
        body: String,
        receivedAt: Long
    ): String {
        val input = "$sender|$body|$receivedAt"
        val digest = MessageDigest.getInstance("SHA-256")
            .digest(input.toByteArray())

        return digest.joinToString("") { "%02x".format(it) }
    }
}
