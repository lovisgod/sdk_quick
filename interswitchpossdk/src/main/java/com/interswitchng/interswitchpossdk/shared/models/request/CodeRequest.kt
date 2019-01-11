package com.interswitchng.interswitchpossdk.shared.models.request

internal data class CodeRequest(
        val alias: String,
        val amount: String,
        val bankCode: String,
        val date: String,
        val stan: String,
        val terminalId: String,
        val transactionType: String,
        val qrFormat: String?,
        val additionalInformation: TransactionType
) {

    companion object {
        // qr request formats
        const val QR_FORMAT_BITMAP = "BITMAP"
        const val QR_FORMAT_RAW = "RAW"
        const val QR_FORMAT_FULL = "FULL"
    }
}