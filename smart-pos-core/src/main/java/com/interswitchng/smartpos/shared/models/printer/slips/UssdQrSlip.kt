package com.interswitchng.smartpos.shared.models.printer.slips

import com.interswitchng.smartpos.shared.models.core.TerminalInfo
import com.interswitchng.smartpos.shared.models.posconfig.PrintObject
import com.interswitchng.smartpos.shared.models.posconfig.PrintStringConfiguration
import com.interswitchng.smartpos.shared.models.printer.info.TransactionInfo
import com.interswitchng.smartpos.shared.models.printer.info.TransactionStatus
import java.text.NumberFormat

internal class UssdQrSlip(terminal: TerminalInfo, status: TransactionStatus, private val info: TransactionInfo, private val code: PrintObject?): TransactionSlip(terminal, status) {


    override fun getTransactionInfo(): List<PrintObject> {

        val numberFormat = NumberFormat.getInstance()
        numberFormat.minimumFractionDigits = 2
        numberFormat.maximumFractionDigits = 2


        val stan = pairString("stan", info.stan)
        val date = pairString("date", info.dateTime)
        val amount = pairString("amount", info.amount)
        val codeTitle = pairString("code","", hasNewLine = true)

        val typeConfig = PrintStringConfiguration(isTitle = true, isBold = true, displayCenter = true)
        val txnType = pairString("", info.type.toString(), stringConfig = typeConfig)

        val halfList = listOf(txnType, stan, date, line, amount, line)
        val fullList = when(code) {
            null -> halfList
            else -> halfList + codeTitle + code + line
        }

        // return transaction info of slip
        return fullList

    }

}
