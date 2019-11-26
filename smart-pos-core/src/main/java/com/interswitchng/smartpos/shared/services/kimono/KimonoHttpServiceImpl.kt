package com.interswitchng.smartpos.shared.services.kimono

import android.content.Context
import android.util.Base64
import com.igweze.ebi.simplecalladapter.Simple
import com.interswitchng.smartpos.shared.Constants
import com.interswitchng.smartpos.shared.interfaces.device.POSDevice
import com.interswitchng.smartpos.shared.interfaces.library.IsoService
import com.interswitchng.smartpos.shared.interfaces.retrofit.IKimonoHttpService
import com.interswitchng.smartpos.shared.models.core.TerminalInfo
import com.interswitchng.smartpos.shared.models.transaction.PaymentInfo
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.request.AccountType
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.request.PurchaseType
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.request.TransactionInfo
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.response.TransactionResponse
import com.interswitchng.smartpos.shared.services.iso8583.utils.DateUtils
import com.interswitchng.smartpos.shared.services.iso8583.utils.IsoUtils
import com.interswitchng.smartpos.shared.services.iso8583.utils.XmlPullParserHandler
import com.interswitchng.smartpos.shared.services.kimono.models.*
import com.interswitchng.smartpos.shared.services.kimono.models.PurchaseRequest
import com.interswitchng.smartpos.shared.utilities.Logger
import okhttp3.MediaType
import okhttp3.RequestBody
import java.io.ByteArrayInputStream
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

internal class KimonoHttpServiceImpl(private val context: Context, private val device: POSDevice,

                                     private val httpService: IKimonoHttpService) : IsoService{
    override fun downloadTerminalParameters(terminalId: String): Boolean {
       return true;
    }


    val logger by lazy { Logger.with(this.javaClass.name) }


    override fun downloadKey(terminalId: String): Boolean {

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


    override  fun initiateCardPurchase(terminalInfo: TerminalInfo, transaction: TransactionInfo): TransactionResponse? {

      val requestBody: String = PurchaseRequest.toCardPurchaseString(device,terminalInfo,transaction)
        val body = RequestBody.create(MediaType.parse("text/xml"), requestBody)


        try {
           val responseBody = httpService.makePurchase(body).run()
           var responseXml= responseBody.body()?.bytes()?.let { String(it) }


            val inputStream = ByteArrayInputStream(responseXml?.toByteArray(Charsets.UTF_8))
       var purchaseResponse=     XmlPullParserHandler().parse( inputStream)

            return if (!responseBody.isSuccessful || purchaseResponse == null) {
                TransactionResponse(
                        responseCode = IsoUtils.TIMEOUT_CODE,
                        authCode = "",
                        stan = "",
                        scripts = "",
                        responseDescription = responseBody.message()
                )
            } else {
                TransactionResponse(
                        responseCode =purchaseResponse.responseCode,//data.responseCode,
                        stan = purchaseResponse.stan,
                        authCode =  purchaseResponse.authCode,// data.authCode,
                        scripts =  purchaseResponse.stan,
                        responseDescription = purchaseResponse.description//data.description
                )
            }

        } catch (e: Exception) {
            logger.log(e.localizedMessage)
            e.printStackTrace()
          //  initiateReversal(request, request.stan) // TODO change stan to authId
            return TransactionResponse(IsoUtils.TIMEOUT_CODE, authCode = "", stan = "", scripts = "")
        }
    }


    override fun initiateRefund(terminalInfo: TerminalInfo, transaction: TransactionInfo): TransactionResponse? {
        // generate purchase request


        val requestBody: String = PurchaseRequest.toRefund(device,terminalInfo,transaction)
        val body = RequestBody.create(MediaType.parse("text/xml"), requestBody)

//        val request = RefundRequest.create(device.name, terminalInfo, transaction,"")
//        request.pinData?.apply {
//            // remove first 2 bytes to make 8 bytes
//            ksn = ksn.substring(4)
//        }
var authCode=""
        try {


            val responseBody = httpService.refund(body).run()
            var responseXml= responseBody.body()?.bytes()?.let { String(it) }


            val inputStream = ByteArrayInputStream(responseXml?.toByteArray(Charsets.UTF_8))
            var purchaseResponse=     XmlPullParserHandler().parse( inputStream)

            return if (!responseBody.isSuccessful || purchaseResponse == null) {
                TransactionResponse(
                        responseCode = IsoUtils.TIMEOUT_CODE,
                        authCode = "",
                        stan = "",
                        scripts = "",
                        responseDescription = responseBody.message()
                )
            } else {
                TransactionResponse(
                        responseCode =purchaseResponse.responseCode,//data.responseCode,
                        stan = purchaseResponse.stan,
                        authCode =  purchaseResponse.authCode,// data.authCode,
                        scripts =  purchaseResponse.stan,
                        responseDescription = purchaseResponse.description//data.description
                )
            }



        } catch (e: Exception) {
            logger.log(e.localizedMessage)
            e.printStackTrace()
             initiateReversal(terminalInfo, transaction) // TODO change stan to authId
            return TransactionResponse(IsoUtils.TIMEOUT_CODE, authCode = authCode, stan = "", scripts = "")
        }
    }





    override fun initiatePaycodePurchase(terminalInfo: TerminalInfo, code: String, paymentInfo: PaymentInfo): TransactionResponse? {

        val now = Date()
        val pan = IsoUtils.generatePan(code)
       /// val amount = String.format(Locale.getDefault(), "%012d", paymentInfo.amount)
        val stan = paymentInfo.getStan()
//        val now = Date()
       // val date = DateUtils.dateFormatter.format(now)
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

        // create transaction info
        val transaction = TransactionInfo(
                cardExpiry = expiry,
                cardPIN = "",
                cardPAN = pan,
                cardTrack2 = track2,
                stan = stan,
                accountType = AccountType.Default,
                amount = paymentInfo.amount,
                csn = "",
                src = src,
                purchaseType = PurchaseType.PayCode,
                icc = ""
        )

        val requestBody: String = PurchaseRequest.toCardPurchaseString(device,terminalInfo,transaction)
        val body = RequestBody.create(MediaType.parse("text/xml"), requestBody)



        try {
            val responseBody = httpService.makePurchase(body).run()
            var responseXml= responseBody.body()?.bytes()?.let { String(it) }


            val inputStream = ByteArrayInputStream(responseXml?.toByteArray(Charsets.UTF_8))
            var purchaseResponse=     XmlPullParserHandler().parse( inputStream)

            return if (!responseBody.isSuccessful || purchaseResponse == null) {
                TransactionResponse(
                        responseCode = IsoUtils.TIMEOUT_CODE,
                        authCode = "",
                        stan = "",
                        scripts = "",
                        responseDescription = responseBody.message()
                )
            } else {
                TransactionResponse(
                        responseCode =purchaseResponse.responseCode,//data.responseCode,
                        stan = purchaseResponse.stan,
                        authCode =  purchaseResponse.authCode,// data.authCode,
                        scripts =  purchaseResponse.stan,
                        responseDescription = purchaseResponse.description//data.description
                )
            }


        } catch (e: Exception) {
            logger.logErr(e.localizedMessage)
            e.printStackTrace()
            initiateReversal(terminalInfo, transaction)
            return TransactionResponse(IsoUtils.TIMEOUT_CODE, authCode = "", stan = "", scripts = "")
        }
    }




    override fun initiateCompletion(terminalInfo: TerminalInfo, transaction: TransactionInfo): TransactionResponse? {
        try {

//            request.pinData?.apply {
//                // remove first 2 bytes to make 8 bytes
//                ksn = ksn.substring(4)
//            }

            val requestBody: String = PurchaseRequest.toCompletion(device,terminalInfo,transaction)
            val body = RequestBody.create(MediaType.parse("text/xml"), requestBody)


            val responseBody = httpService.completion(body).run()
            var responseXml= responseBody.body()?.bytes()?.let { String(it) }


            val inputStream = ByteArrayInputStream(responseXml?.toByteArray(Charsets.UTF_8))
            var purchaseResponse=     XmlPullParserHandler().parse( inputStream)

            return if (!responseBody.isSuccessful || purchaseResponse == null) {
                TransactionResponse(
                        responseCode = IsoUtils.TIMEOUT_CODE,
                        authCode = "",
                        stan = "",
                        scripts = "",
                        responseDescription = responseBody.message()
                )
            } else {
                TransactionResponse(
                        responseCode =purchaseResponse.responseCode,//data.responseCode,
                        stan = purchaseResponse.stan,
                        authCode =  purchaseResponse.authCode,// data.authCode,
                        scripts =  purchaseResponse.stan,
                        responseDescription = purchaseResponse.description//data.description
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




            val requestBody: String = PurchaseRequest.toReservation(device,terminalInfo,transaction)
            val body = RequestBody.create(MediaType.parse("text/xml"), requestBody)


                val responseBody = httpService.reservation(body).run()
                var responseXml= responseBody.body()?.bytes()?.let { String(it) }


                val inputStream = ByteArrayInputStream(responseXml?.toByteArray(Charsets.UTF_8))
                var purchaseResponse=     XmlPullParserHandler().parse( inputStream)

                return if (!responseBody.isSuccessful || purchaseResponse == null) {
                    TransactionResponse(
                            responseCode = IsoUtils.TIMEOUT_CODE,
                            authCode = "",
                            stan = "",
                            scripts = "",
                            responseDescription = responseBody.message()
                    )
                } else {
                    TransactionResponse(
                            responseCode =purchaseResponse.responseCode,//data.responseCode,
                            stan = purchaseResponse.stan,
                            authCode =  purchaseResponse.authCode,// data.authCode,
                            scripts =  purchaseResponse.stan,
                            responseDescription = purchaseResponse.description//data.description
                    )
                }




        } catch (e: Exception) {
            logger.log(e.localizedMessage)
            e.printStackTrace()
            return TransactionResponse(IsoUtils.TIMEOUT_CODE, authCode = "", stan = "", scripts = "")
        }

    }


    override fun initiateReversal(terminalInfo: TerminalInfo, transaction: TransactionInfo): TransactionResponse? {

        val requestBody: String = PurchaseRequest.toReversal(device,terminalInfo,transaction)


        val body = RequestBody.create(MediaType.parse("text/xml"), requestBody)

 try {
            val responseBody = httpService.reversePurchase(body).run()
            var responseXml= responseBody.body()?.bytes()?.let { String(it) }


            val inputStream = ByteArrayInputStream(responseXml?.toByteArray(Charsets.UTF_8))
            var purchaseResponse=     XmlPullParserHandler().parse( inputStream)

            return if (!responseBody.isSuccessful || purchaseResponse == null) {
                TransactionResponse(
                        responseCode = IsoUtils.TIMEOUT_CODE,
                        authCode = "",
                        stan = "",
                        scripts = "",
                        responseDescription = responseBody.message()
                )
            } else {
                TransactionResponse(
                        responseCode =purchaseResponse.responseCode,//data.responseCode,
                        stan = purchaseResponse.stan,
                        authCode =  purchaseResponse.authCode,// data.authCode,
                        scripts =  purchaseResponse.stan,
                        responseDescription = purchaseResponse.description//data.description
                )
            }



        } catch (e: Exception) {
            logger.logErr(e.localizedMessage)
            e.printStackTrace()
            return TransactionResponse(IsoUtils.TIMEOUT_CODE, authCode = "", stan = "", scripts = "")
        }
    }


    private fun ByteArray.base64encode(): String {
        return String(Base64.encode(this, Base64.NO_WRAP))
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


//
//
//        private fun parseXmlToJsonObject(xml: String) : String {
//            var jsonObj: JSONObject? = null
//            try {
//                jsonObj = Xml.toJSONObject(xml)
//            } catch (e: JSONException) {
//                Log.e("JSON exception", e.message)
//                e.printStackTrace()
//            }
//
//            return jsonObj.toString()
//        }
//
//        fun<T> parseResponse(xml: String, clazz: Class<T>) : T {
//            try {
//                return initializeMoshi().adapter(clazz).fromJson(parseXmlToJsonObject(xml))!!
//            }catch (e: IOException){
//                throw IllegalArgumentException("Could not deserialize: $xml into class: $clazz")
//            }
//        }
//
//        private fun initializeMoshi(): Moshi {
//            return Moshi.Builder()
//                    .add(KotlinJsonAdapterFactory())
//                    .build()
//        }
    }


}
