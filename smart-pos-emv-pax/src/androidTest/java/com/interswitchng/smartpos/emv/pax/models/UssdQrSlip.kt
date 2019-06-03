package com.interswitchng.smartpos.emv.pax.models

import com.interswitchng.smartpos.shared.models.posconfig.PrintObject

internal class UssdQrSlip(config: POSConfiguration, status: TransactionStatus, private val info: TransactionInfo): TransactionSlip(config, status) {


    override fun getTransactionInfo(): List<PrintObject> {

        val stan = pairString("stan", info.stan)
        val date = pairString("date", info.dateTime)
        val amount = pairString("amount", info.amount)
        val authCode = pairString("authentication code", info.authorizationCode)
        val pinStatus = PrintObject.Data(info.pinStatus)

        return listOf(stan, date, amount, authCode, pinStatus,  PrintObject.Line)
    }

}
