package com.interswitchng.interswitchpossdk.shared.models.printslips.slips

import com.interswitchng.interswitchpossdk.shared.models.TerminalInfo
import com.interswitchng.interswitchpossdk.shared.models.posconfig.POSConfiguration
import com.interswitchng.interswitchpossdk.shared.models.posconfig.PrintObject
import com.interswitchng.interswitchpossdk.shared.models.posconfig.PrintStringConfiguration
import com.interswitchng.interswitchpossdk.shared.models.printslips.info.TransactionInfo
import com.interswitchng.interswitchpossdk.shared.models.printslips.info.TransactionStatus
import java.text.NumberFormat

internal class UssdQrSlip(terminal: TerminalInfo, status: TransactionStatus, private val info: TransactionInfo): TransactionSlip(terminal, status) {


    override fun getTransactionInfo(): List<PrintObject> {

        val numberFormat = NumberFormat.getInstance()
        numberFormat.minimumFractionDigits = 2
        numberFormat.maximumFractionDigits = 2

        val txnAmount = numberFormat.format(info.amount.toDouble())

        val stan = pairString("stan", info.stan)
        val date = pairString("date", info.dateTime)
        val amount = pairString("amount", txnAmount)
        val authCode = pairString("authentication code", info.authorizationCode)
        val pinStatus = pairString("", info.pinStatus)


        val typeConfig = PrintStringConfiguration(isTitle = true, isBold = true, displayCenter = true)
        val txnType = pairString("", info.type.toString(), stringConfig = typeConfig)


        val line = PrintObject.Line()
        // return transaction info of slip
        return listOf(txnType, stan, date, line, amount, line, authCode, pinStatus, line)
    }

}
