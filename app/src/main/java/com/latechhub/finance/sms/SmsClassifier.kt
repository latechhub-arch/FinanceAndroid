package com.latechhub.finance.sms

object SmsClassifier {

    enum class Type {
        MPESA,
        FULIZA,
        FULIZA_REPAYMENT,
        BANK,
        UNKNOWN
    }

    private val bankTransactionSenders = setOf(
        "COOPBANK",
        "KCB",
        "EQUITY",
        "EQUITEL",
        "EQUITY BANK",
        "FAMILYBANK",
        "IANDMBANK",
        "NCBA_BANK",
        "DTB",
        "STANBIC",
        "ABSABANK"
    )

    fun classify(sender: String, body: String): Type {
        val normalizedSender = sender.trim().uppercase()
        val normalizedBody = body.trim().uppercase()

        if (
            normalizedBody.contains("FROM YOUR M-PESA HAS BEEN USED TO") &&
            normalizedBody.contains("PAY YOUR OUTSTANDING FULIZA M-PESA")
        ) {
            return Type.FULIZA_REPAYMENT
        }

        if (
            normalizedBody.matches(Regex("^[A-Z0-9]+\\s+CONFIRMED\\..*")) &&
            normalizedBody.contains("FULIZA M-PESA AMOUNT IS") &&
            normalizedBody.contains("TOTAL FULIZA M-PESA OUTSTANDING AMOUNT IS")
        ) {
            return Type.FULIZA
        }

        if (normalizedBody.matches(Regex("^[A-Z0-9]+\\s+CONFIRMED\\..*"))) {
            return Type.MPESA
        }

        if (normalizedSender in bankTransactionSenders) {
            return Type.BANK
        }

        return Type.UNKNOWN
    }
}
