package com.interswitchng.smartpos.shared.services.kimono

import android.app.Application
import android.util.Base64
import com.igweze.ebi.simplecalladapter.Simple
import com.interswitchng.smartpos.shared.Constants
import com.interswitchng.smartpos.shared.interfaces.device.POSDevice
import com.interswitchng.smartpos.shared.interfaces.library.IsoService
import com.interswitchng.smartpos.shared.interfaces.library.KeyValueStore
import com.interswitchng.smartpos.shared.interfaces.retrofit.IKimonoHttpService
import com.interswitchng.smartpos.shared.models.core.TerminalInfo
import com.interswitchng.smartpos.shared.models.transaction.PaymentInfo
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.request.AccountType
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.request.IccData
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.request.PurchaseType
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.request.TransactionInfo
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.response.TransactionResponse
import com.interswitchng.smartpos.shared.services.iso8583.utils.DateUtils
import com.interswitchng.smartpos.shared.services.iso8583.utils.IsoUtils
import com.interswitchng.smartpos.shared.services.iso8583.utils.NibssIsoMessage
import com.interswitchng.smartpos.shared.services.kimono.models.*
import com.interswitchng.smartpos.shared.services.kimono.models.CompletionRequest
import com.interswitchng.smartpos.shared.services.kimono.models.PurchaseRequest
import com.interswitchng.smartpos.shared.services.kimono.models.ReversalRequest
import com.interswitchng.smartpos.shared.services.kimono.utils.KimonoResponseMessage
import com.interswitchng.smartpos.shared.services.kimono.utils.KimonoXmlMessage
import com.interswitchng.smartpos.shared.utilities.Logger
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

internal class KimonoHttpServiceImpl(private val device: POSDevice,
                                     private val app: Application,
                                     private val store: KeyValueStore,
                                     private val httpService: IKimonoHttpService) : IsoService{


    val logger by lazy { Logger.with(this.javaClass.name) }


    override fun downloadKey(terminalId: String, ip: String, port: Int): Boolean {

        // load test keys
        val tik = Constants.ISW_DUKPT_IPEK
        val ksn = Constants.ISW_DUKPT_KSN

        // load keys
        device.loadInitialKey(tik, ksn)
        return true
    }


    override suspend fun callHome(terminalInfo: TerminalInfo): Boolean {

//terminalInfo.merchantId

// terminalInfo.terminalId

        val uid = ""
        val request = CallHomeRequest.create(device.name, terminalInfo, uid)


        val response = httpService.callHome(request).run()
        val data = response.body()
        return true
    }

    override fun initiateCardPurchase(terminalInfo: TerminalInfo, transaction: TransactionInfo): TransactionResponse? {
        // generate purchase request
        val request = PurchaseRequest.create(device.name, terminalInfo, transaction)
        request.pinData?.apply {
            // remove first 2 bytes to make 8 bytes
            ksn = ksn.substring(4)
        }

        try {
            val response = httpService.makePurchase(request).run()
            val data = response.body()

            return if (!response.isSuccessful || data == null) {
                TransactionResponse(
                        responseCode = response.code().toString(),
                        authCode = "",
                        stan = "",
                        scripts = "",
                        responseDescription = response.message()
                )
            } else {
                TransactionResponse(
                        responseCode = data.responseCode,
                        stan = data.stan,
                        authCode = data.authCode,
                        scripts = "",
                        responseDescription = data.description
                )
            }

        } catch (e: Exception) {
            logger.log(e.localizedMessage)
            e.printStackTrace()
            reversePurchase(request, request.stan) // TODO change stan to authId
            return TransactionResponse(IsoUtils.TIMEOUT_CODE, authCode = "", stan = "", scripts = "")
        }
    }




    override fun initiatePaycodePurchase(terminalInfo: TerminalInfo, code: String, paymentInfo: PaymentInfo): TransactionResponse? {

        val now = Date()
        val pan = IsoUtils.generatePan(code)
        val amount = String.format(Locale.getDefault(), "%012d", paymentInfo.amount)
        val stan = paymentInfo.getStan()
        val date = DateUtils.dateFormatter.format(now)
        val src = "501"

        // generate expiry date using current date
        val expiryDate = Calendar.getInstance().let {
            it.time = now
            val currentYear = it.get(Calendar.YEAR)
            it.set(Calendar.YEAR, currentYear + 1)
            it.time
        }

        // format track 2
        val expiry = DateUtils.yearAndMonthFormatter.format(expiryDate)
        val track2 = "${pan}D$expiry"

        // get icc data
        val iccData = getIcc(terminalInfo, amount, date)

        // create transaction info
        val transaction = TransactionInfo(
                cardExpiry = expiry,
                cardPIN = "",
                cardPAN = pan,
                cardTrack2 = track2,
                iccData = iccData,
                iccString = iccData.iccAsString,
                stan = stan,
                accountType = AccountType.Default,
                amount = paymentInfo.amount,
                csn = "",
                src = src,
                purchaseType = PurchaseType.PayCode,
                pinKsn = ""
        )


        // generate purchase request
        val request = PurchaseRequest.create(device.name, terminalInfo, transaction)

        try {
            val response = httpService.makePurchase(request).run()
            val data = response.body()

            return if (!response.isSuccessful || data == null) {
                TransactionResponse(
                        responseCode = IsoUtils.TIMEOUT_CODE,
                        authCode = "",
                        stan = "",
                        scripts = ""
                )
            } else {
                TransactionResponse(
                        responseCode = data.responseCode,
                        stan = data.stan,
                        authCode = data.authCode,
                        scripts = ""
                )
            }

        } catch (e: Exception) {
            logger.logErr(e.localizedMessage)
            e.printStackTrace()
            reversePurchase(request, request.stan) // TODO change stan to authId
            return TransactionResponse(IsoUtils.TIMEOUT_CODE, authCode = "", stan = "", scripts = "")
        }
    }




    override fun initiateCompletion(terminalInfo: TerminalInfo, transaction: TransactionInfo): TransactionResponse? {
        try {




            val request = CompletionRequest.create(device.name, terminalInfo, transaction)
            request.pinData?.apply {
                // remove first 2 bytes to make 8 bytes
                ksn = ksn.substring(4)
            }


                val response = httpService.completion(request).run()
                val data = response.body()

                return if (!response.isSuccessful || data == null) {
                    TransactionResponse(
                            responseCode = response.code().toString(),
                            authCode = "",
                            stan = "",
                            scripts = "",
                            responseDescription = response.message()
                    )
                } else {
                    TransactionResponse(
                            responseCode = data.responseCode,
                            stan = data.stan,
                            authCode = data.authCode,
                            scripts = "",
                            responseDescription = data.referenceNumber
                    )
                }




        } catch (e: Exception) {
            logger.log(e.localizedMessage)
            e.printStackTrace()
            return TransactionResponse(IsoUtils.TIMEOUT_CODE, authCode = "", stan = "", scripts = "")
        }

    }






    override fun initiatePreAuthorization(terminalInfo: TerminalInfo, transaction: TransactionInfo): TransactionResponse? {
        try {

            val stan = transaction.stan




            val request = ReservationRequest.create(device.name, terminalInfo, transaction)
            request.pinData?.apply {
                // remove first 2 bytes to make 8 bytes
                ksn = ksn.substring(4)
            }


            val response = httpService.reservation(request).run()
            val data = response.body()

            return if (!response.isSuccessful || data == null) {
                TransactionResponse(
                        responseCode = response.code().toString(),
                        authCode = "",
                        stan = "",
                        scripts = "",
                        responseDescription = response.message()
                )
            } else {
                TransactionResponse(
                        responseCode = data.responseCode,
                        stan = data.stan,
                        authCode = data.authCode,
                        scripts = "",
                        responseDescription = data.referenceNumber
                )
            }




        } catch (e: Exception) {
            logger.log(e.localizedMessage)
            e.printStackTrace()
            return TransactionResponse(IsoUtils.TIMEOUT_CODE, authCode = "", stan = "", scripts = "")
        }

    }





    fun reversePurchase(txn: PurchaseRequest, authId: String?): TransactionResponse {
        // create reversal request
        val request = ReversalRequest.create(txn, authId)

        try {
            // initiate reversal request
            val response = httpService.reversePurchase(request).run()
            val data = response.body()

            return if (!response.isSuccessful || data == null) {
                TransactionResponse(
                        responseCode = IsoUtils.TIMEOUT_CODE,
                        authCode = "",
                        stan = "",
                        scripts = "")
            } else {
                TransactionResponse(
                        responseCode = data.responseCode,
                        stan = data.stan,
                        authCode = data.authCode,
                        scripts = "")
            }

        } catch (e: Exception) {
            logger.logErr(e.localizedMessage)
            e.printStackTrace()
            return TransactionResponse(IsoUtils.TIMEOUT_CODE, authCode = "", stan = "", scripts = "")
        }

    }
















    private fun getIcc(terminalInfo: TerminalInfo, amount: String, date: String): IccData {
        val authorizedAmountTLV = String.format("9F02%02d%s", amount.length / 2, amount)
        val transactionDateTLV = String.format("9A%02d%s", date.length / 2, date)
        val iccData = "9F260831BDCBC7CFF6253B9F2701809F10120110A50003020000000000000000000000FF9F3704F435D8A29F3602052795050880000000" +
                "${transactionDateTLV}9C0100${authorizedAmountTLV}5F2A020566820238009F1A0205669F34034103029F3303E0F8C89F3501229F0306000000000000"

        // remove leadin zero if exits
        val currencyCode = if (terminalInfo.currencyCode.length > 3) terminalInfo.currencyCode.substring(1) else terminalInfo.currencyCode
        val countryCode = if (terminalInfo.countryCode.length > 3) terminalInfo.countryCode.substring(1) else terminalInfo.countryCode



        return IccData().apply {
            TRANSACTION_AMOUNT = amount
            ANOTHER_AMOUNT = "000000000000"
            APPLICATION_INTERCHANGE_PROFILE = "3800"
            APPLICATION_TRANSACTION_COUNTER = "0527"
            CRYPTOGRAM_INFO_DATA = "80"
            CARD_HOLDER_VERIFICATION_RESULT = "410302"
            ISSUER_APP_DATA = "0110A50003020000000000000000000000FF"
            TRANSACTION_CURRENCY_CODE = currencyCode
            TERMINAL_VERIFICATION_RESULT = "0880000000"
            TERMINAL_COUNTRY_CODE = countryCode
            TERMINAL_TYPE = "22"
            TERMINAL_CAPABILITIES = terminalInfo.capabilities ?: "E0F8C8"
            TRANSACTION_DATE = date
            TRANSACTION_TYPE = "00"
            UNPREDICTABLE_NUMBER = "F435D8A2"
            DEDICATED_FILE_NAME = ""
            AUTHORIZATION_REQUEST = "31BDCBC7CFF6253B"

            iccAsString = iccData
        }

    }




    private fun ByteArray.base64encode(): String {
        return String(Base64.encode(this, Base64.NO_WRAP))
    }

//
//
////All these are still pending
//    override fun initiatePreAuthorization(
//            terminalInfo: TerminalInfo,
//            transaction: TransactionInfo
//    ): TransactionResponse? {
//        try {
//
//
//
//            val now = Date()
//            val processCode = "60" + transaction.accountType.value + "00"
//            val hasPin = transaction.cardPIN.isNotEmpty()
//            val stan = transaction.stan
//            val randomReference = "000000$stan"
//
//            var responseString =""
//            val responseMsg = KimonoResponseMessage(KimonoXmlMessage.readXml(responseString))
//            return responseMsg.xmlMessage.let {
//                val authCode = it.getObjectValue<String?>("/purchaseResponse/authId") ?: ""
//                val code = it.getObjectValue<String?>("/purchaseResponse/field39")?:""
//                val scripts = it.getObjectValue<String?>("/purchaseResponse/referenceNumber")?:""
//                return@let TransactionResponse(responseCode = code, authCode =  authCode, stan = stan, scripts = scripts)
//            }
//
//
//        } catch (e: Exception) {
//            logger.log(e.localizedMessage)
//            e.printStackTrace()
//            return TransactionResponse(IsoUtils.TIMEOUT_CODE, authCode = "", stan = "", scripts = "")
//        }
//    }
//
//




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