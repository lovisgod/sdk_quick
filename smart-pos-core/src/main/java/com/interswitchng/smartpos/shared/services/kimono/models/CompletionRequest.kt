package com.interswitchng.smartpos.shared.services.kimono.models

import com.interswitchng.smartpos.BuildConfig
import com.interswitchng.smartpos.IswPos
import com.interswitchng.smartpos.shared.models.core.Environment
import com.interswitchng.smartpos.shared.models.core.TerminalInfo
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.request.IccData
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.request.TransactionInfo
import com.interswitchng.smartpos.shared.services.iso8583.utils.DateUtils
//import com.interswitchng.smartpos.shared.services.DateUtils
import org.simpleframework.xml.Element
import org.simpleframework.xml.Root
import java.util.*


@Root(name = "completionRequest", strict = false)
internal class CompletionRequest {
    @field:Element(name = "terminalInformation", required = false)
    var terminalInformation: TerminalInformation? = null
    @field:Element(name = "cardData", required = false)
    var cardData: CardData? = null
    @field:Element(name = "pinData", required = false)
    var pinData: PinData? = null


    @field:Element(name = "stan", required = false)
    var stan: String = ""

    @field:Element(name = "minorAmount", required = false)
    var minorAmount: String = ""

    @field:Element(name = "rate", required = false)
    var rate: String = ""
    @field:Element(name = "amountSettlement", required = false)
    var amountSettlement: String = ""
    @field:Element(name = "settlementFee", required = false)
    var settlementFee: String = ""
    @field:Element(name = "settlementCurrencyCode", required = false)
    var settlementCurrencyCode: String = ""
    @field:Element(name = "tmsConfiguredTerminalLocation", required = false)
    var tmsConfiguredTerminalLocation: String = ""


    @field:Element(name = "originalAuthRef", required = false)
    var originalAuthRef: String = ""
    @field:Element(name = "originalDateTime", required = false)
    var originalDateTime: String = ""

    @field:Element(name = "notDisposable", required = false)
    var notDisposable: String = ""







    @field:Element(name = "keyLabel", required = false)
    var keyLabel: String = ""


    companion object {
        fun create(deviceName: String, terminalInfo: TerminalInfo, transactionInfo: TransactionInfo): CompletionRequest {
            val hasPin = transactionInfo.cardPIN.isNotEmpty()
            val iswConfig = IswPos.getInstance().config

            return CompletionRequest().apply {
//                terminalInformation = TerminalInformation.create(deviceName, terminalInfo, transactionInfo)
//                cardData = CardData.create(transactionInfo)
//
//                minorAmount = transactionInfo.amount.toString()
//                stan = transactionInfo.stan
//                pinData = if (hasPin) PinData.create(transactionInfo) else null
//                keyLabel = if (iswConfig.environment == Environment.Test) "000006" else "000002"
            }
        }
    }
}

