package com.interswitchng.smartpos.shared.services.kimono.models

//import com.interswitchng.smartpos.shared.services.DateUtils
import com.interswitchng.smartpos.IswPos
import com.interswitchng.smartpos.shared.Constants
import com.interswitchng.smartpos.shared.interfaces.device.POSDevice
import com.interswitchng.smartpos.shared.models.core.Environment
import com.interswitchng.smartpos.shared.models.core.TerminalInfo
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.request.IccData
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.request.TransactionInfo
import com.interswitchng.smartpos.shared.services.iso8583.utils.DateUtils
import com.interswitchng.smartpos.shared.utilities.InputValidator
import com.interswitchng.smartpos.shared.utilities.Logger
import org.simpleframework.xml.Element
import org.simpleframework.xml.Root
import java.util.*


internal class PurchaseRequest
{
    companion object {

        fun toCashOutInquiry(device: POSDevice, terminalInfo: TerminalInfo, transaction: TransactionInfo): String {

            val hasPin = transaction.cardPIN.isNotEmpty()
            var pinData = ""
            var customerId = terminalInfo.agentId
            var phoneNumber = terminalInfo.agentId
            var bankCbnCode = terminalInfo.terminalId.drop(1).take(3)
            var customerEmail = terminalInfo.agentEmail

            var transactionAmount = transaction.amount
            var transactionAmountInNaira = transactionAmount / 100
            var paymentCode = when (transactionAmountInNaira) {
                in 1..5000 -> Constants.PAYMENT_CODE_1
                in 5001..10000 -> Constants.PAYMENT_CODE_2
                in 10001..20000 -> Constants.PAYMENT_CODE_3
                else -> Constants.PAYMENT_CODE_4
            }

            var rrfNumber = transaction.stan.padStart(12, '0')
            val amount = String.format(Locale.getDefault(), "%012d", transactionAmount)
            Logger.with("purchaserequest").logErr(amount)
            val now = Date()
            val date = DateUtils.dateFormatter.format(now)
            var icc = getIcc(terminalInfo, amount, date, transaction)

            val iswConfig = IswPos.getInstance().config
            var keyLabel = if (iswConfig.environment == Environment.Test) "000006" else "000002"

            var dedicatedFileTag = "<DedicatedFileName>${icc.DEDICATED_FILE_NAME}</DedicatedFileName>"

            var track2 = let {
                val neededLength = transaction.cardTrack2.length - 2
                val isVisa = transaction.cardTrack2.startsWith('4')
                val hasCharacter = transaction.cardTrack2.last().isLetter()

                // remove character suffix for visa
                if (isVisa && hasCharacter) transaction.cardTrack2.substring(0..neededLength)
                else transaction.cardTrack2
            }

            if (hasPin)

                pinData = """<pinData><ksnd>605</ksnd><pinType>Dukpt</pinType><ksn>${transaction.pinKsn}</ksn><pinBlock>${transaction.cardPIN}</pinBlock></pinData>"""


            val requestBody = """<billPaymentRequest>
                <retrievalReferenceNumber>$rrfNumber</retrievalReferenceNumber>
                <bankCbnCode>${bankCbnCode}</bankCbnCode>
                <cardPan>${transaction.cardPAN}</cardPan>
                <customerEmail>$customerEmail</customerEmail>
                <customerId>$customerId</customerId>
                <customerMobile>$phoneNumber</customerMobile>
                <paymentCode>$paymentCode</paymentCode>
                <terminalId>${terminalInfo.terminalId}</terminalId>
                <customerName></customerName>
                <requestType>Inquiry</requestType>
                <terminalInformation><batteryInformation>-1</batteryInformation> 
                <currencyCode>${terminalInfo.currencyCode}</currencyCode>
                <languageInfo>EN</languageInfo>
                <merchantId>${terminalInfo.merchantId}</merchantId>
                <merhcantLocation>${terminalInfo.merchantNameAndLocation}</merhcantLocation> 
                <posConditionCode>00</posConditionCode> <posDataCode>511101511344101</posDataCode> 
                <posEntryMode>051</posEntryMode> <posGeoCode>00234000000000566</posGeoCode> 
                <printerStatus>1</printerStatus><terminalId>${terminalInfo.terminalId}</terminalId> <terminalType>${icc.TERMINAL_TYPE}</terminalType> <transmissionDate>${DateUtils.universalDateFormat.format(Date())}</transmissionDate> <uniqueId>${icc.INTERFACE_DEVICE_SERIAL_NUMBER}</uniqueId></terminalInformation><cardData><cardSequenceNumber>${transaction.csn}</cardSequenceNumber> <track2><pan>${transaction.cardPAN}</pan> <expiryMonth>${transaction.cardExpiry.takeLast(2)}</expiryMonth> <expiryYear>${transaction.cardExpiry.take(2)}</expiryYear><track2>${track2}</track2></track2><emvData><AmountAuthorized>${icc.TRANSACTION_AMOUNT}</AmountAuthorized> <AmountOther>${icc.ANOTHER_AMOUNT}</AmountOther> <ApplicationInterchangeProfile>${icc.APPLICATION_INTERCHANGE_PROFILE}</ApplicationInterchangeProfile> <atc>${icc.APPLICATION_TRANSACTION_COUNTER}</atc><Cryptogram>${icc.AUTHORIZATION_REQUEST}</Cryptogram> <CryptogramInformationData>${icc.CRYPTOGRAM_INFO_DATA}</CryptogramInformationData> <CvmResults>${icc.CARD_HOLDER_VERIFICATION_RESULT}</CvmResults><iad>${icc.ISSUER_APP_DATA}</iad> <TransactionCurrencyCode>${icc.TRANSACTION_CURRENCY_CODE}</TransactionCurrencyCode> <TerminalVerificationResult>${icc.TERMINAL_VERIFICATION_RESULT}</TerminalVerificationResult> <TerminalCountryCode>${icc.TERMINAL_COUNTRY_CODE}</TerminalCountryCode> <TerminalType>${icc.TERMINAL_TYPE}</TerminalType> <TerminalCapabilities>${icc.TERMINAL_CAPABILITIES}</TerminalCapabilities> <TransactionDate>${icc.TRANSACTION_DATE}</TransactionDate> <TransactionType>${icc.TRANSACTION_TYPE}</TransactionType> <UnpredictableNumber>${icc.UNPREDICTABLE_NUMBER}</UnpredictableNumber> ${dedicatedFileTag}</emvData></cardData><fromAccount>${transaction.accountType.name}</fromAccount> <stan>${transaction.stan}</stan> <minorAmount>${transactionAmount}</minorAmount> ${pinData} <keyLabel>${keyLabel}</keyLabel></billPaymentRequest>
        """
            Logger.with("Purchase Request body").logErr(requestBody)

            return requestBody

        }

        fun toCashOutPayString(response: BillPaymentResponse, terminalInfo: TerminalInfo, transaction: TransactionInfo): String {
            val hasPin = transaction.cardPIN.isNotEmpty()
            var pinData = ""
            var customerId = terminalInfo.agentId
            var phoneNumber = terminalInfo.agentId
            var bankCbnCode = terminalInfo.terminalId.drop(1).take(3)
            var customerEmail = terminalInfo.agentEmail

            var transactionAmount = transaction.amount
            var transactionAmountInNaira = transactionAmount / 100
            var paymentCode = when (transactionAmountInNaira) {
                in 1..5000 -> Constants.PAYMENT_CODE_1
                in 5001..10000 -> Constants.PAYMENT_CODE_2
                in 10001..20000 -> Constants.PAYMENT_CODE_3
                else -> Constants.PAYMENT_CODE_4
            }

            var rrfNumber = transaction.stan.padStart(12, '0')
            val amount = String.format(Locale.getDefault(), "%012d", transactionAmount)
            Logger.with("purchaserequest").logErr(amount)
            val now = Date()
            val date = DateUtils.dateFormatter.format(now)
            var icc = getIcc(terminalInfo, amount, date, transaction)

            val iswConfig = IswPos.getInstance().config
            var keyLabel = if (iswConfig.environment == Environment.Test) "000006" else "000002"

            var dedicatedFileTag = "<DedicatedFileName>${icc.DEDICATED_FILE_NAME}</DedicatedFileName>"

            var track2 = let {
                val neededLength = transaction.cardTrack2.length - 2
                val isVisa = transaction.cardTrack2.startsWith('4')
                val hasCharacter = transaction.cardTrack2.last().isLetter()

                // remove character suffix for visa
                if (isVisa && hasCharacter) transaction.cardTrack2.substring(0..neededLength)
                else transaction.cardTrack2
            }

            if (hasPin)

                pinData = """<pinData><ksnd>605</ksnd><pinType>Dukpt</pinType><ksn>${transaction.pinKsn}</ksn><pinBlock>${transaction.cardPIN}</pinBlock></pinData>"""


            val requestBody = """<billPaymentRequest>
                <retrievalReferenceNumber>$rrfNumber</retrievalReferenceNumber>
                <bankCbnCode>${bankCbnCode}</bankCbnCode>
                <cardPan>${transaction.cardPAN}</cardPan>
                <customerEmail>$customerEmail</customerEmail>
                <customerId>$customerId</customerId>
                <customerMobile>$phoneNumber</customerMobile>
                <paymentCode>$paymentCode</paymentCode>
                <terminalId>${terminalInfo.terminalId}</terminalId>
                <transactionRef>${response.transactionRef}</transactionRef>
                <customerName></customerName>
                <requestType>Payment</requestType>
                <terminalInformation><batteryInformation>-1</batteryInformation> 
                <currencyCode>${terminalInfo.currencyCode}</currencyCode>
                <languageInfo>EN</languageInfo>
                <merchantId>${terminalInfo.merchantId}</merchantId>
                <merhcantLocation>${terminalInfo.merchantNameAndLocation}</merhcantLocation> 
                <posConditionCode>00</posConditionCode> <posDataCode>511101511344101</posDataCode> 
                <posEntryMode>051</posEntryMode> <posGeoCode>00234000000000566</posGeoCode> 
                <printerStatus>1</printerStatus><terminalId>${terminalInfo.terminalId}</terminalId> <terminalType>${icc.TERMINAL_TYPE}</terminalType> <transmissionDate>${DateUtils.universalDateFormat.format(Date())}</transmissionDate> <uniqueId>${icc.INTERFACE_DEVICE_SERIAL_NUMBER}</uniqueId></terminalInformation><cardData><cardSequenceNumber>${transaction.csn}</cardSequenceNumber> <track2><pan>${transaction.cardPAN}</pan> <expiryMonth>${transaction.cardExpiry.takeLast(2)}</expiryMonth> <expiryYear>${transaction.cardExpiry.take(2)}</expiryYear><track2>${track2}</track2></track2><emvData><AmountAuthorized>${icc.TRANSACTION_AMOUNT}</AmountAuthorized> <AmountOther>${icc.ANOTHER_AMOUNT}</AmountOther> <ApplicationInterchangeProfile>${icc.APPLICATION_INTERCHANGE_PROFILE}</ApplicationInterchangeProfile> <atc>${icc.APPLICATION_TRANSACTION_COUNTER}</atc><Cryptogram>${icc.AUTHORIZATION_REQUEST}</Cryptogram> <CryptogramInformationData>${icc.CRYPTOGRAM_INFO_DATA}</CryptogramInformationData> <CvmResults>${icc.CARD_HOLDER_VERIFICATION_RESULT}</CvmResults><iad>${icc.ISSUER_APP_DATA}</iad> <TransactionCurrencyCode>${icc.TRANSACTION_CURRENCY_CODE}</TransactionCurrencyCode> <TerminalVerificationResult>${icc.TERMINAL_VERIFICATION_RESULT}</TerminalVerificationResult> <TerminalCountryCode>${icc.TERMINAL_COUNTRY_CODE}</TerminalCountryCode> <TerminalType>${icc.TERMINAL_TYPE}</TerminalType> <TerminalCapabilities>${icc.TERMINAL_CAPABILITIES}</TerminalCapabilities> <TransactionDate>${icc.TRANSACTION_DATE}</TransactionDate> <TransactionType>${icc.TRANSACTION_TYPE}</TransactionType> <UnpredictableNumber>${icc.UNPREDICTABLE_NUMBER}</UnpredictableNumber> ${dedicatedFileTag}</emvData></cardData><fromAccount>${transaction.accountType.name}</fromAccount> <stan>${transaction.stan}</stan> <uuid>${response.uuid}</uuid>  <minorAmount>${transactionAmount}</minorAmount> ${pinData} <keyLabel>${keyLabel}</keyLabel></billPaymentRequest>
        """
            Logger.with("Purchase Request body").logErr(requestBody)

            return requestBody

        }

        fun toCNPPurchaseString(device:POSDevice, terminalInfo: TerminalInfo, transaction: TransactionInfo): String {

            var pinData=""
            var transactionAmount = transaction.amount

            val amount = String.format(Locale.getDefault(), "%012d", transactionAmount)
            Logger.with("purchaserequest").logErr(amount)
            val now = Date()
            val date = DateUtils.dateFormatter.format(now)
            var icc= getIcc(terminalInfo,amount,date,transaction)

            val iswConfig = IswPos.getInstance().config
            var keyLabel=if (iswConfig.environment == Environment.Test) "000006" else "000002"

            var dedicatedFileTag="<DedicatedFileName>${icc.DEDICATED_FILE_NAME}</DedicatedFileName>"

            val requestBody = """<?xml version="1.0" encoding="UTF-8" ?><purchaseRequest>
                <terminalInformation><batteryInformation>-1</batteryInformation> <currencyCode>${terminalInfo.currencyCode}</currencyCode><languageInfo>EN</languageInfo><merchantId>${terminalInfo.merchantId}</merchantId><merhcantLocation>${terminalInfo.merchantNameAndLocation}</merhcantLocation> <posConditionCode>00</posConditionCode> <posDataCode>511101511344101</posDataCode> <posEntryMode>051</posEntryMode> <posGeoCode>00234000000000566</posGeoCode> <printerStatus>1</printerStatus><terminalId>${terminalInfo.terminalId}</terminalId> <terminalType>${device.name}</terminalType> <transmissionDate>${DateUtils.universalDateFormat.format(Date())}</transmissionDate> <uniqueId>${icc.INTERFACE_DEVICE_SERIAL_NUMBER}</uniqueId></terminalInformation><cardData><cardSequenceNumber>${transaction.csn}</cardSequenceNumber> <track2><pan>${transaction.cardPAN}</pan> <expiryMonth>${transaction.cardExpiry.takeLast(2)}</expiryMonth> <expiryYear>${transaction.cardExpiry.take(2)}</expiryYear></track2><emvData><AmountAuthorized>${icc.TRANSACTION_AMOUNT}</AmountAuthorized> <AmountOther>${icc.ANOTHER_AMOUNT}</AmountOther> <ApplicationInterchangeProfile>${icc.APPLICATION_INTERCHANGE_PROFILE}</ApplicationInterchangeProfile> <atc>${icc.APPLICATION_TRANSACTION_COUNTER}</atc><Cryptogram>${icc.AUTHORIZATION_REQUEST}</Cryptogram> <CryptogramInformationData>${icc.CRYPTOGRAM_INFO_DATA}</CryptogramInformationData> <CvmResults>${icc.CARD_HOLDER_VERIFICATION_RESULT}</CvmResults><iad>${icc.ISSUER_APP_DATA}</iad> <TransactionCurrencyCode>${icc.TRANSACTION_CURRENCY_CODE}</TransactionCurrencyCode> <TerminalVerificationResult>${icc.TERMINAL_VERIFICATION_RESULT}</TerminalVerificationResult> <TerminalCountryCode>${icc.TERMINAL_COUNTRY_CODE}</TerminalCountryCode> <TerminalType>${icc.TERMINAL_TYPE}</TerminalType> <TerminalCapabilities>${icc.TERMINAL_CAPABILITIES}</TerminalCapabilities> <TransactionDate>${icc.TRANSACTION_DATE}</TransactionDate> <TransactionType>${icc.TRANSACTION_TYPE}</TransactionType> <UnpredictableNumber>${icc.UNPREDICTABLE_NUMBER}</UnpredictableNumber> ${dedicatedFileTag}</emvData></cardData><fromAccount>${transaction.accountType.name}</fromAccount> <stan>${transaction.stan}</stan> <minorAmount>${transactionAmount}</minorAmount> ${pinData} <keyLabel>${keyLabel}</keyLabel></purchaseRequest>
        """
            Logger.with("Purchase Request body").logErr(requestBody)

            return requestBody

        }

        fun toCardPurchaseString(device:POSDevice, terminalInfo: TerminalInfo, transaction: TransactionInfo): String {


            val hasPin = transaction.cardPIN.isNotEmpty()
            var transactionAmount = transaction.amount


            var pinData=""

            Logger.with("amount kimono").log(transaction.amount.toString())

            val amount = String.format(Locale.getDefault(), "%012d", transactionAmount)
            Logger.with("purchaserequest").logErr(amount)
            val now = Date()
            val date = DateUtils.dateFormatter.format(now)
            var icc= getIcc(terminalInfo,amount,date,transaction)

            val iswConfig = IswPos.getInstance().config
            var keyLabel=if (iswConfig.environment == Environment.Test) "000006" else "000002"

            var dedicatedFileTag="<DedicatedFileName>${icc.DEDICATED_FILE_NAME}</DedicatedFileName>"


            var  track2 = let {
                val neededLength = transaction.cardTrack2.length - 2
                val isVisa = transaction.cardTrack2.startsWith('4')
                val hasCharacter = transaction.cardTrack2.last().isLetter()

                // remove character suffix for visa
                if (isVisa && hasCharacter) transaction.cardTrack2.substring(0..neededLength)
                else transaction.cardTrack2
            }

        if(hasPin)

                pinData= """<pinData><ksnd>605</ksnd><pinType>Dukpt</pinType><ksn>${transaction.pinKsn}</ksn><pinBlock>${transaction.cardPIN}</pinBlock></pinData>"""

            if(false)
                dedicatedFileTag=  """"<DedicatedFileName>${icc.DEDICATED_FILE_NAME}</DedicatedFileName>"""


            val requestBody = """<?xml version="1.0" encoding="UTF-8" ?><purchaseRequest>
           <terminalInformation><batteryInformation>-1</batteryInformation> <currencyCode>${terminalInfo.currencyCode}</currencyCode><languageInfo>EN</languageInfo><merchantId>${terminalInfo.merchantId}</merchantId><merhcantLocation>${terminalInfo.merchantNameAndLocation}</merhcantLocation> <posConditionCode>00</posConditionCode> <posDataCode>${if (hasPin) "510101511344101" else "511101511344101"}</posDataCode> <posEntryMode>051</posEntryMode> <posGeoCode>00234000000000566</posGeoCode> <printerStatus>1</printerStatus><terminalId>${terminalInfo.terminalId}</terminalId> <terminalType>${device.name}</terminalType> <transmissionDate>${DateUtils.universalDateFormat.format(Date())}</transmissionDate> <uniqueId>${icc.INTERFACE_DEVICE_SERIAL_NUMBER}</uniqueId></terminalInformation><cardData><cardSequenceNumber>${transaction.csn}</cardSequenceNumber> <track2><pan>${transaction.cardPAN}</pan> <expiryMonth>${transaction.cardExpiry.takeLast(2)}</expiryMonth> <expiryYear>${transaction.cardExpiry.take(2)}</expiryYear> <track2>${track2}</track2></track2><emvData><AmountAuthorized>${icc.TRANSACTION_AMOUNT}</AmountAuthorized> <AmountOther>${icc.ANOTHER_AMOUNT}</AmountOther> <ApplicationInterchangeProfile>${icc.APPLICATION_INTERCHANGE_PROFILE}</ApplicationInterchangeProfile> <atc>${icc.APPLICATION_TRANSACTION_COUNTER}</atc><Cryptogram>${icc.AUTHORIZATION_REQUEST}</Cryptogram> <CryptogramInformationData>${icc.CRYPTOGRAM_INFO_DATA}</CryptogramInformationData> <CvmResults>${icc.CARD_HOLDER_VERIFICATION_RESULT}</CvmResults><iad>${icc.ISSUER_APP_DATA}</iad> <TransactionCurrencyCode>${icc.TRANSACTION_CURRENCY_CODE}</TransactionCurrencyCode> <TerminalVerificationResult>${icc.TERMINAL_VERIFICATION_RESULT}</TerminalVerificationResult> <TerminalCountryCode>${icc.TERMINAL_COUNTRY_CODE}</TerminalCountryCode> <TerminalType>${icc.TERMINAL_TYPE}</TerminalType> <TerminalCapabilities>${icc.TERMINAL_CAPABILITIES}</TerminalCapabilities> <TransactionDate>${icc.TRANSACTION_DATE}</TransactionDate> <TransactionType>${icc.TRANSACTION_TYPE}</TransactionType> <UnpredictableNumber>${icc.UNPREDICTABLE_NUMBER}</UnpredictableNumber> ${dedicatedFileTag}</emvData></cardData><fromAccount>${transaction.accountType.name}</fromAccount> <stan>${transaction.stan}</stan> <minorAmount>${transactionAmount}</minorAmount> ${pinData} <keyLabel>${keyLabel}</keyLabel></purchaseRequest>
        """

            return requestBody

        }


        fun toReversal(device:POSDevice,terminalInfo: TerminalInfo, transaction: TransactionInfo):String
        {
            val hasPin = transaction.cardPIN.isNotEmpty()
            var pinData=""
            if(hasPin) pinData= """<pinData><ksnd>605</ksnd><pinBlock></pinBlock><pinType>Dukpt</pinType> </pinData>"""
            var dedicatedFileTag=""
            var transactionAmount = transaction.amount

            val amount = String.format(Locale.getDefault(), "%012d", transactionAmount)
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
            return """<reversalRequestWithoutOriginalDate><terminalInformation><batteryInformation>-1</batteryInformation> <currencyCode>${terminalInfo.currencyCode}</currencyCode><languageInfo>EN</languageInfo><merchantId>${terminalInfo.merchantId}</merchantId><merhcantLocation>${terminalInfo.merchantNameAndLocation}</merhcantLocation> <posConditionCode>00</posConditionCode> <posDataCode>${if (hasPin) "510101511344101" else "511101511344101"}</posDataCode> <posEntryMode>051</posEntryMode> <posGeoCode>00234000000000566</posGeoCode> <printerStatus>1</printerStatus><terminalId>${terminalInfo.terminalId}</terminalId> <terminalType>${device.name}</terminalType> <transmissionDate>${DateUtils.universalDateFormat.format(Date())}</transmissionDate> <uniqueId>${icc.INTERFACE_DEVICE_SERIAL_NUMBER}</uniqueId></terminalInformation><cardData><cardSequenceNumber>${transaction.csn}</cardSequenceNumber><track2><pan>${transaction.cardPAN}</pan> <expiryMonth>${transaction.cardExpiry.takeLast(2)}</expiryMonth> <expiryYear>${transaction.cardExpiry.take(2)}</expiryYear> <track2>${track2}</track2></track2><wasFallback></wasFallback><emvData><AmountAuthorized>000000010000</AmountAuthorized> <AmountOther>${icc.ANOTHER_AMOUNT}</AmountOther><ApplicationInterchangeProfile>${icc.APPLICATION_INTERCHANGE_PROFILE}</ApplicationInterchangeProfile> <atc>${icc.APPLICATION_TRANSACTION_COUNTER}</atc><Cryptogram>${icc.AUTHORIZATION_REQUEST}</Cryptogram> <CryptogramInformationData>${icc.CRYPTOGRAM_INFO_DATA}</CryptogramInformationData> <CvmResults>${icc.CARD_HOLDER_VERIFICATION_RESULT}</CvmResults> <iad>${icc.ISSUER_APP_DATA}</iad> <TransactionCurrencyCode>${transactionCurrencyCode}</TransactionCurrencyCode><TerminalVerificationResult>${icc.TERMINAL_VERIFICATION_RESULT}</TerminalVerificationResult> <TerminalCountryCode>${terminalCOuntryCode}</TerminalCountryCode><TerminalType>${icc.TERMINAL_TYPE}</TerminalType> <TerminalCapabilities>${icc.TERMINAL_CAPABILITIES}</TerminalCapabilities> <TransactionDate>${icc.TRANSACTION_DATE}</TransactionDate> <TransactionType>${icc.TRANSACTION_TYPE}</TransactionType> <UnpredictableNumber>${icc.UNPREDICTABLE_NUMBER}</UnpredictableNumber> ${dedicatedFileTag}</emvData></cardData><stan>${transaction.stan}</stan><fromAccount>${transaction.accountType.name}</fromAccount> <toAccount>Current</toAccount> <minorAmount>${transactionAmount}</minorAmount> <messageReasonCode>4000</messageReasonCode> <rate></rate><settlementFee></settlementFee> <settlementCurrencyCode>${settlementCurrencyCode}</settlementCurrencyCode> <amountSettlement></amountSettlement> <attemptCount>1</attemptCount> <creationDate>190509</creationDate> <originalTransmissionDateTime>2019-05-09T09:10:49</originalTransmissionDateTime> <reversalType>Purchase</reversalType> <tmsConfiguredTerminalLocation></tmsConfiguredTerminalLocation>${pinData}<keyLabel>${keyLabel}</keyLabel> <originalAuthId>${originalAuthId}</originalAuthId> <notDisposable>false</notDisposable> <originalStan>${originalStan}</originalStan></reversalRequestWithoutOriginalDate>"""
        }


fun toReservation(device:POSDevice,terminalInfo: TerminalInfo, transaction: TransactionInfo):String{



    val hasPin = transaction.cardPIN.isNotEmpty()

    var pinData=""
    var transactionAmount = transaction.amount


    val amount = String.format(Locale.getDefault(), "%012d", transactionAmount)
    val now = Date()
    val date = DateUtils.dateFormatter.format(now)
    var icc= getIcc(terminalInfo,amount,date,transaction)

    val iswConfig = IswPos.getInstance().config
    var keyLabel=if (iswConfig.environment == Environment.Test) "000006" else "000002"

    var dedicatedFileTag="<DedicatedFileName>${icc.DEDICATED_FILE_NAME}</DedicatedFileName>"


    var  track2 = let {
        val neededLength = transaction.cardTrack2.length - 2
        val isVisa = transaction.cardTrack2.startsWith('4')
        val hasCharacter = transaction.cardTrack2.last().isLetter()

        // remove character suffix for visa
        if (isVisa && hasCharacter) transaction.cardTrack2.substring(0..neededLength)
        else transaction.cardTrack2
    }

    if(hasPin)

        pinData= """<pinData><ksnd>605</ksnd><pinType>Dukpt</pinType><ksn>${transaction.pinKsn}</ksn><pinBlock>${transaction.cardPIN}</pinBlock></pinData>"""

    if(false)
        dedicatedFileTag=  """"<DedicatedFileName>${icc.DEDICATED_FILE_NAME}</DedicatedFileName>"""
//
//
//    var  track2 = let {
//        val neededLength = transaction.cardTrack2.length - 2
//        val isVisa = transaction.cardTrack2.startsWith('4')
//        val hasCharacter = transaction.cardTrack2.last().isLetter()
//
//        // remove character suffix for visa
//        if (isVisa && hasCharacter) transaction.cardTrack2.substring(0..neededLength)
//        else transaction.cardTrack2
//    }
//    val amount = String.format(Locale.getDefault(), "%012d", transaction.amount)
//    val now = Date()
//    val date = DateUtils.dateFormatter.format(now)
//    var icc= getIcc(terminalInfo,amount,date,transaction)
//    if(false)
//        dedicatedFileTag=  """"<DedicatedFileName>${icc.DEDICATED_FILE_NAME}</DedicatedFileName>"""

//
//    val requestBody = """<?xml version="1.0" encoding="UTF-8" ?><purchaseRequest>
//                <terminalInformation><batteryInformation>-1</batteryInformation> <currencyCode>${terminalInfo.currencyCode}</currencyCode><languageInfo>EN</languageInfo><merchantId>${terminalInfo.merchantId}</merchantId><merhcantLocation>${terminalInfo.merchantNameAndLocation}</merhcantLocation> <posConditionCode>00</posConditionCode> <posDataCode>${if (hasPin) "510101511344101" else "511101511344101"}</posDataCode><posEntryMode>051</posEntryMode> <posGeoCode>00234000000000566</posGeoCode> <printerStatus>1</printerStatus><terminalId>${terminalInfo.terminalId}</terminalId> <terminalType>${device.name}</terminalType> <transmissionDate>${DateUtils.universalDateFormat.format(Date())}</transmissionDate> <uniqueId>${icc.INTERFACE_DEVICE_SERIAL_NUMBER}</uniqueId></terminalInformation><cardData><cardSequenceNumber>${transaction.csn}</cardSequenceNumber> <track2><pan>${transaction.cardPAN}</pan> <expiryMonth>${transaction.cardExpiry.takeLast(2)}</expiryMonth> <expiryYear>${transaction.cardExpiry.take(2)}</expiryYear> <track2>${track2}</track2></track2><emvData><AmountAuthorized>${icc.TRANSACTION_AMOUNT}</AmountAuthorized> <AmountOther>${icc.ANOTHER_AMOUNT}</AmountOther> <ApplicationInterchangeProfile>${icc.APPLICATION_INTERCHANGE_PROFILE}</ApplicationInterchangeProfile> <atc>${icc.APPLICATION_TRANSACTION_COUNTER}</atc><Cryptogram>${icc.AUTHORIZATION_REQUEST}</Cryptogram> <CryptogramInformationData>${icc.CRYPTOGRAM_INFO_DATA}</CryptogramInformationData> <CvmResults>${icc.CARD_HOLDER_VERIFICATION_RESULT}</CvmResults><iad>${icc.ISSUER_APP_DATA}</iad> <TransactionCurrencyCode>${icc.TRANSACTION_CURRENCY_CODE}</TransactionCurrencyCode> <TerminalVerificationResult>${icc.TERMINAL_VERIFICATION_RESULT}</TerminalVerificationResult> <TerminalCountryCode>${icc.TERMINAL_COUNTRY_CODE}</TerminalCountryCode> <TerminalType>${icc.TERMINAL_TYPE}</TerminalType> <TerminalCapabilities>${icc.TERMINAL_CAPABILITIES}</TerminalCapabilities> <TransactionDate>${icc.TRANSACTION_DATE}</TransactionDate> <TransactionType>${icc.TRANSACTION_TYPE}</TransactionType> <UnpredictableNumber>${icc.UNPREDICTABLE_NUMBER}</UnpredictableNumber> ${dedicatedFileTag}</emvData></cardData><fromAccount>${transaction.accountType.name}</fromAccount> <stan>${transaction.stan}</stan> <minorAmount>${ transaction.amount.toString()}</minorAmount> ${pinData} <keyLabel>${keyLabel}</keyLabel></purchaseRequest>
//        """

    return  """
        <reservationRequest><terminalInformation><batteryInformation>-1</batteryInformation> <currencyCode>${terminalInfo.currencyCode}</currencyCode><languageInfo>EN</languageInfo><merchantId>${terminalInfo.merchantId}</merchantId><merhcantLocation>${terminalInfo.merchantNameAndLocation}</merhcantLocation> <posConditionCode>00</posConditionCode> <posDataCode>${if (hasPin) "510101511344101" else "511101511344101"}</posDataCode><posEntryMode>051</posEntryMode> <posGeoCode>00234000000000566</posGeoCode> <printerStatus>1</printerStatus><terminalId>${terminalInfo.terminalId}</terminalId> <terminalType>${device.name}</terminalType> <transmissionDate>${DateUtils.universalDateFormat.format(Date())}</transmissionDate> <uniqueId>${icc.INTERFACE_DEVICE_SERIAL_NUMBER}</uniqueId></terminalInformation><cardData><cardSequenceNumber>${transaction.csn}</cardSequenceNumber><track2><pan>${transaction.cardPAN}</pan> <expiryMonth>${transaction.cardExpiry.takeLast(2)}</expiryMonth> <expiryYear>${transaction.cardExpiry.take(2)}</expiryYear> <track2>${track2}</track2></track2><wasFallback></wasFallback><emvData><AmountAuthorized>${icc.TRANSACTION_AMOUNT}</AmountAuthorized> <AmountOther>${icc.ANOTHER_AMOUNT}</AmountOther><ApplicationInterchangeProfile>${icc.APPLICATION_INTERCHANGE_PROFILE}</ApplicationInterchangeProfile> <atc>${icc.APPLICATION_TRANSACTION_COUNTER}</atc><Cryptogram>${icc.AUTHORIZATION_REQUEST}</Cryptogram> <CryptogramInformationData>${icc.CRYPTOGRAM_INFO_DATA}</CryptogramInformationData> <CvmResults>${icc.CARD_HOLDER_VERIFICATION_RESULT}</CvmResults> <iad>${icc.ISSUER_APP_DATA}</iad> <TransactionCurrencyCode>${icc.TRANSACTION_CURRENCY_CODE}</TransactionCurrencyCode><TerminalVerificationResult>${icc.TERMINAL_VERIFICATION_RESULT}</TerminalVerificationResult> <TerminalCountryCode>${icc.TERMINAL_COUNTRY_CODE}</TerminalCountryCode><TerminalType>${icc.TERMINAL_TYPE}</TerminalType> <TerminalCapabilities>${icc.TERMINAL_CAPABILITIES}</TerminalCapabilities> <TransactionDate>${icc.TRANSACTION_DATE}</TransactionDate> <TransactionType>${icc.TRANSACTION_TYPE}</TransactionType> <UnpredictableNumber>${icc.UNPREDICTABLE_NUMBER}</UnpredictableNumber> ${dedicatedFileTag}</emvData></cardData><fromAccount>${transaction.accountType.name}</fromAccount> <stan>${transaction.stan}</stan><minorAmount>${transactionAmount}</minorAmount><tmsConfiguredTerminalLocation></tmsConfiguredTerminalLocation>${pinData}<keyLabel>${keyLabel}</keyLabel></reservationRequest>
    """.trimIndent()
}



        fun toCompletion(device:POSDevice,terminalInfo: TerminalInfo, transaction: TransactionInfo):String{

            val hasPin = transaction.cardPIN.isNotEmpty()
            var pinData=""
            if(hasPin) pinData= """<pinData><ksnd>605</ksnd><pinBlock></pinBlock><pinType>Dukpt</pinType></pinData>"""
            var transactionAmount = transaction.amount

            val amount = String.format(Locale.getDefault(), "%012d", transactionAmount)
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
                <completionRequest><terminalInformation><batteryInformation>-1</batteryInformation> <currencyCode>${terminalInfo.currencyCode}</currencyCode><languageInfo>EN</languageInfo><merchantId>${terminalInfo.merchantId}</merchantId><merhcantLocation>${terminalInfo.merchantNameAndLocation}</merhcantLocation> <posConditionCode>00</posConditionCode> <posDataCode>${if (hasPin) "510101511344101" else "511101511344101"}</posDataCode> <posEntryMode>051</posEntryMode> <posGeoCode>00234000000000566</posGeoCode> <printerStatus>1</printerStatus><terminalId>${terminalInfo.terminalId}</terminalId> <terminalType>${device.name}</terminalType> <transmissionDate>${DateUtils.universalDateFormat.format(Date())}</transmissionDate> <uniqueId>${icc.INTERFACE_DEVICE_SERIAL_NUMBER}</uniqueId></terminalInformation><cardData><cardSequenceNumber>${transaction.csn}</cardSequenceNumber><track2><pan>${transaction.cardPAN}</pan> <expiryMonth>${transaction.cardExpiry.takeLast(2)}</expiryMonth> <expiryYear>${transaction.cardExpiry.take(2)}</expiryYear> <track2>${track2}</track2></track2><wasFallback></wasFallback><emvData><AmountAuthorized>000000010000</AmountAuthorized> <AmountOther>${icc.ANOTHER_AMOUNT}</AmountOther><ApplicationInterchangeProfile>${icc.APPLICATION_INTERCHANGE_PROFILE}</ApplicationInterchangeProfile> <atc>${icc.APPLICATION_TRANSACTION_COUNTER}</atc><Cryptogram>${icc.AUTHORIZATION_REQUEST}</Cryptogram> <CryptogramInformationData>${icc.CRYPTOGRAM_INFO_DATA}</CryptogramInformationData> <CvmResults>${icc.CARD_HOLDER_VERIFICATION_RESULT}</CvmResults> <iad>${icc.ISSUER_APP_DATA}</iad> <TransactionCurrencyCode>${icc.TRANSACTION_CURRENCY_CODE}</TransactionCurrencyCode><TerminalVerificationResult>${icc.TERMINAL_VERIFICATION_RESULT}</TerminalVerificationResult> <TerminalCountryCode>${icc.TERMINAL_COUNTRY_CODE}</TerminalCountryCode><TerminalType>${icc.TERMINAL_TYPE}</TerminalType> <TerminalCapabilities>${icc.TERMINAL_CAPABILITIES}</TerminalCapabilities> <TransactionDate>${icc.TRANSACTION_DATE}</TransactionDate> <TransactionType>${icc.TRANSACTION_TYPE}</TransactionType> <UnpredictableNumber>${icc.UNPREDICTABLE_NUMBER}</UnpredictableNumber>${dedicatedFileTag}</emvData></cardData><stan>${transaction.stan}</stan><minorAmount>${transactionAmount}</minorAmount><surcharge>0</surcharge><rate>0</rate><amountSettlement>0</amountSettlement><settlementFee>0</settlementFee><settlementCurrencyCode>${settlementCurrencyCode}</settlementCurrencyCode><tmsConfiguredTerminalLocation></tmsConfiguredTerminalLocation>${pinData}<keyLabel>${keyLabel}</keyLabel><originalAuthRef>${originalAuthId}</originalAuthRef><originalDateTime>${transaction.originalTransactionInfoData?.originalTransmissionDateAndTime}</originalDateTime><notDisposable>false</notDisposable><originalStan>${originalStan}</originalStan></completionRequest>
            """.trimIndent()
        }



        fun toRefund(device:POSDevice,terminalInfo: TerminalInfo, transaction: TransactionInfo):String{


            val hasPin = transaction.cardPIN.isNotEmpty()
            var pinData=""
            if(hasPin) pinData= """<pinData><ksnd>605</ksnd><pinBlock></pinBlock><pinType>Dukpt</pinType></pinData>"""
            var transactionAmount = transaction.amount

            val amount = String.format(Locale.getDefault(), "%012d", transactionAmount)
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
        <cardData><cardSequenceNumber>${transaction.csn}</cardSequenceNumber><track2><pan>${transaction.cardPAN}</pan> <expiryMonth>${transaction.cardExpiry.takeLast(2)}</expiryMonth> <expiryYear>${transaction.cardExpiry.take(2)}</expiryYear> <track2>${track2}</track2></track2>
                <emvData><AmountAuthorized>000000000000</AmountAuthorized> <AmountOther>${icc.ANOTHER_AMOUNT}</AmountOther> <ApplicationInterchangeProfile>${icc.APPLICATION_INTERCHANGE_PROFILE}</ApplicationInterchangeProfile> <atc>${icc.APPLICATION_TRANSACTION_COUNTER}</atc><Cryptogram>${icc.AUTHORIZATION_REQUEST}</Cryptogram> <CryptogramInformationData>${icc.CRYPTOGRAM_INFO_DATA}</CryptogramInformationData> <CvmResults>${icc.CARD_HOLDER_VERIFICATION_RESULT}</CvmResults><iad>${icc.ISSUER_APP_DATA}</iad> <TransactionCurrencyCode>${icc.TRANSACTION_CURRENCY_CODE}</TransactionCurrencyCode> <TerminalVerificationResult>${icc.TERMINAL_VERIFICATION_RESULT}</TerminalVerificationResult> <TerminalCountryCode>${icc.TERMINAL_COUNTRY_CODE}</TerminalCountryCode> <TerminalType>${icc.TERMINAL_TYPE}</TerminalType> <TerminalCapabilities>${icc.TERMINAL_CAPABILITIES}</TerminalCapabilities> <TransactionDate>${icc.TRANSACTION_DATE}</TransactionDate> <TransactionType>${icc.TRANSACTION_TYPE}</TransactionType> <UnpredictableNumber>${icc.UNPREDICTABLE_NUMBER}</UnpredictableNumber>${dedicatedFileTag}</emvData></cardData>
        <fromAccount>${transaction.accountType.name}</fromAccount>
        <stan>${transaction.stan}</stan>
        <originalTransmissionDateTime>${transaction.originalTransactionInfoData?.originalTransmissionDateAndTime}</originalTransmissionDateTime>
        <minorAmount>${transactionAmount}</minorAmount>
        <receivingInstitutionId>627784</receivingInstitutionId>
        <surcharge>2500</surcharge>
       ${pinData}
        <keyLabel>${keyLabel}</keyLabel>
		<originalAuthId>${transaction.originalTransactionInfoData?.originalAuthorizationId}</originalAuthId>
      </refundRequest>"""
        }

        private fun getIcc(terminalInfo: TerminalInfo, amount: String, date: String,transaction: TransactionInfo): IccData {

            transaction.iccData.TRANSACTION_AMOUNT=amount
//transaction.iccData.TERMINAL_CAPABILITIES="E0F0C8"
            return  transaction.iccData
        }




    }
}






@Root(name = "terminalInformation", strict = false)
internal class TerminalInformation {
    @field:Element(name = "terminalId", required = false)
    var terminalId: String = ""
    @field:Element(name = "merchantId", required = false)
    var merchantId: String = ""
    @field:Element(name = "merchantNameAndLocation", required = false)
    var merchantNameAndLocation: String = ""
    @field:Element(name = "merchantCategoryCode", required = false)
    var merchantCategoryCode: String = ""
    @field:Element(name = "countryCode", required = false)
    var countryCode: String = ""
    @field:Element(name = "currencyCode", required = false)
    var currencyCode: String = ""
    @field:Element(name = "callHomeTimeInMin", required = false)
    var callHomeTimeInMin: String = ""
    @field:Element(name = "serverTimeoutInSec", required = false)
    var serverTimeoutInSec: String = ""
    @field:Element(name = "serverIp", required = false)
    var serverIp: String = ""
    @field:Element(name = "kimono", required = false)
    var isKimono: Boolean = false
    @field:Element(name = "kimono3", required = false)
    var isKimono3: Boolean = false
    @field:Element(name = "capabilities", required = false)
    var capabilities: String? = null
    @field:Element(name = "serverPort", required = false)
    var serverPort: String = ""
    @field:Element(name = "serverUrl", required = false)
    var serverUrl: String = ""
    @field:Element(name = "agentId", required = false)
    var agentId: String = ""
    @field:Element(name = "agentEmail", required = false)
    var agentEmail: String = ""


    lateinit var error: TerminalInformation


    val isValid: Boolean
        get() {
            validateInfo()

            val properties = listOf(error.terminalId, error.merchantId, error.serverIp, error.capabilities,
                    error.merchantNameAndLocation, error.merchantCategoryCode, error.serverPort, error.serverUrl,
                    error.countryCode, error.currencyCode, error.callHomeTimeInMin, error.serverTimeoutInSec, error.agentId, error.agentEmail)


            // validate that all error properties are empty
            return properties.all { it?.isEmpty() ?: true }
        }

    val toTerminalInfo: TerminalInfo
        get() {
            return TerminalInfo(
                    terminalId = terminalId,
                    merchantId = merchantId,
                    merchantNameAndLocation = merchantNameAndLocation,
                    merchantCategoryCode = merchantCategoryCode,
                    countryCode = countryCode,
                    currencyCode = currencyCode,
                    callHomeTimeInMin = callHomeTimeInMin.toIntOrNull() ?: -1,
                    serverTimeoutInSec = serverTimeoutInSec.toIntOrNull() ?: -1,
                    isKimono = isKimono,
                    isKimono3 = isKimono3,
                    capabilities = capabilities,
                    serverIp = serverIp,
                    serverUrl = serverUrl,
                    serverPort = serverPort.toIntOrNull() ?: -1,
                    agentId = agentId,
                    agentEmail = agentEmail

            )
        }

    private fun validateInfo() {
        error = TerminalInformation()

        // validate terminalId
        val terminalIdValidator = InputValidator(terminalId)
                .isNotEmpty().isAlphaNumeric().isExactLength(8)
        // assign error message for field
        if (terminalIdValidator.hasError) error.terminalId = terminalIdValidator.message


        // validate merchant id
        val merchantId = InputValidator(merchantId)
                .isNotEmpty().isAlphaNumeric().isExactLength(15)
        // assign error message for field
        if (merchantId.hasError) error.merchantId = merchantId.message


        // validate merchant name and location
        val merchantNameAndLocation = InputValidator(merchantNameAndLocation).isNotEmpty()
        // assign error message for field
        if (merchantNameAndLocation.hasError) error.merchantNameAndLocation = merchantNameAndLocation.message


        // validate merchant code
        val merchantCode = InputValidator(merchantCategoryCode)
                .isNotEmpty().isNumber().isExactLength(4)
        // assign error message for field
        if (merchantCode.hasError) error.merchantCategoryCode = merchantCode.message


        // validate country code
        val countryCode = InputValidator(countryCode)
                .isNotEmpty().isNumber().hasMinLength(3).hasMaxLength(4)

        // assign error message for field
        if (countryCode.hasError) error.countryCode = countryCode.message


        // validate country code
        val currencyCode = InputValidator(currencyCode)
                .isNotEmpty().isNumber().isExactLength(3)
        // assign error message for field
        if (currencyCode.hasError) error.currencyCode = currencyCode.message


        val callHomeTime = InputValidator(callHomeTimeInMin)
                .isNotEmpty().isNumber().isNumberBetween(0, 120)
        // assign error message for field
        if (callHomeTime.hasError) error.callHomeTimeInMin = callHomeTime.message


        // validate server timeout
        val serverTimeout = InputValidator(serverTimeoutInSec)
                .isNotEmpty().isNumber().isNumberBetween(0, 120)
        // assign error message for field
        if (serverTimeout.hasError) error.serverTimeoutInSec = serverTimeout.message


        // validate server server url
        val serverUrl = InputValidator(serverUrl)
                .isNotEmpty()
        // assign error message for field if is kimono
        if (isKimono && serverUrl.hasError) error.serverUrl = serverUrl.message

        // validate agentId value
        val agentId = InputValidator(agentId).isNotEmpty().isNumber()

        //assign error message for field
        if (isKimono && agentId.hasError) error.agentId = agentId.message

        // validate agentEmail value
        val agentEmail = InputValidator(agentEmail).isNotEmpty().isAlphaNumeric()

        //assign error message for field
        if (isKimono && agentId.hasError) error.agentEmail = agentEmail.message


        // validate terminal capabilities value
        capabilities?.apply {
            val capabilitiesValidator = InputValidator(this)
                    .isNotEmpty().isExactLength(6).isAlphaNumeric()

            if (capabilitiesValidator.hasError) error.capabilities = capabilitiesValidator.message
        }

        // validate server IP
        val serverIp = InputValidator(serverIp)
                .isNotEmpty().isValidIp()
        // assign error message for field if not kimono
        if (serverIp.hasError && !isKimono) error.serverIp = serverIp.message


        // validate server server url
        val serverPort = InputValidator(serverPort)
                .isNotEmpty().isNumber()
                .hasMaxLength(5)
                .isNumberBetween(0, 65535)

        // assign error message for field if not kimono
        if (serverPort.hasError && !isKimono) error.serverPort = serverPort.message
    }
}
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
