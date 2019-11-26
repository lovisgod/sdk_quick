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


@Root(name = "reservationRequest", strict = false)
internal class ReservationRequest {
    @field:Element(name = "terminalInformation", required = false)
    var terminalInformation: TerminalInformation? = null
    @field:Element(name = "cardData", required = false)
    var cardData: CardData? = null
    @field:Element(name = "pinData", required = false)
    var pinData: PinData? = null


    @field:Element(name = "stan", required = false)
    var stan: String = ""

    @field:Element(name = "fromAccount", required = false)
    var fromAccount: String = ""

    @field:Element(name = "minorAmount", required = false)
    var minorAmount: String = ""


    @field:Element(name = "tmsConfiguredTerminalLocation", required = false)
    var tmsConfiguredTerminalLocation: String = ""



    @field:Element(name = "keyLabel", required = false)
    var keyLabel: String = ""


    companion object {
        fun create(deviceName: String, terminalInfo: TerminalInfo, transactionInfo: TransactionInfo): ReservationRequest {
            val hasPin = transactionInfo.cardPIN.isNotEmpty()
            val iswConfig = IswPos.getInstance().config

            return ReservationRequest().apply {
//                terminalInformation = TerminalInformation.create(deviceName, terminalInfo, transactionInfo)
//                cardData = CardData.create(transactionInfo)
//                fromAccount = transactionInfo.accountType.name
//                minorAmount = transactionInfo.amount.toString()
//                stan = transactionInfo.stan
//                pinData = if (hasPin) PinData.create(transactionInfo) else null
//                keyLabel = if (iswConfig.environment == Environment.Test) "000006" else "000002"
//                tmsConfiguredTerminalLocation=""
            }
        }
    }
}

