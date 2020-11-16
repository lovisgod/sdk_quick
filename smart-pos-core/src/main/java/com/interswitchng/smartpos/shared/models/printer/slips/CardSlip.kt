package com.interswitchng.smartpos.shared.models.printer.slips

import com.interswitchng.smartpos.shared.models.core.TerminalInfo
import com.interswitchng.smartpos.shared.models.posconfig.PrintObject
import com.interswitchng.smartpos.shared.models.posconfig.PrintStringConfiguration
import com.interswitchng.smartpos.shared.models.printer.info.TransactionInfo
import com.interswitchng.smartpos.shared.models.printer.info.TransactionStatus
import com.interswitchng.smartpos.shared.models.printer.info.TransactionType
import com.interswitchng.smartpos.shared.utilities.DisplayUtils


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
    override fun getTransactionInfo(reprint: Boolean): List<PrintObject> {


        val typeConfig = PrintStringConfiguration(isTitle = true, isBold = true, displayCenter = true)

        var withdrawalTag = "Withdrawal"

        if(info.type == TransactionType.CashOutInquiry){
            withdrawalTag = "Inquiry"
        }

        val txnType = pairString("", withdrawalTag , stringConfig = typeConfig)

        val paymentType = pairString("channel", info.paymentType.toString())
        val stan = pairString("stan", info.stan.padStart(6, '0'))
        val date = pairString("date", info.dateTime.take(10))
        val time = pairString("time", info.dateTime.substring(11, 19))


        val reprintConfig = PrintStringConfiguration(displayCenter = true, isBold = true, isTitle = true)
        val rePrintFlag = pairString("","*** Re-Print ***",stringConfig = reprintConfig )

        val amount = pairString("amount", DisplayUtils.getAmountWithCurrency(info.amount))

        val list = mutableListOf(txnType, paymentType, date, time, line,rePrintFlag, amount,rePrintFlag,line)

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
            val cardType = pairString("card type", info.cardType + "card")
            val cardPan = pairString("card pan", pan, stringConfig = panConfig)
            val cardExpiry = pairString("expiry date", info.cardExpiry)

            list.addAll(listOf(cardType, cardPan, cardExpiry, stan, line))

        }

        if(!reprint){
            list.removeAll(listOf(rePrintFlag))
        }

        // return transaction info of slip
        return list
    }

}