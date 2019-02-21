package com.interswitchng.smartpos.shared.models.printslips.slips

import com.interswitchng.smartpos.shared.models.core.TerminalInfo
import com.interswitchng.smartpos.shared.models.posconfig.PrintObject
import com.interswitchng.smartpos.shared.models.posconfig.PrintStringConfiguration
import com.interswitchng.smartpos.shared.models.printslips.info.TransactionStatus
import com.interswitchng.smartpos.shared.services.iso8583.utils.IsoUtils

internal abstract class TransactionSlip(private val terminal: TerminalInfo, private val status: TransactionStatus) {
    protected val line = PrintObject.Line()

    protected fun pairString(title: String, value: String, hasNewLine: Boolean = false, isUpperCase: Boolean = true, stringConfig: PrintStringConfiguration = PrintStringConfiguration()): PrintObject {
        // get title formatted
        val titleCopy = when(title.isEmpty()) {
            true -> ""
            else -> "$title: "
        }

        // get formatted pair
        var result = when(hasNewLine) {
            true -> "$titleCopy\n$value"
            else -> "$titleCopy$value"
        }

        // append new line if not display center
        result = when(stringConfig.displayCenter) {
            true -> result
            else -> "$result\n"
        }


        result = when(isUpperCase) {
            true -> result.toUpperCase()
            else -> result
        }

        return PrintObject.Data(result, stringConfig)
    }

    internal fun getTerminalInfo(): List<PrintObject> {
        val merchantName = pairString("merchant", terminal.merchantNameAndLocation)
        val terminalId = pairString("Terminal Id", terminal.terminalId)

        return listOf(merchantName, terminalId, line)
    }


    internal fun getTransactionStatus(): List<PrintObject> {
        val responseMsg = pairString("", status.responseMessage, stringConfig = PrintStringConfiguration(isTitle = true, displayCenter = true))
        val printList = mutableListOf(responseMsg)

        if (status.responseCode.isNotEmpty() && status.responseCode != IsoUtils.TIMEOUT_CODE) {
            val responseCode = pairString("response code", status.responseCode)
            printList.add(responseCode)
        }

        if (status.AID.isNotEmpty()) {
            val aid = pairString("AID", status.AID)
            printList.add(aid)
        }

        val tel = pairString("TEL", status.telephone)
        return printList + listOf(tel, line)
    }


    internal abstract fun getTransactionInfo(): List<PrintObject>

    internal fun getSlipItems() = getTerminalInfo() + getTransactionInfo() + getTransactionStatus()
}