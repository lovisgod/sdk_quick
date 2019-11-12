package com.interswitchng.smartpos.shared.services.kimono

import android.content.Context
import com.gojuno.koptional.None
import com.gojuno.koptional.Optional
import com.gojuno.koptional.Some
import com.igweze.ebi.simplecalladapter.Simple
import com.interswitchng.smartpos.shared.interfaces.library.KeyValueStore
import com.interswitchng.smartpos.shared.interfaces.library.KimonoHttpService
import com.interswitchng.smartpos.shared.interfaces.retrofit.IKimonoHttpService
import com.interswitchng.smartpos.shared.models.core.TerminalInfo
import com.interswitchng.smartpos.shared.models.kimono.request.*
import com.interswitchng.smartpos.shared.services.kimono.utils.KimonoResponseMessage
import com.interswitchng.smartpos.shared.models.transaction.PaymentInfo
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.request.AccountType
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.request.TransactionInfo
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.response.TransactionResponse
import com.interswitchng.smartpos.shared.services.iso8583.utils.DateUtils
import com.interswitchng.smartpos.shared.services.iso8583.utils.IsoUtils
import com.interswitchng.smartpos.shared.services.kimono.utils.KimonoXmlMessage
import com.interswitchng.smartpos.shared.utilities.Logger
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

internal class KimonoHttpServiceImpl(private val context: Context,
                                     private val store: KeyValueStore,
                                     private val httpService: IKimonoHttpService) : KimonoHttpService {







    val logger by lazy { Logger.with(this.javaClass.name) }


//    protected val terminalInfo: TerminalInfo by lazy { TerminalInfo.get(get())!! }

    override suspend fun callHome(request: CallHomeModel): Optional<String> {

       // terminalInfo.callHomeTimeInMin
//        val request = CallHomeModel(_termInfo = TermInfoModel(_mid = terminalInfo.merchantId,
//                _tid = terminalInfo.terminalId,`
//                _currencycode = terminalInfo.currencyCode,
//                _poscondcode =terminalInfo.countryCode,
//                _mloc = terminalInfo.merchantNameAndLocation,
//                _tim = "",
//                _uid = "",
//                _posgeocode = "",
//                _batt = "",
//                _csid = "",
//                _lang = "",
//                _pstat = "",
//                _ttype = ""
//        ));


        val response = httpService.callHome(request).await()

        return when (val codeResponse = response.first) {
            null -> None
            else ->

                Some(codeResponse)
        }
    }



    override  fun initiateCardPurchase(terminalInfo: TerminalInfo, transaction: TransactionInfo): TransactionResponse? {


        try {

            val now = Date()
            val processCode = "00" + transaction.accountType.value + "00"
            val hasPin = transaction.cardPIN.isNotEmpty()
            val stan = transaction.stan
            val randomReference = "000000$stan"

         terminalInfo.copy()

            val emvData=EmvData(TransactionDate = DateUtils.timeAndDateFormatter.format(now),
                    TerminalCountryCode = terminalInfo.countryCode,
                    TransactionType = "",
                    TerminalType ="PAX" ,
                    TransactionCurrencyCode ="" ,
                    TerminalCapabilities = "",
                    TerminalVerificationResult = "",
                    CvmResults ="" ,
                    AmountAuthorized ="" ,
                    AmountOther ="" ,
                    ApplicationInterchangeProfile ="" ,
                    atc ="" ,
                    Cryptogram ="" ,
                    CryptogramInformationData = "",
                    DedicatedFileName ="" ,
                    iad = "",
                    UnpredictableNumber =""
                    );

            val terminal =TerminalInformation(
                    merhcantLocation = terminalInfo.merchantNameAndLocation,
                    batteryInformation ="" ,
                    currencyCode = terminalInfo.currencyCode ,
                    merchantId = terminalInfo.merchantId,
                    terminalId = terminalInfo.terminalId,
                    uniqueId = "",
                    languageInfo = "",
                    printerStatus = "",
                    terminalType = "PAX",
                    posConditionCode = "",
                    posGeoCode = "",
                    transmissionDate =  DateUtils.timeAndDateFormatter.format(now),
                    posEntryMode = "",
                    posDataCode = ""
            )


            val track2  =
                    Track2(track2 = transaction.cardTrack2,
                    expiryMonth = transaction.cardExpiry,
                    expiryYear = transaction.cardExpiry,
                    pan =transaction.cardPAN )

            val cardData =CardData(track2=track2,emvData=emvData,cardSequenceNumber =  "")

            val pinData= PinData(
                    pinType ="Dukpt" ,
                    ksnd ="605" )

           val purchaseRequest =PurchaseRequest(cardData =cardData,
                    fromAccount =when(transaction.accountType ) {
                        AccountType.Default -> "Default"
                        AccountType.Savings -> "Saving"
                        AccountType.Current -> "Current"
                        AccountType.Credit -> "Credit"
                    },
                    stan = stan,
                    keyLabel = "000002",
                    minorAmount = "125000",
                    pinData =pinData,
                   terminalInformation = terminal)


            val response = httpService.purchaseRequest(purchaseRequest)//;//.await()

//
//            return when (val codeResponse = response.first) {
//                null -> TransactionResponse(IsoUtils.TIMEOUT_CODE, authCode = "", stan = "", scripts = "")
//                else ->
//
//                    Some(codeResponse)
//            }


            val responseMsg = KimonoResponseMessage(KimonoXmlMessage.readXml(response.toString()))
            return responseMsg.xmlMessage.let {
                val authCode = it.getObjectValue<String?>("/purchaseResponse/authId") ?: ""
                val code = it.getObjectValue<String?>("/purchaseResponse/field39")?:""
                val scripts = it.getObjectValue<String?>("/purchaseResponse/referenceNumber")?:""
                return@let TransactionResponse(responseCode = code, authCode =  authCode, stan = stan, scripts = scripts)
            }




        } catch (e: Exception) {
            logger.log(e.localizedMessage)
            e.printStackTrace()
            return TransactionResponse(IsoUtils.TIMEOUT_CODE, authCode = "", stan = "", scripts = "")
        }

    }



    override fun initiatePaycodePurchase(terminalInfo: TerminalInfo, code: String, paymentInfo: PaymentInfo): TransactionResponse? {

        try {


            val pan = generatePan(code)
            val amount = String.format(Locale.getDefault(), "%012d", paymentInfo.amount)
            val now = Date()
            val processCode = "001000"
            val stan = paymentInfo.getStan()
            val randomReference = "000000$stan"
            val date = DateUtils.dateFormatter.format(now)
            val src = "501"

            var responseString =""
            val responseMsg = KimonoResponseMessage(KimonoXmlMessage.readXml(responseString))
            return responseMsg.xmlMessage.let {
                val authCode = it.getObjectValue<String?>("/purchaseResponse/authId") ?: ""
                val code = it.getObjectValue<String?>("/purchaseResponse/field39")?:""
                val scripts = it.getObjectValue<String?>("/purchaseResponse/referenceNumber")?:""
                return@let TransactionResponse(responseCode = code, authCode =  authCode, stan = stan, scripts = scripts)
            }


        } catch (e: Exception) {
        logger.log(e.localizedMessage)
        e.printStackTrace()
        return TransactionResponse(IsoUtils.TIMEOUT_CODE, authCode = "", stan = "", scripts = "")
    }


    }




    override fun initiatePreAuthorization(
            terminalInfo: TerminalInfo,
            transaction: TransactionInfo
    ): TransactionResponse? {
        try {

            val now = Date()
            val processCode = "60" + transaction.accountType.value + "00"
            val hasPin = transaction.cardPIN.isNotEmpty()
            val stan = transaction.stan
            val randomReference = "000000$stan"

            var responseString =""
            val responseMsg = KimonoResponseMessage(KimonoXmlMessage.readXml(responseString))
            return responseMsg.xmlMessage.let {
                val authCode = it.getObjectValue<String?>("/purchaseResponse/authId") ?: ""
                val code = it.getObjectValue<String?>("/purchaseResponse/field39")?:""
                val scripts = it.getObjectValue<String?>("/purchaseResponse/referenceNumber")?:""
                return@let TransactionResponse(responseCode = code, authCode =  authCode, stan = stan, scripts = scripts)
            }


        } catch (e: Exception) {
            logger.log(e.localizedMessage)
            e.printStackTrace()
            return TransactionResponse(IsoUtils.TIMEOUT_CODE, authCode = "", stan = "", scripts = "")
        }
    }

    override fun initiateCompletion(terminalInfo: TerminalInfo, transaction: TransactionInfo): TransactionResponse? {
        try {
            val now = Date()
            val processCode = "61" + transaction.accountType.value + "00"
            val hasPin = transaction.cardPIN.isNotEmpty()
            val stan = transaction.stan
            val randomReference = "000000$stan"


            var responseString =""
            val responseMsg = KimonoResponseMessage(KimonoXmlMessage.readXml(responseString))
            return responseMsg.xmlMessage.let {
                val authCode = it.getObjectValue<String?>("/purchaseResponse/authId") ?: ""
                val code = it.getObjectValue<String?>("/purchaseResponse/field39")?:""
                val scripts = it.getObjectValue<String?>("/purchaseResponse/referenceNumber")?:""
                return@let TransactionResponse(responseCode = code, authCode =  authCode, stan = stan, scripts = scripts)
            }

        } catch (e: Exception) {
            logger.log(e.localizedMessage)
            e.printStackTrace()
            return TransactionResponse(IsoUtils.TIMEOUT_CODE, authCode = "", stan = "", scripts = "")
        }

    }





    private fun generatePan(code: String): String {
        val bin = "506179"
        var binAndCode = "$bin$code"
        val remainder = 12 - code.length
        // pad if less than 12
        if (remainder > 0)
            binAndCode = "$binAndCode${"0".repeat(remainder)}"

        var nSum = 0
        var alternate = true
        for (i in binAndCode.length - 1 downTo 0) {

            var d = binAndCode[i] - '0'

            if (alternate)
                d *= 2

            // We add two digits to handle
            // cases that make two digits after
            // doubling
            nSum += d / 10
            nSum += d % 10

            alternate = !alternate
        }

        val unitDigit = nSum % 10
        val checkDigit = 10 - unitDigit

        return "$binAndCode$checkDigit"
    }

    private suspend fun <T> Simple<T>.await(): Pair<T?, String?> {
        return suspendCoroutine { continuation ->
            process { response, t ->
                val message =  t?.message ?: t?.localizedMessage

                // log errors
                if (message != null) logger.log(message)
                // pair result and error
                val result = Pair(response, message)
                // return response
                continuation.resume(result)
            }
        }
    }


}