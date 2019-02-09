package com.interswitchng.interswitchpossdk.shared.models.request

import com.interswitchng.interswitchpossdk.shared.models.posconfig.POSConfiguration
import com.interswitchng.interswitchpossdk.shared.models.PaymentInfo

internal data class TransactionInformation(
        val currencyCode: String,
        val merchantId: String,
        val merchantLocation: String,
        val posGeoCode: String,
        val terminalType: String,
        val uniqueId: String
) {

    companion object {

        internal fun from(config: POSConfiguration, paymentInfo: PaymentInfo) = TransactionInformation (
                currencyCode = "",
                merchantId = config.merchantId,
                merchantLocation = config.merchantLocation,
                posGeoCode = "",
                terminalType = config.terminalType,
                uniqueId = config.uniqueId
        )
    }
}