package com.interswitchng.interswitchpossdk.shared.models.request

import com.interswitchng.interswitchpossdk.shared.models.posconfig.POSConfiguration
import com.interswitchng.interswitchpossdk.shared.models.PaymentInfo
import com.interswitchng.interswitchpossdk.shared.utilities.DisplayUtils
import java.util.*

internal data class CodeRequest(
        val alias: String,
        val amount: String,
        val bankCode: String?,
        val date: String,
        val stan: String,
        val terminalId: String,
        val transactionType: String,
        val qrFormat: String?,
        val additionalInformation: TransactionInformation
) {

    companion object {
        // transaction types
        internal const val TRANSACTION_QR = "QR"
        internal const val TRANSACTION_USSD = "USSD"

        // qr request formats
        internal const val QR_FORMAT_BITMAP = "BITMAP"
        internal const val QR_FORMAT_RAW = "RAW"
        internal const val QR_FORMAT_FULL = "FULL"

        internal fun from(config: POSConfiguration, paymentInfo: PaymentInfo, transactionType: String, qrFormat: String? = null) = CodeRequest (
                alias = config.alias,
                amount = "${paymentInfo.amount * 100}",
                bankCode = paymentInfo.bankCode,
                date = DisplayUtils.getIsoString(Date()),
                stan = paymentInfo.stan,
                terminalId = config.terminalId,
                transactionType = transactionType,
                qrFormat = qrFormat,
                additionalInformation = TransactionInformation.from(config, paymentInfo)
        )
    }
}