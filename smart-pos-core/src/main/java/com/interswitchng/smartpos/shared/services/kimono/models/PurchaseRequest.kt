package com.interswitchng.smartpos.shared.services.kimono.models

//import com.interswitchng.smartpos.shared.services.DateUtils
import com.interswitchng.smartpos.IswPos
import com.interswitchng.smartpos.shared.interfaces.device.POSDevice
import com.interswitchng.smartpos.shared.models.core.Environment
import com.interswitchng.smartpos.shared.models.core.TerminalInfo
import com.interswitchng.smartpos.shared.models.transaction.PaymentInfo
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.request.IccData
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.request.TransactionInfo
import com.interswitchng.smartpos.shared.services.iso8583.utils.DateUtils
import com.interswitchng.smartpos.shared.services.iso8583.utils.IsoUtils
import org.simpleframework.xml.Element
import org.simpleframework.xml.Root
import java.util.*




internal class PurchaseRequest
{
    companion object {
        fun toCardPurchaseString(device:POSDevice, terminalInfo: TerminalInfo, transaction: TransactionInfo): String {


            val hasPin = transaction.cardPIN.isNotEmpty()

            var pinData=""

            var dedicatedFileTag=""




            val amount = String.format(Locale.getDefault(), "%012d", transaction.amount)
            val now = Date()
            val date = DateUtils.dateFormatter.format(now)
            var icc= getIcc(terminalInfo,amount,date,transaction)

            val iswConfig = IswPos.getInstance().config
            var keyLabel=if (iswConfig.environment == Environment.Test) "000006" else "000002"


            var  track2 = let {
                val neededLength = transaction.cardTrack2.length - 2
                val isVisa = transaction.cardTrack2.startsWith('4')
                val hasCharacter = transaction.cardTrack2.last().isLetter()

                // remove character suffix for visa
                if (isVisa && hasCharacter) transaction.cardTrack2.substring(0..neededLength)
                else transaction.cardTrack2
            }

        if(hasPin)

                pinData= """<pinData><ksnd>605</ksnd><pinType>Dukpt</pinType> </pinData>"""

            if(false)
                dedicatedFileTag=  """"<DedicatedFileName>${icc.DEDICATED_FILE_NAME}</DedicatedFileName>"""


            val requestBody = """<?xml version="1.0" encoding="UTF-8" ?><purchaseRequest>
                <terminalInformation><batteryInformation>-1</batteryInformation> <currencyCode>${terminalInfo.currencyCode}</currencyCode><languageInfo>EN</languageInfo><merchantId>${terminalInfo.merchantId}</merchantId><merhcantLocation>${terminalInfo.merchantNameAndLocation}</merhcantLocation> <posConditionCode>00</posConditionCode> <posDataCode>${if (hasPin) "510101511344101" else "511101511344101"}</posDataCode> <posEntryMode>051</posEntryMode> <posGeoCode>00234000000000566</posGeoCode> <printerStatus>1</printerStatus><terminalId>${terminalInfo.terminalId}</terminalId> <terminalType>${device.name}</terminalType> <transmissionDate>${DateUtils.universalDateFormat.format(Date())}</transmissionDate> <uniqueId>${icc.INTERFACE_DEVICE_SERIAL_NUMBER}</uniqueId></terminalInformation><cardData><cardSequenceNumber>01</cardSequenceNumber> <track2><pan>${transaction.cardPAN}</pan> <expiryMonth>${transaction.cardExpiry.takeLast(2)}</expiryMonth> <expiryYear>${transaction.cardExpiry.take(2)}</expiryYear> <track2>${track2}</track2></track2><emvData><AmountAuthorized>000000000000</AmountAuthorized> <AmountOther>${icc.ANOTHER_AMOUNT}</AmountOther> <ApplicationInterchangeProfile>${icc.APPLICATION_INTERCHANGE_PROFILE}</ApplicationInterchangeProfile> <atc>${icc.APPLICATION_TRANSACTION_COUNTER}</atc><Cryptogram>${icc.AUTHORIZATION_REQUEST}</Cryptogram> <CryptogramInformationData>${icc.CRYPTOGRAM_INFO_DATA}</CryptogramInformationData> <CvmResults>${icc.CARD_HOLDER_VERIFICATION_RESULT}</CvmResults><iad>${icc.ISSUER_APP_DATA}</iad> <TransactionCurrencyCode>${icc.TRANSACTION_CURRENCY_CODE}</TransactionCurrencyCode> <TerminalVerificationResult>${icc.TERMINAL_VERIFICATION_RESULT}</TerminalVerificationResult> <TerminalCountryCode>${icc.TERMINAL_COUNTRY_CODE}</TerminalCountryCode> <TerminalType>${icc.TERMINAL_TYPE}</TerminalType> <TerminalCapabilities>${icc.TERMINAL_CAPABILITIES}</TerminalCapabilities> <TransactionDate>${icc.TRANSACTION_DATE}</TransactionDate> <TransactionType>${icc.TRANSACTION_TYPE}</TransactionType> <UnpredictableNumber>${icc.UNPREDICTABLE_NUMBER}</UnpredictableNumber> ${dedicatedFileTag}</emvData></cardData><fromAccount>${transaction.accountType.name}</fromAccount> <stan>${transaction.stan}</stan> <minorAmount>${ transaction.amount.toString()}</minorAmount> ${pinData} <keyLabel>${keyLabel}</keyLabel></purchaseRequest>
        """

            return requestBody

        }


        fun toReversal(device:POSDevice,terminalInfo: TerminalInfo, transaction: TransactionInfo):String
        {
            val hasPin = transaction.cardPIN.isNotEmpty()
            var pinData=""
            if(hasPin) pinData= """<pinData><ksnd>605</ksnd><pinBlock></pinBlock><pinType>Dukpt</pinType> </pinData>"""
            var dedicatedFileTag=""

            val amount = String.format(Locale.getDefault(), "%012d", transaction.amount)
            val now = Date()
            val date = DateUtils.dateFormatter.format(now)
            var icc= getIcc(terminalInfo,amount,date,transaction)
            var transactionCurrencyCode= icc.TRANSACTION_CURRENCY_CODE
            var terminalCOuntryCode=icc.TERMINAL_COUNTRY_CODE
            var originalStan=transaction.originalTransactionInfoData?.originalStan
            var originalAuthId=transaction.originalTransactionInfoData?.originalAuthorizationId
            var settlementCurrencyCode=icc.TRANSACTION_CURRENCY_CODE
            val iswConfig = IswPos.getInstance().config
            var keyLabel=if (iswConfig.environment == Environment.Test) "000006" else "000002"




            if(false)
                dedicatedFileTag=  """"<DedicatedFileName>${icc.DEDICATED_FILE_NAME}</DedicatedFileName>"""

            var  track2 = let {
                val neededLength = transaction.cardTrack2.length - 2
                val isVisa = transaction.cardTrack2.startsWith('4')
                val hasCharacter = transaction.cardTrack2.last().isLetter()

                // remove character suffix for visa
                if (isVisa && hasCharacter) transaction.cardTrack2.substring(0..neededLength)
                else transaction.cardTrack2
            }
            return  """<reversalRequestWithoutOriginalDate><terminalInformation><batteryInformation>-1</batteryInformation> <currencyCode>${terminalInfo.currencyCode}</currencyCode><languageInfo>EN</languageInfo><merchantId>${terminalInfo.merchantId}</merchantId><merhcantLocation>${terminalInfo.merchantNameAndLocation}</merhcantLocation> <posConditionCode>00</posConditionCode> <posDataCode>${if (hasPin) "510101511344101" else "511101511344101"}</posDataCode> <posEntryMode>051</posEntryMode> <posGeoCode>00234000000000566</posGeoCode> <printerStatus>1</printerStatus><terminalId>${terminalInfo.terminalId}</terminalId> <terminalType>${device.name}</terminalType> <transmissionDate>${DateUtils.universalDateFormat.format(Date())}</transmissionDate> <uniqueId>${icc.INTERFACE_DEVICE_SERIAL_NUMBER}</uniqueId></terminalInformation><cardData><cardSequenceNumber>01</cardSequenceNumber><track2><pan>${transaction.cardPAN}</pan> <expiryMonth>${transaction.cardExpiry.takeLast(2)}</expiryMonth> <expiryYear>${transaction.cardExpiry.take(2)}</expiryYear> <track2>${track2}</track2></track2><wasFallback></wasFallback><emvData><AmountAuthorized>000000010000</AmountAuthorized> <AmountOther>${icc.ANOTHER_AMOUNT}</AmountOther><ApplicationInterchangeProfile>${icc.APPLICATION_INTERCHANGE_PROFILE}</ApplicationInterchangeProfile> <atc>${icc.APPLICATION_TRANSACTION_COUNTER}</atc><Cryptogram>${icc.AUTHORIZATION_REQUEST}</Cryptogram> <CryptogramInformationData>${icc.CRYPTOGRAM_INFO_DATA}</CryptogramInformationData> <CvmResults>${icc.CARD_HOLDER_VERIFICATION_RESULT}</CvmResults> <iad>${icc.ISSUER_APP_DATA}</iad> <TransactionCurrencyCode>${transactionCurrencyCode}</TransactionCurrencyCode><TerminalVerificationResult>${icc.TERMINAL_VERIFICATION_RESULT}</TerminalVerificationResult> <TerminalCountryCode>${terminalCOuntryCode}</TerminalCountryCode><TerminalType>${icc.TERMINAL_TYPE}</TerminalType> <TerminalCapabilities>${icc.TERMINAL_CAPABILITIES}</TerminalCapabilities> <TransactionDate>${icc.TRANSACTION_DATE}</TransactionDate> <TransactionType>${icc.TRANSACTION_TYPE}</TransactionType> <UnpredictableNumber>${icc.UNPREDICTABLE_NUMBER}</UnpredictableNumber> ${dedicatedFileTag}</emvData></cardData><stan>${transaction.stan}</stan><fromAccount>${transaction.accountType.name}</fromAccount> <toAccount>Current</toAccount> <minorAmount>${transaction.amount.toString()}</minorAmount> <messageReasonCode>4000</messageReasonCode> <rate></rate><settlementFee></settlementFee> <settlementCurrencyCode>${settlementCurrencyCode}</settlementCurrencyCode> <amountSettlement></amountSettlement> <attemptCount>1</attemptCount> <creationDate>190509</creationDate> <originalTransmissionDateTime>2019-05-09T09:10:49</originalTransmissionDateTime> <reversalType>Purchase</reversalType> <tmsConfiguredTerminalLocation></tmsConfiguredTerminalLocation>${pinData}<keyLabel>${keyLabel}</keyLabel> <originalAuthId>${originalAuthId}</originalAuthId> <notDisposable>false</notDisposable> <originalStan>${originalStan}</originalStan></reversalRequestWithoutOriginalDate>"""
        }


fun toReservation(device:POSDevice,terminalInfo: TerminalInfo, transaction: TransactionInfo):String{


    val iswConfig = IswPos.getInstance().config
    var keyLabel=if (iswConfig.environment == Environment.Test) "000006" else "000002"


    val hasPin = transaction.cardPIN.isNotEmpty()
    var pinData=""
    if(hasPin) pinData= """<pinData><ksnd>605</ksnd><pinBlock></pinBlock><pinType>Dukpt</pinType> </pinData>"""
    var dedicatedFileTag=""


    var  track2 = let {
        val neededLength = transaction.cardTrack2.length - 2
        val isVisa = transaction.cardTrack2.startsWith('4')
        val hasCharacter = transaction.cardTrack2.last().isLetter()

        // remove character suffix for visa
        if (isVisa && hasCharacter) transaction.cardTrack2.substring(0..neededLength)
        else transaction.cardTrack2
    }
    val amount = String.format(Locale.getDefault(), "%012d", transaction.amount)
    val now = Date()
    val date = DateUtils.dateFormatter.format(now)
    var icc= getIcc(terminalInfo,amount,date,transaction)
    if(false)
        dedicatedFileTag=  """"<DedicatedFileName>${icc.DEDICATED_FILE_NAME}</DedicatedFileName>"""

    return  """
        <reservationRequest><terminalInformation><batteryInformation>-1</batteryInformation> <currencyCode>${terminalInfo.currencyCode}</currencyCode><languageInfo>EN</languageInfo><merchantId>${terminalInfo.merchantId}</merchantId><merhcantLocation>${terminalInfo.merchantNameAndLocation}</merhcantLocation> <posConditionCode>00</posConditionCode> <posDataCode>${if (hasPin) "510101511344101" else "511101511344101"}</posDataCode> <posEntryMode>051</posEntryMode> <posGeoCode>00234000000000566</posGeoCode> <printerStatus>1</printerStatus><terminalId>${terminalInfo.terminalId}</terminalId> <terminalType>${device.name}</terminalType> <transmissionDate>${DateUtils.universalDateFormat.format(Date())}</transmissionDate> <uniqueId>${icc.INTERFACE_DEVICE_SERIAL_NUMBER}</uniqueId></terminalInformation><cardData><cardSequenceNumber>01</cardSequenceNumber><track2><pan>${transaction.cardPAN}</pan> <expiryMonth>${transaction.cardExpiry.takeLast(2)}</expiryMonth> <expiryYear>${transaction.cardExpiry.take(2)}</expiryYear> <track2>${track2}</track2></track2><wasFallback></wasFallback><emvData><AmountAuthorized>000000010000</AmountAuthorized> <AmountOther>${icc.ANOTHER_AMOUNT}</AmountOther><ApplicationInterchangeProfile>${icc.APPLICATION_INTERCHANGE_PROFILE}</ApplicationInterchangeProfile> <atc>${icc.APPLICATION_TRANSACTION_COUNTER}</atc><Cryptogram>${icc.AUTHORIZATION_REQUEST}</Cryptogram> <CryptogramInformationData>${icc.CRYPTOGRAM_INFO_DATA}</CryptogramInformationData> <CvmResults>${icc.CARD_HOLDER_VERIFICATION_RESULT}</CvmResults> <iad>${icc.ISSUER_APP_DATA}</iad> <TransactionCurrencyCode>${icc.TRANSACTION_CURRENCY_CODE}</TransactionCurrencyCode><TerminalVerificationResult>${icc.TERMINAL_VERIFICATION_RESULT}</TerminalVerificationResult> <TerminalCountryCode>${icc.TERMINAL_COUNTRY_CODE}</TerminalCountryCode><TerminalType>${icc.TERMINAL_TYPE}</TerminalType> <TerminalCapabilities>${icc.TERMINAL_CAPABILITIES}</TerminalCapabilities> <TransactionDate>${icc.TRANSACTION_DATE}</TransactionDate> <TransactionType>${icc.TRANSACTION_TYPE}</TransactionType> <UnpredictableNumber>${icc.UNPREDICTABLE_NUMBER}</UnpredictableNumber> ${dedicatedFileTag}</emvData></cardData><fromAccount>${transaction.accountType.name}</fromAccount> <stan>${transaction.stan}</stan><minorAmount>${transaction.amount.toString()}</minorAmount><tmsConfiguredTerminalLocation></tmsConfiguredTerminalLocation>${pinData}<keyLabel>${keyLabel}</keyLabel></reservationRequest>
    """.trimIndent()
}



        fun toCompletion(device:POSDevice,terminalInfo: TerminalInfo, transaction: TransactionInfo):String{

            val hasPin = transaction.cardPIN.isNotEmpty()
            var pinData=""
            if(hasPin) pinData= """<pinData><ksnd>605</ksnd><pinBlock></pinBlock><pinType>Dukpt</pinType> </pinData>"""

            val amount = String.format(Locale.getDefault(), "%012d", transaction.amount)
            val now = Date()
            val date = DateUtils.dateFormatter.format(now)
            var icc= getIcc(terminalInfo,amount,date,transaction)

            var originalStan=transaction.originalTransactionInfoData?.originalStan
            var originalAuthId=transaction.originalTransactionInfoData?.originalAuthorizationId
            var settlementCurrencyCode=icc.TRANSACTION_CURRENCY_CODE
            val iswConfig = IswPos.getInstance().config
            var keyLabel=if (iswConfig.environment == Environment.Test) "000006" else "000002"



            var dedicatedFileTag=""
            if(false)
                dedicatedFileTag=  """"<DedicatedFileName>${icc.DEDICATED_FILE_NAME}</DedicatedFileName>"""


            var  track2 = let {
                val neededLength = transaction.cardTrack2.length - 2
                val isVisa = transaction.cardTrack2.startsWith('4')
                val hasCharacter = transaction.cardTrack2.last().isLetter()

                // remove character suffix for visa
                if (isVisa && hasCharacter) transaction.cardTrack2.substring(0..neededLength)
                else transaction.cardTrack2
            }
            return  """
                <completionRequest><terminalInformation><batteryInformation>-1</batteryInformation> <currencyCode>${terminalInfo.currencyCode}</currencyCode><languageInfo>EN</languageInfo><merchantId>${terminalInfo.merchantId}</merchantId><merhcantLocation>${terminalInfo.merchantNameAndLocation}</merhcantLocation> <posConditionCode>00</posConditionCode> <posDataCode>${if (hasPin) "510101511344101" else "511101511344101"}</posDataCode> <posEntryMode>051</posEntryMode> <posGeoCode>00234000000000566</posGeoCode> <printerStatus>1</printerStatus><terminalId>${terminalInfo.terminalId}</terminalId> <terminalType>${device.name}</terminalType> <transmissionDate>${DateUtils.universalDateFormat.format(Date())}</transmissionDate> <uniqueId>${icc.INTERFACE_DEVICE_SERIAL_NUMBER}</uniqueId></terminalInformation><cardData><cardSequenceNumber>01</cardSequenceNumber><track2><pan>${transaction.cardPAN}</pan> <expiryMonth>${transaction.cardExpiry.takeLast(2)}</expiryMonth> <expiryYear>${transaction.cardExpiry.take(2)}</expiryYear> <track2>${track2}</track2></track2><wasFallback></wasFallback><emvData><AmountAuthorized>000000010000</AmountAuthorized> <AmountOther>${icc.ANOTHER_AMOUNT}</AmountOther><ApplicationInterchangeProfile>${icc.APPLICATION_INTERCHANGE_PROFILE}</ApplicationInterchangeProfile> <atc>${icc.APPLICATION_TRANSACTION_COUNTER}</atc><Cryptogram>${icc.AUTHORIZATION_REQUEST}</Cryptogram> <CryptogramInformationData>${icc.CRYPTOGRAM_INFO_DATA}</CryptogramInformationData> <CvmResults>${icc.CARD_HOLDER_VERIFICATION_RESULT}</CvmResults> <iad>${icc.ISSUER_APP_DATA}</iad> <TransactionCurrencyCode>${icc.TRANSACTION_CURRENCY_CODE}</TransactionCurrencyCode><TerminalVerificationResult>${icc.TERMINAL_VERIFICATION_RESULT}</TerminalVerificationResult> <TerminalCountryCode>${icc.TERMINAL_COUNTRY_CODE}</TerminalCountryCode><TerminalType>${icc.TERMINAL_TYPE}</TerminalType> <TerminalCapabilities>${icc.TERMINAL_CAPABILITIES}</TerminalCapabilities> <TransactionDate>${icc.TRANSACTION_DATE}</TransactionDate> <TransactionType>${icc.TRANSACTION_TYPE}</TransactionType> <UnpredictableNumber>${icc.UNPREDICTABLE_NUMBER}</UnpredictableNumber>${dedicatedFileTag}</emvData></cardData><stan>${transaction.stan}</stan><minorAmount>${transaction.amount.toString()}</minorAmount><surcharge>0</surcharge><rate>0</rate><amountSettlement>0</amountSettlement><settlementFee>0</settlementFee><settlementCurrencyCode>${settlementCurrencyCode}}</settlementCurrencyCode><tmsConfiguredTerminalLocation></tmsConfiguredTerminalLocation>${pinData}<keyLabel>${keyLabel}</keyLabel><originalAuthRef>${originalAuthId}</originalAuthRef><originalDateTime>${transaction.originalTransactionInfoData?.originalTransmissionDateAndTime}</originalDateTime><notDisposable>false</notDisposable><originalStan>${originalStan}</originalStan></completionRequest>
            """.trimIndent()
        }



        fun toRefund(device:POSDevice,terminalInfo: TerminalInfo, transaction: TransactionInfo):String{


            val hasPin = transaction.cardPIN.isNotEmpty()
            var pinData=""
            if(hasPin) pinData= """<pinData><ksnd>605</ksnd><pinBlock></pinBlock><pinType>Dukpt</pinType> </pinData>"""

            val amount = String.format(Locale.getDefault(), "%012d", transaction.amount)
            val now = Date()
            val date = DateUtils.dateFormatter.format(now)
            var icc= getIcc(terminalInfo,amount,date,transaction)
            val iswConfig = IswPos.getInstance().config
            var keyLabel=if (iswConfig.environment == Environment.Test) "000006" else "000002"
            var  track2 = let {
                val neededLength = transaction.cardTrack2.length - 2
                val isVisa = transaction.cardTrack2.startsWith('4')
                val hasCharacter = transaction.cardTrack2.last().isLetter()

                // remove character suffix for visa
                if (isVisa && hasCharacter) transaction.cardTrack2.substring(0..neededLength)
                else transaction.cardTrack2
            }

            var dedicatedFileTag=""
if(false)
            dedicatedFileTag=  """"<DedicatedFileName>${icc.DEDICATED_FILE_NAME}</DedicatedFileName>"""

            return  """<refundRequest>
        <terminalInformation><batteryInformation>-1</batteryInformation> <currencyCode>${terminalInfo.currencyCode}</currencyCode><languageInfo>EN</languageInfo><merchantId>${terminalInfo.merchantId}</merchantId><merhcantLocation>${terminalInfo.merchantNameAndLocation}</merhcantLocation> <posConditionCode>00</posConditionCode> <posDataCode>${if (hasPin) "510101511344101" else "511101511344101"}</posDataCode> <posEntryMode>051</posEntryMode> <posGeoCode>00234000000000566</posGeoCode> <printerStatus>1</printerStatus><terminalId>${terminalInfo.terminalId}</terminalId> <terminalType>${device.name}</terminalType> <transmissionDate>${DateUtils.universalDateFormat.format(Date())}</transmissionDate> <uniqueId>${icc.INTERFACE_DEVICE_SERIAL_NUMBER}</uniqueId></terminalInformation> 
        <cardData><cardSequenceNumber>01</cardSequenceNumber> <track2><pan>${transaction.cardPAN}</pan> <expiryMonth>${transaction.cardExpiry.takeLast(2)}</expiryMonth> <expiryYear>${transaction.cardExpiry.take(2)}</expiryYear> <track2>${track2}</track2></track2>
                <emvData><AmountAuthorized>000000000000</AmountAuthorized> <AmountOther>${icc.ANOTHER_AMOUNT}</AmountOther> <ApplicationInterchangeProfile>${icc.APPLICATION_INTERCHANGE_PROFILE}</ApplicationInterchangeProfile> <atc>${icc.APPLICATION_TRANSACTION_COUNTER}</atc><Cryptogram>${icc.AUTHORIZATION_REQUEST}</Cryptogram> <CryptogramInformationData>${icc.CRYPTOGRAM_INFO_DATA}</CryptogramInformationData> <CvmResults>${icc.CARD_HOLDER_VERIFICATION_RESULT}</CvmResults><iad>${icc.ISSUER_APP_DATA}</iad> <TransactionCurrencyCode>${icc.TRANSACTION_CURRENCY_CODE}</TransactionCurrencyCode> <TerminalVerificationResult>${icc.TERMINAL_VERIFICATION_RESULT}</TerminalVerificationResult> <TerminalCountryCode>${icc.TERMINAL_COUNTRY_CODE}</TerminalCountryCode> <TerminalType>${icc.TERMINAL_TYPE}</TerminalType> <TerminalCapabilities>${icc.TERMINAL_CAPABILITIES}</TerminalCapabilities> <TransactionDate>${icc.TRANSACTION_DATE}</TransactionDate> <TransactionType>${icc.TRANSACTION_TYPE}</TransactionType> <UnpredictableNumber>${icc.UNPREDICTABLE_NUMBER}</UnpredictableNumber>${dedicatedFileTag}</emvData></cardData>
        <fromAccount>${transaction.accountType.name}</fromAccount>
        <stan>${transaction.stan}</stan>
        <originalTransmissionDateTime>${transaction.originalTransactionInfoData?.originalTransmissionDateAndTime}</originalTransmissionDateTime>
        <minorAmount>${ transaction.amount}</minorAmount>
        <receivingInstitutionId>627784</receivingInstitutionId>
        <surcharge>2500</surcharge>
       ${pinData}
        <keyLabel>${keyLabel}</keyLabel>
		<originalAuthId>${transaction.originalTransactionInfoData?.originalAuthorizationId}</originalAuthId>
      </refundRequest>"""
        }

        private fun getIcc(terminalInfo: TerminalInfo, amount: String, date: String,transaction: TransactionInfo): IccData {
            transaction.iccFull.TRANSACTION_AMOUNT=amount

            return  transaction.iccFull
        }
//
//        private fun getIcc(terminalInfo: TerminalInfo, amount: String, date: String,transaction: TransactionInfo): IccData {
//            val authorizedAmountTLV = String.format("9F02%02d%s", amount.length / 2, amount)
//            val transactionDateTLV = String.format("9A%02d%s", date.length / 2, date)
//            val iccData =transaction.icc;
//
////                    "9F260831BDCBC7CFF6253B9F2701809F10120110A50003020000000000000000000000FF9F3704F435D8A29F3602052795050880000000" +
////                    "${transactionDateTLV}9C0100${authorizedAmountTLV}5F2A020566820238009F1A0205669F34034103029F3303E0F8C89F3501229F0306000000000000"
//
//            // remove leadin zero if exits
//            val currencyCode = if (terminalInfo.currencyCode.length > 3) terminalInfo.currencyCode.substring(1) else terminalInfo.currencyCode
//            val countryCode = if (terminalInfo.countryCode.length > 3) terminalInfo.countryCode.substring(1) else terminalInfo.countryCode
//
//
//
//
//
//            return IccData().apply {
//                TRANSACTION_AMOUNT = amount
//                ANOTHER_AMOUNT = "000000000000"
//                APPLICATION_INTERCHANGE_PROFILE = "3800"
//                APPLICATION_TRANSACTION_COUNTER = "0527" //check
//                CRYPTOGRAM_INFO_DATA = "80"
//                CARD_HOLDER_VERIFICATION_RESULT = "410302"
//                ISSUER_APP_DATA = "0110A50003020000000000000000000000FF"
//                TRANSACTION_CURRENCY_CODE = currencyCode
//                TERMINAL_VERIFICATION_RESULT = "0880000000"
//                TERMINAL_COUNTRY_CODE = countryCode
//                TERMINAL_TYPE = "22"
//                TERMINAL_CAPABILITIES = terminalInfo.capabilities ?: "E0F8C8"
//                TRANSACTION_DATE = date
//                TRANSACTION_TYPE = "00"
//                UNPREDICTABLE_NUMBER = "F435D8A2"
//                DEDICATED_FILE_NAME = ""
//                AUTHORIZATION_REQUEST = "31BDCBC7CFF6253B"
//
//                iccAsString = iccData
//            }
//
//        }








    }
}










@Root(name = "TerminalInformation", strict = false)
data class TerminalInformation  @JvmOverloads constructor(@field:Element(name = "batteryInformation", required = false)
                          var batteryInformation: String = "",
                          @field:Element(name = "currencyCode", required = false)
var currencyCode: String = "",
@field:Element(name = "languageInfo", required = false)
var languageInfo: String = "",
@field:Element(name = "merchantId", required = false)
var merchantId: String = "",
// TODO confirm if this is no a typo <merhcantLocation></merhcantLocation>
@field:Element(name = "merhcantLocation", required = false)
var merchantLocation: String = "",
@field:Element(name = "posConditionCode", required = false)
var posConditionCode: String = "",
@field:Element(name = "posDataCode", required = false)
var posDataCode: String = "",
@field:Element(name = "posEntryMode", required = false)
var posEntryMode: String = "",
@field:Element(name = "posGeoCode", required = false)
var posGeoCode: String = "",
@field:Element(name = "printerStatus", required = false)
var printerStatus: String = "",
@field:Element(name = "terminalId", required = false)
var terminalId: String = "",
@field:Element(name = "terminalType", required = false)
var terminalType: String = "",
@field:Element(name = "transmissionDate", required = false)
var transmissionDate: String = "",
@field:Element(name = "uniqueId", required = false)
var uniqueId: String = "",
@field:Element(name = "cardAcceptorId", required = false)
var cardAcceptorId: String = "",
@field:Element(name = "cellStationId", required = false)
var cellStationId: String = ""
)
//{


//    companion object {
//        fun create(deviceName: String, terminalInfo: TerminalInfo, transactionInfo: TransactionInfo): TerminalInformation {
//            val battery = "-1"
//            val date = DateUtils.universalDateFormat.format(Date())
//            val hasPin = transactionInfo.cardPIN.isNotEmpty()
//
//            return TerminalInformation().apply {
//                batteryInformation = battery
//                currencyCode = terminalInfo.currencyCode
//                languageInfo = "EN"
//                merchantId = terminalInfo.merchantId
//                merchantLocation = terminalInfo.merchantNameAndLocation
//                posConditionCode = "00"
//                posEntryMode = "051"
//                terminalId = terminalInfo.terminalId
//                transmissionDate = date
//                uniqueId = "00000${transactionInfo.stan}"
//                terminalType = deviceName.toUpperCase()
//                posDataCode = if (hasPin) "510101511344101" else "511101511344101"
//                cardAcceptorId = terminalInfo.merchantId
//                cellStationId = "00"
//                posGeoCode = "00234000000000566"
//                printerStatus="1"
//            }
//        }
//    }
//}

@Root(name = "CardData", strict = false)
data class CardData @JvmOverloads constructor(
    @field:Element(name = "cardSequenceNumber", required = false)
    var cardSequenceNumber: String = "",
    @field:Element(name = "emvData", required = false)
    var emvData: IccData? = null,
    @field:Element(name = "mifareData", required = false)
    var mifareData: MiFareData? = null,
    @field:Element(name = "track2", required = false)
    var track2: Track2? = null,
    @field:Element(name = "wasFallback", required = false)
    var wasFallback: Boolean = false
)
//    companion object {
//        fun create(transactionInfo: TransactionInfo) = CardData().apply {
//            cardSequenceNumber = transactionInfo.csn
//            //ToDO: reenable
////            emvData = transactionInfo.iccData
//            track2 = Track2().apply {
//                pan = transactionInfo.cardPAN
//                expiryMonth = transactionInfo.cardExpiry.takeLast(2)
//                expiryYear = transactionInfo.cardExpiry.take(2)
//                track2 = let {
//                    val neededLength = transactionInfo.cardTrack2.length - 2
//                    val isVisa = transactionInfo.cardTrack2.startsWith('4')
//                    val hasCharacter = transactionInfo.cardTrack2.last().isLetter()
//
//                    // remove character suffix for visa
//                    if (isVisa && hasCharacter) transactionInfo.cardTrack2.substring(0..neededLength)
//                    else transactionInfo.cardTrack2
//                }
//            }
//
//        }
//    }
//}

@Root(name = "MiFareData", strict = false)
data class MiFareData @JvmOverloads constructor(  @field:Element(name = "cardSerialNo", required = false)
                                                  var cardSerialNo: String = "")


@Root(name = "Track2", strict = false)
data class Track2  @JvmOverloads constructor(
        @field:Element(name = "pan", required = false)
        var pan: String = "",
        @field:Element(name = "expiryMonth", required = false)
var expiryMonth: String = "",
@field:Element(name = "expiryYear", required = false)
var expiryYear: String = "",
@field:Element(name = "track2", required = false)
var track2: String = ""
)

@Root(name = "Track2", strict = false)
data class PinData @JvmOverloads constructor(

        @field:Element(name = "ksnd", required = false)
        var ksnd: String = "605",
        @field:Element(name = "pinType", required = false)
        var pinType: String = "Dukpt",
        @field:Element(name = "ksn", required = false)
        var ksn: String = "",
        @field:Element(name = "pinBlock", required = false)
        var pinBlock: String = ""
)
{





//    companion object {
//        fun create(txnInfo: TransactionInfo) = PinData().apply {
//            //TODO, Get pinKs
////            ksn = txnInfo.pinKsn
//            pinBlock = txnInfo.cardPIN
//        }
//
//    }
}
