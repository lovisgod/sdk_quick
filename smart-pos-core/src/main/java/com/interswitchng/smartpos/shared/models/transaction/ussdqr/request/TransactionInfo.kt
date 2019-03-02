package com.interswitchng.smartpos.shared.models.transaction.ussdqr.request

import com.interswitchng.smartpos.shared.models.transaction.PaymentInfo
import com.interswitchng.smartpos.shared.models.core.TerminalInfo

internal data class TransactionInfo(
        val currencyCode: String,
        val merchantId: String,
        val merchantLocation: String,
        val posGeoCode: String,
        val terminalType: String,
        val uniqueId: String
) {

    companion object {

        internal fun from(terminalInfo: TerminalInfo, paymentInfo: PaymentInfo) = TransactionInfo (
                currencyCode = terminalInfo.currencyCode,
                merchantId = terminalInfo.merchantId,
                merchantLocation = terminalInfo.merchantNameAndLocation,
                posGeoCode = terminalInfo.countryCode,
                terminalType = "Android",
                uniqueId =  paymentInfo.currentStan
        )
    }
}