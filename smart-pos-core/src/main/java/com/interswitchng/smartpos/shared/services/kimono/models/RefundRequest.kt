package com.interswitchng.smartpos.shared.services.kimono.models

//import com.interswitchng.smartpos.shared.services.DateUtils
import com.interswitchng.smartpos.IswPos
import com.interswitchng.smartpos.shared.models.core.Environment
import com.interswitchng.smartpos.shared.models.core.TerminalInfo
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.request.IccData
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.request.TransactionInfo
import com.interswitchng.smartpos.shared.services.iso8583.utils.DateUtils
import org.simpleframework.xml.Element
import org.simpleframework.xml.Root
import java.util.*


@Root(name = "refundRequest", strict = false)
internal class RefundRequest {
    @field:Element(name = "terminalInformation", required = false)
    var terminalInformation: RefundTerminalInformation? = null


    @field:Element(name = "cardData", required = false)
    var cardData: RefundCardData? = null

    @field:Element(name = "fromAccount", required = false)
    var fromAccount: String = ""


    @field:Element(name = "stan", required = false)
    var stan: String = ""

    @field:Element(name = "minorAmount", required = false)
    var minorAmount: String = ""


    @field:Element(name = "receivingInstitutionId", required = false)
    var receivingInstitutionId: String = ""


    @field:Element(name = "surcharge", required = false)
    var surcharge: String = ""


    @field:Element(name = "pinData", required = false)
    var pinData: RefundPinData? = null

    @field:Element(name = "keyLabel", required = false)
    var keyLabel: String = ""



    @field:Element(name = "originalAuthId", required = false)
    var originalAuth: String = ""


    companion object {
        fun create(deviceName: String, terminalInfo: TerminalInfo, transactionInfo: TransactionInfo,originalAuthId:String): RefundRequest {
            val hasPin = transactionInfo.cardPIN.isNotEmpty()
            val iswConfig = IswPos.getInstance().config

            return RefundRequest().apply {
                terminalInformation = RefundTerminalInformation.create(deviceName, terminalInfo, transactionInfo)
                cardData = RefundCardData.create(transactionInfo)
                fromAccount = transactionInfo.accountType.name
                minorAmount = transactionInfo.amount.toString()
                stan = transactionInfo.stan
                pinData = if (hasPin) RefundPinData.create(transactionInfo) else null
                keyLabel = if (iswConfig.environment == Environment.Test) "000006" else "000002"
                originalAuth=originalAuthId
            }
        }
    }
}










@Root(name = "TerminalInformation", strict = false)
internal class RefundTerminalInformation {
    @field:Element(name = "batteryInformation", required = false)
    var batteryInformation: String = ""
    @field:Element(name = "currencyCode", required = false)
    var currencyCode: String = ""
    @field:Element(name = "languageInfo", required = false)
    var languageInfo: String = ""
    @field:Element(name = "merchantId", required = false)
    var merchantId: String = ""
    // TODO confirm if this is no a typo <merhcantLocation></merhcantLocation>
    @field:Element(name = "merhcantLocation", required = false)
    var merchantLocation: String = ""
    @field:Element(name = "posConditionCode", required = false)
    var posConditionCode: String = ""
    @field:Element(name = "posDataCode", required = false)
    var posDataCode: String = ""
    @field:Element(name = "posEntryMode", required = false)
    var posEntryMode: String = ""
    @field:Element(name = "posGeoCode", required = false)
    var posGeoCode: String = ""
    @field:Element(name = "printerStatus", required = false)
    var printerStatus: String = ""
    @field:Element(name = "terminalId", required = false)
    var terminalId: String = ""
    @field:Element(name = "terminalType", required = false)
    var terminalType: String = ""
    @field:Element(name = "transmissionDate", required = false)
    var transmissionDate: String = ""
    @field:Element(name = "uniqueId", required = false)
    var uniqueId: String = ""
    @field:Element(name = "cardAcceptorId", required = false)
    var cardAcceptorId: String = ""
    @field:Element(name = "cellStationId", required = false)
    var cellStationId: String = ""


    companion object {
        fun create(deviceName: String, terminalInfo: TerminalInfo, transactionInfo: TransactionInfo): RefundTerminalInformation {
            val battery = "-1"
            val date = DateUtils.universalDateFormat.format(Date())
            val hasPin = transactionInfo.cardPIN.isNotEmpty()

            return RefundTerminalInformation().apply {
                batteryInformation = battery
                currencyCode = terminalInfo.currencyCode
                languageInfo = "EN"
                merchantId = terminalInfo.merchantId
                merchantLocation = terminalInfo.merchantNameAndLocation
                posConditionCode = "00"
                posEntryMode = "051"
                printerStatus = "1"
                terminalId = terminalInfo.terminalId
                transmissionDate = date
                uniqueId = "00000${transactionInfo.stan}"
                terminalType = deviceName.toUpperCase()
                posDataCode = if (hasPin) "510101511344101" else "511101511344101"
                cardAcceptorId = terminalInfo.merchantId
                cellStationId = "00"
                posGeoCode = "00234000000000566"
            }
        }
    }
}

@Root(name = "CardData", strict = false)
internal class RefundCardData {
    @field:Element(name = "cardSequenceNumber", required = false)
    var cardSequenceNumber: String = ""

    @field:Element(name = "emvData", required = false)
    var emvData: IccData? = null
    @field:Element(name = "mifareData", required = false)
    var mifareData: MiFareData? = null
    @field:Element(name = "track2", required = false)
    var track2: Track2? = null
    @field:Element(name = "wasFallback", required = false)
    var wasFallback: Boolean = false

    companion object {
        fun create(transactionInfo: TransactionInfo) = RefundCardData().apply {
            cardSequenceNumber = transactionInfo.csn



            //TODO: reenable to iccdata
//            emvData = transactionInfo.iccData



            track2 = Track2().apply {
                pan = transactionInfo.cardPAN
                expiryMonth = transactionInfo.cardExpiry.takeLast(2)
                expiryYear = transactionInfo.cardExpiry.take(2)
                track2 = let {
                    val neededLength = transactionInfo.cardTrack2.length - 2
                    val isVisa = transactionInfo.cardTrack2.startsWith('4')
                    val hasCharacter = transactionInfo.cardTrack2.last().isLetter()

                    // remove character suffix for visa
                    if (isVisa && hasCharacter) transactionInfo.cardTrack2.substring(0..neededLength)
                    else transactionInfo.cardTrack2
                }
            }

        }
    }
}



@Root(name = "pinData", strict = false)
internal class RefundPinData {
    @field:Element(name = "ksnd", required = false)
    var ksnd: String = "605"
    @field:Element(name = "pinType", required = false)
    var pinType: String = "Dukpt"


    @field:Element(name = "ksn", required = false)
    var ksn: String = ""
    @field:Element(name = "pinBlock", required = false)
    var pinBlock: String = ""

    companion object {
        fun create(txnInfo: TransactionInfo) = RefundPinData().apply {
            //ToDo: re enable the pinKsn
//            ksn = txnInfo.pinKsn
            pinBlock = txnInfo.cardPIN

        }

    }
}
