package com.interswitchng.smartpos.shared.models.printer.slips

import com.interswitchng.smartpos.shared.models.core.TerminalInfo
import com.interswitchng.smartpos.shared.models.posconfig.PrintObject
import com.interswitchng.smartpos.shared.models.posconfig.PrintStringConfiguration
import com.interswitchng.smartpos.shared.models.printer.info.TransactionInfo
import com.interswitchng.smartpos.shared.models.printer.info.TransactionStatus


/**
 * @inherit
 *
 * This class is responsible for  generating a print slip
 * for card and paycode transactions
 *
 * @param info the information concerning the current transaction
 */
internal class CardSlip(terminal: TerminalInfo, status: TransactionStatus, private val info: TransactionInfo) : TransactionSlip(terminal, status) {


    /**
     * @inherit
     */
    override fun getTransactionInfo(): List<PrintObject> {


        val typeConfig = PrintStringConfiguration(isTitle = true, isBold = true, displayCenter = true)
        val txnType = pairString("", info.type.toString(), stringConfig = typeConfig)
        val paymentType = pairString("channel", info.paymentType.toString())
        val stan = pairString("stan", info.stan)
        val date = pairString("date", info.dateTime)
        val dateTime = pairString("Date Time", info.originalDateTime)
        val amount = pairString("amount", info.amount)
        val authCode = pairString("authentication code", info.authorizationCode)
        val list = mutableListOf(txnType, paymentType, stan, date, dateTime, line, amount, line)

        // check if its card transaction
        if (info.cardPan.isNotEmpty()) {
            val pan = run {
                val length = info.cardPan.length
                if (length < 10) return@run ""
                val firstFour = info.cardPan.substring(0..3)
                val middle = "*".repeat(length - 8)
                val lastFour = info.cardPan.substring(length - 4 until length)
                return@run "$firstFour$middle$lastFour"
            }

            val panConfig = PrintStringConfiguration(isBold = true)
            val cardPan = pairString(info.cardType, pan, true, stringConfig = panConfig)
            val cardExpiry = pairString("expiry date", info.cardExpiry)
            val pinStatus = pairString("", info.pinStatus)

            list.addAll(listOf(cardPan, cardExpiry, authCode, pinStatus, line))
        }


        // return transaction info of slip
        return list
    }

}