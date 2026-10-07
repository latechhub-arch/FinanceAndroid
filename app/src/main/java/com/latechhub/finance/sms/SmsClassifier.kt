package com.latechhub.finance.sms

object SmsClassifier {

    enum class Type {
        MPESA,
        FULIZA,
        FULIZA_REPAYMENT,
        BANK,
        UNKNOWN
    }

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
            normalizedBody.contains("FULIZA") ||
            normalizedBody.contains("FULIZA LOAN") ||
            normalizedBody.contains("FULIZA OUTSTANDING")
        ) {
            return Type.FULIZA
        }

        if (
            normalizedSender.contains("MPESA") ||
            normalizedSender.contains("SAFARICOM") ||
            normalizedBody.contains("M-PESA")
        ) {
            return Type.MPESA
        }

        if (normalizedBody.matches(Regex("^[A-Z0-9]+\\s+CONFIRMED\\..*"))) {
            return Type.MPESA
        }

        if (normalizedSender.isNotBlank()) {
            return Type.BANK
        }

        return Type.UNKNOWN
    }
}
