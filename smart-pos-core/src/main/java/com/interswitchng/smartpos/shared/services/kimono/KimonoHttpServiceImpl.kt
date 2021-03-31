package com.interswitchng.smartpos.shared.services.kimono

import android.content.Context
import android.util.Base64
import com.igweze.ebi.simplecalladapter.Simple
import com.interswitchng.smartpos.modules.main.fragments.CardTransactionsFragment
import com.interswitchng.smartpos.modules.main.models.BillPaymentModel
import com.interswitchng.smartpos.modules.main.transfer.TokenPassportResponse
import com.interswitchng.smartpos.shared.Constants
import com.interswitchng.smartpos.shared.interfaces.device.POSDevice
import com.interswitchng.smartpos.shared.interfaces.library.IsoService
import com.interswitchng.smartpos.shared.interfaces.library.KeyValueStore
import com.interswitchng.smartpos.shared.interfaces.library.TransactionLogService
import com.interswitchng.smartpos.shared.interfaces.retrofit.IAgentService
import com.interswitchng.smartpos.shared.interfaces.retrofit.IKimonoHttpService
import com.interswitchng.smartpos.shared.interfaces.retrofit.TokenService
import com.interswitchng.smartpos.shared.models.core.TerminalInfo
import com.interswitchng.smartpos.shared.models.printer.info.TransactionType
import com.interswitchng.smartpos.shared.models.transaction.PaymentInfo
import com.interswitchng.smartpos.shared.models.transaction.PaymentType
import com.interswitchng.smartpos.shared.models.transaction.TransactionLog
import com.interswitchng.smartpos.shared.models.transaction.TransactionResult
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.request.AccountType
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.request.IccData
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.request.PurchaseType
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.request.TransactionInfo
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.response.TransactionResponse
import com.interswitchng.smartpos.shared.models.utils.XmlStringConverter
import com.interswitchng.smartpos.shared.services.iso8583.utils.DateUtils
import com.interswitchng.smartpos.shared.services.iso8583.utils.IsoUtils
import com.interswitchng.smartpos.shared.services.iso8583.utils.XmlPullParserHandler
import com.interswitchng.smartpos.shared.services.iso8583.utils.XmlPullParserHandlerBP
import com.interswitchng.smartpos.shared.services.kimono.models.*
import com.interswitchng.smartpos.shared.services.kimono.models.CallHomeRequest
import com.interswitchng.smartpos.shared.services.kimono.models.PurchaseRequest
import com.interswitchng.smartpos.shared.utilities.DeviceUtils
import com.interswitchng.smartpos.shared.utilities.DisplayUtils
import com.interswitchng.smartpos.shared.utilities.Logger
import com.pixplicity.easyprefs.library.Prefs
import okhttp3.MediaType
import okhttp3.RequestBody
import java.io.ByteArrayInputStream
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

internal class KimonoHttpServiceImpl(private val context: Context,
                                     private val store: KeyValueStore,
                                     private val device: POSDevice,
                                     private val transactionLogService: TransactionLogService,
                                     private val httpService: IKimonoHttpService,
                                     private val agentService: IAgentService,
                                     private val tokenService: TokenService) : IsoService {


    private lateinit var transactionResult: TransactionResult


    val logger by lazy { Logger.with(this.javaClass.name) }


    override fun downloadAgentId(terminalId: String): AgentIdResponse? {

        try {
            val responseBody = agentService.getAgentId(Constants.AGENT_ID + terminalId).execute()

            return if (responseBody.isSuccessful) {
                responseBody.body()
            } else {
                AgentIdResponse()
            }
        } catch (e: Exception) {
            logger.log(e.localizedMessage)
            e.printStackTrace()
        }
        return AgentIdResponse()
    }

    override fun downloadKey(terminalId: String, ip: String, port: Int, isNibbsTest: Boolean): Boolean {


        try {
            val responseBody = agentService.getKimonoKey(url = Constants.ISW_KIMONO_KEY_URL, cmd = "key", terminalId = terminalId, pkmod = Constants.PKMOD, pkex = Constants.PKEX, pkv = "1", keyset_id = "000002", der_en = "1").execute()


            return if (!responseBody.isSuccessful) {
                Logger.with("KeyResponseCode").log(responseBody.errorBody().toString())
                false
            } else {
                // load test keys
                val tik = Constants.ISW_DUKPT_IPEK
                val ksn = Constants.ISW_DUKPT_KSN

                // load keys
                device.loadInitialKey(tik, ksn)

                Logger.with("KeyResponseMessage").log(responseBody.body()?.string()
                        ?: "no response")
                true
            }
        } catch (e: Exception) {
            logger.log(e.localizedMessage)
            e.printStackTrace()
        }
        return false


    }

    override fun downloadTerminalParametersForKimono(serialNumber: String): AllTerminalInfo? {
        try {
            val url = Constants.ISW_TERMINAL_CONFIG_URL + serialNumber
            val responseBody = agentService.downloadTerminalParameters(url).execute()
            if (responseBody.isSuccessful) {
                return responseBody.body()
            } else {
                AllTerminalInfo()
            }
        } catch (e: Exception) {
            logger.log(e.localizedMessage)
            e.printStackTrace()
        }

        return AllTerminalInfo()

    }


    override suspend fun callHome(terminalInfo: TerminalInfo): Boolean {

        val uid = ""
        val request = CallHomeRequest.create(device.name, terminalInfo, uid)

        val response = httpService.callHome(request).run()
        val data = response.body()
        return true
    }


    override fun initiateCardPurchase(terminalInfo: TerminalInfo, transaction: TransactionInfo): TransactionResponse? {
        val requestBody: String = PurchaseRequest.toCardPurchaseString(device, terminalInfo, transaction)
        val body = RequestBody.create(MediaType.parse("text/xml"), requestBody)

        try {
            val responseBody = httpService.makePurchase(body).run()
            var responseXml = responseBody.body()?.bytes()?.let { String(it) }


            val inputStream = ByteArrayInputStream(responseXml?.toByteArray(Charsets.UTF_8))
            var purchaseResponse = XmlPullParserHandler().parse(inputStream)

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
                        responseCode = purchaseResponse.responseCode,//data.responseCode,
                        stan = purchaseResponse.stan,
                        authCode = purchaseResponse.authCode,// data.authCode,
                        scripts = purchaseResponse.stan,
                        responseDescription = purchaseResponse.description,//data.description
                        rrn = purchaseResponse.referenceNumber
                )
            }

        } catch (e: Exception) {
            //logger.log(e.localizedMessage)
            e.printStackTrace()
            //  initiateReversal(request, request.stan) // TODO change stan to authId
            return TransactionResponse(IsoUtils.TIMEOUT_CODE, authCode = "", stan = "", scripts = "")
        }

    }

    override fun initiateCNPPurchase(terminalInfo: TerminalInfo, transaction: TransactionInfo): TransactionResponse? {
        val requestBody: String = PurchaseRequest.toCNPPurchaseString(device, terminalInfo, transaction)
        val body = RequestBody.create(MediaType.parse("text/xml"), requestBody)

        try {
            val responseBody = httpService.makePurchase(body).run()
            var responseXml = responseBody.body()?.bytes()?.let { String(it) }


            val inputStream = ByteArrayInputStream(responseXml?.toByteArray(Charsets.UTF_8))
            var purchaseResponse = XmlPullParserHandler().parse(inputStream)

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
                        responseCode = purchaseResponse.responseCode,//data.responseCode,
                        stan = purchaseResponse.stan,
                        authCode = purchaseResponse.authCode,// data.authCode,
                        scripts = purchaseResponse.stan,
                        responseDescription = purchaseResponse.description,//data.description
                        rrn = purchaseResponse.referenceNumber

                )
            }

        } catch (e: Exception) {
            logger.log(e.localizedMessage)
            e.printStackTrace()
            //  initiateReversal(request, request.stan) // TODO change stan to authId
            return TransactionResponse(IsoUtils.TIMEOUT_CODE, authCode = "", stan = "", scripts = "")
        }
    }

    override fun initiateBillPayment(terminalInfo: TerminalInfo, txnInfo: TransactionInfo): TransactionResponse? {
        val requestCashOutBody: String = PurchaseRequest.toCashOutInquiry(device, terminalInfo, txnInfo)
        val bodyCashOut = RequestBody.create(MediaType.parse("text/xml"), requestCashOutBody)

        try {
            var url = Constants.KIMONO_CASH_OUT_ENDPOINT_INQUIRY
            if (terminalInfo.isKimono3) {
                url = Constants.KIMONO_3_END_POINT
            }
            val responseBody = httpService.makeCashOutInquiry(url, bodyCashOut).run()
            val purchaseResponse = responseBody.body()

            return if (!responseBody.isSuccessful || purchaseResponse?.responseCode == null) {
                TransactionResponse(
                        responseCode = IsoUtils.TIMEOUT_CODE,
                        authCode = "",
                        stan = "",
                        scripts = "",
                        responseDescription = responseBody.message(),
                        type = TransactionType.CashOutInquiry
                )
            } else if (responseBody.isSuccessful && purchaseResponse.responseCode != "00") {

                val now = Date()
                val pinStatus = when {
                    purchaseResponse.responseCode == IsoUtils.OK -> "PIN Verified"
                    else -> "PIN Unverified"
                }
                transactionResult = TransactionResult(
                        paymentType = PaymentType.Card,
                        dateTime = DateUtils.universalDateFormat.format(now),
                        amount = txnInfo.amount.toString(),
                        type = TransactionType.CashOutInquiry,
                        responseMessage = IsoUtils.getIsoResultMsg(purchaseResponse.responseCode)
                                ?: purchaseResponse.description,
                        responseCode = purchaseResponse.responseCode,
                        cardPan = txnInfo.cardPAN,
                        cardExpiry = txnInfo.cardExpiry,
                        cardType = CardTransactionsFragment.CompletionData.cardType,
                        stan = purchaseResponse.stan,
                        pinStatus = pinStatus,
                        AID = txnInfo.aid,
                        code = "",
                        telephone = purchaseResponse.customerId,
                        icc = txnInfo.iccString,
                        src = txnInfo.src,
                        csn = txnInfo.csn,
                        cardPin = txnInfo.cardPIN,
                        cardTrack2 = txnInfo.cardTrack2,
                        time = now.time
                )

                TransactionResponse(
                        responseCode = purchaseResponse.responseCode,//data.responseCode,
                        stan = purchaseResponse.stan,
                        responseDescription = purchaseResponse.description,//data.description
                        type = TransactionType.CashOutInquiry
                )


            } else {
                val now = Date()
                val pinStatus = when {
                    purchaseResponse.responseCode == IsoUtils.OK -> "PIN Verified"
                    else -> "PIN Unverified"
                }
                transactionResult = TransactionResult(
                        paymentType = PaymentType.Card,
                        dateTime = DateUtils.universalDateFormat.format(now),
                        amount = txnInfo.amount.toString(),
                        type = TransactionType.CashOutInquiry,
                        authorizationCode = purchaseResponse.authId,
                        responseMessage = IsoUtils.getIsoResultMsg(purchaseResponse.responseCode)!!,
                        responseCode = purchaseResponse.responseCode,
                        cardPan = txnInfo.cardPAN,
                        cardExpiry = txnInfo.cardExpiry,
                        cardType = CardTransactionsFragment.CompletionData.cardType,
                        stan = purchaseResponse.stan,
                        pinStatus = pinStatus,
                        AID = txnInfo.aid,
                        code = "",
                        telephone = purchaseResponse.customerId,
                        icc = txnInfo.iccString,
                        src = txnInfo.src,
                        csn = txnInfo.csn,
                        cardPin = txnInfo.cardPIN,
                        cardTrack2 = txnInfo.cardTrack2,
                        time = now.time
                )
                logTransaction(transactionResult)
                cashOutPay(purchaseResponse, terminalInfo, txnInfo)
            }

        } catch (e: Exception) {
            //logger.log(e.localizedMessage)
            e.printStackTrace()
            //  initiateReversal(request, request.stan) // TODO change stan to authId
            return TransactionResponse(IsoUtils.TIMEOUT_CODE, authCode = "", stan = "", scripts = "", type = TransactionType.CashOutInquiry)
        }

    }

    override fun initiateGeneralBillPayment(terminalInfo: TerminalInfo, txnInfo: TransactionInfo, paymentModel: BillPaymentModel): TransactionResponse? {

        if (Prefs.getBoolean("isAirtime", false)) {
            var airResp = billPaymentPayAirtime(terminalInfo, txnInfo, paymentModel)
            return  airResp
        } else {
            var requestCashOutBody = PurchaseRequest.toBillPaymentInquiry(device, terminalInfo, txnInfo, paymentModel)
            val bodyCashOut = RequestBody.create(MediaType.parse("text/xml"), requestCashOutBody)

            try {
                var url = Constants.KIMONO_CASH_OUT_ENDPOINT_INQUIRY
                if (terminalInfo.isKimono3) {
                    url = Constants.KIMONO_3_END_POINT
                }
                val responseBody = httpService.makeBillPaymentInquiry(url, "Bearer ${Prefs.getString("token", "")}", bodyCashOut).run()
                val purchaseResponse = responseBody.body()
                print(purchaseResponse)
                return if (!responseBody.isSuccessful || purchaseResponse?.responseCode == null) {
                    TransactionResponse(
                            responseCode = IsoUtils.TIMEOUT_CODE,
                            authCode = "",
                            stan = "",
                            scripts = "",
                            responseDescription = responseBody.message(),
                            type = TransactionType.CashOutInquiry
                    )
                } else if (responseBody.isSuccessful && purchaseResponse.responseCode != "00") {

                    val now = Date()
                    val pinStatus = when {
                        purchaseResponse.responseCode == IsoUtils.OK -> "PIN Verified"
                        else -> "PIN Unverified"
                    }
                    transactionResult = TransactionResult(
                            paymentType = PaymentType.Card,
                            dateTime = DateUtils.universalDateFormat.format(now),
                            amount = txnInfo.amount.toString(),
                            type = TransactionType.CashOutInquiry,
                            responseMessage = IsoUtils.getIsoResultMsg(purchaseResponse.responseCode)
                                    ?: purchaseResponse.description,
                            responseCode = purchaseResponse.responseCode,
                            cardPan = txnInfo.cardPAN,
                            cardExpiry = txnInfo.cardExpiry,
                            cardType = CardTransactionsFragment.CompletionData.cardType,
                            stan = purchaseResponse.stan,
                            pinStatus = pinStatus,
                            AID = txnInfo.aid,
                            code = "",
                            telephone = purchaseResponse.customerId,
                            icc = txnInfo.iccString,
                            src = txnInfo.src,
                            csn = txnInfo.csn,
                            cardPin = txnInfo.cardPIN,
                            cardTrack2 = txnInfo.cardTrack2,
                            time = now.time
                    )

                    TransactionResponse(
                            responseCode = purchaseResponse.responseCode,//data.responseCode,
                            stan = purchaseResponse.stan,
                            responseDescription = purchaseResponse.description,//data.description
                            type = TransactionType.CashOutInquiry
                    )


                } else {
                    val now = Date()
                    val pinStatus = when {
                        purchaseResponse.responseCode == IsoUtils.OK -> "PIN Verified"
                        else -> "PIN Unverified"
                    }
                    transactionResult = TransactionResult(
                            paymentType = PaymentType.Card,
                            dateTime = DateUtils.universalDateFormat.format(now),
                            amount = txnInfo.amount.toString(),
                            type = TransactionType.CashOutInquiry,
                            authorizationCode = purchaseResponse.authId,
                            responseMessage = IsoUtils.getIsoResultMsg(purchaseResponse.responseCode)!!,
                            responseCode = purchaseResponse.responseCode,
                            cardPan = txnInfo.cardPAN,
                            cardExpiry = txnInfo.cardExpiry,
                            cardType = CardTransactionsFragment.CompletionData.cardType,
                            stan = purchaseResponse.stan,
                            pinStatus = pinStatus,
                            AID = txnInfo.aid,
                            code = "",
                            telephone = purchaseResponse.customerId,
                            icc = txnInfo.iccString,
                            src = txnInfo.src,
                            csn = txnInfo.csn,
                            cardPin = txnInfo.cardPIN,
                            cardTrack2 = txnInfo.cardTrack2,
                            time = now.time
                    )
                    logTransaction(transactionResult)
                    billPaymentPay(purchaseResponse, terminalInfo, txnInfo, paymentModel)
                }

            } catch (e: Exception) {
                //logger.log(e.localizedMessage)
                e.printStackTrace()
                //  initiateReversal(request, request.stan) // TODO change stan to authId
                return TransactionResponse(IsoUtils.TIMEOUT_CODE, authCode = "", stan = "", scripts = "", type = TransactionType.CashOutInquiry)
            }
        }

    }

    override fun initiateTransfer(terminalInfo: TerminalInfo, txnInfo: TransactionInfo, destinationAccountNumber: String, receivingInstitutionId: String): TransactionResponse? {
        val xmlString: String = PurchaseRequest.toTransferString(device, terminalInfo, txnInfo, destinationAccountNumber, receivingInstitutionId)
        val bodyCashOut = XmlStringConverter().toBody(xmlString)

        try {
            var url = Constants.KIMONO_TRANSFER_END_POINT
            val responseBody = httpService.makeTransfer(bodyCashOut, "Bearer ${Prefs.getString("token", "")}").run()
            val purchaseResponse = responseBody.body()

            return if (!responseBody.isSuccessful || purchaseResponse?.responseCode == null) {
                TransactionResponse(
                        responseCode = IsoUtils.TIMEOUT_CODE,
                        authCode = "",
                        stan = "",
                        scripts = "",
                        responseDescription = responseBody.message(),
                        type = TransactionType.Transfer
                )
            } else {
                TransactionResponse(
                        responseCode = purchaseResponse.responseCode,//data.responseCode,
                        stan = purchaseResponse.stan,
                        responseDescription = purchaseResponse.description,//data.description
                        type = TransactionType.Transfer
                )


            }

        } catch (e: Exception) {
            //logger.log(e.localizedMessage)
            e.printStackTrace()
            return TransactionResponse(IsoUtils.TIMEOUT_CODE, authCode = "", stan = "", scripts = "", type = TransactionType.Transfer)
        }
    }

    override fun getToken(terminalInfo: TerminalInfo) {
        val xmlString: String = PurchaseRequest.toTokenString(terminalInfo)
        val bodyCashOut = XmlStringConverter().toBody(xmlString)

        try {
            val responseBody = tokenService.getToken(bodyCashOut).run()
            val response = responseBody.body()
            println(response)

             if (responseBody.isSuccessful && !response?.token.isNullOrEmpty()) {
                Prefs.putString("token", response?.token.toString())
            }
        } catch (e: Exception)
        {
            //logger.log(e.localizedMessage)
            e.printStackTrace()
        }

    }

    private fun cashOutPay(response: BillPaymentResponse, terminalInfo: TerminalInfo, txnInfo: TransactionInfo): TransactionResponse? {

        var url = Constants.KIMONO_CASH_OUT_ENDPOINT_PAY
        if (terminalInfo.isKimono3) {
            url = Constants.KIMONO_3_END_POINT
        }

        val requestBody: String = PurchaseRequest.toCashOutPayString(response, terminalInfo, txnInfo)
        val body = RequestBody.create(MediaType.parse("text/xml"), requestBody)

        try {
            val responseBody = httpService.makeCashOutPayment(url, body, "").run()
            var purchaseResponse = responseBody.body()

            return if (!responseBody.isSuccessful || purchaseResponse == null) {
                TransactionResponse(
                        responseCode = IsoUtils.TIMEOUT_CODE,
                        authCode = "",
                        stan = "",
                        scripts = "",
                        responseDescription = responseBody.message(),
                        type = TransactionType.CashOutPay
                )
            } else {
                TransactionResponse(
                        responseCode = purchaseResponse.responseCode,//data.responseCode,
                        stan = purchaseResponse.stan,
                        authCode = purchaseResponse.authId,//purchaseResponse.authCode,// data.authCode,
                        scripts = purchaseResponse.stan,
                        responseDescription = purchaseResponse.description,//data.description
                        name = purchaseResponse.customerDescription,
                        ref = purchaseResponse.transactionRef,
                        rrn = purchaseResponse.retrievalRefNumber,
                        type = TransactionType.CashOutPay

                )
            }

        } catch (e: Exception) {
            logger.log(e.localizedMessage)
            e.printStackTrace()
            //  initiateReversal(request, request.stan) // TODO change stan to authId
            return TransactionResponse(IsoUtils.TIMEOUT_CODE, authCode = "", stan = "", scripts = "", type = TransactionType.CashOutPay)
        }
    }

    private fun billPaymentPay(response: BillPaymentResponse, terminalInfo: TerminalInfo, txnInfo: TransactionInfo, paymentModel: BillPaymentModel): TransactionResponse? {

        var url = Constants.KIMONO_CASH_OUT_ENDPOINT_PAY
        if (terminalInfo.isKimono3) {
            url = Constants.KIMONO_3_END_POINT
        }

        val requestBody: String = PurchaseRequest.toBillPaymentPayString(response, terminalInfo, txnInfo, paymentModel)
        val body = RequestBody.create(MediaType.parse("text/xml"), requestBody)

        try {
            val responseBody = httpService.makeCashOutPayment(url, body, "bearer ${Prefs.getString("token", "")}").run()
            var purchaseResponse = responseBody.body()

            return if (!responseBody.isSuccessful || purchaseResponse == null) {
                TransactionResponse(
                        responseCode = IsoUtils.TIMEOUT_CODE,
                        authCode = "",
                        stan = "",
                        scripts = "",
                        responseDescription = responseBody.message(),
                        type = TransactionType.CashOutPay
                )
            } else {
                TransactionResponse(
                        responseCode = purchaseResponse.responseCode,//data.responseCode,
                        stan = purchaseResponse.stan,
                        authCode = purchaseResponse.authId,//purchaseResponse.authCode,// data.authCode,
                        scripts = purchaseResponse.stan,
                        responseDescription = purchaseResponse.description,//data.description
                        name = purchaseResponse.customerDescription,
                        ref = purchaseResponse.transactionRef,
                        rrn = purchaseResponse.retrievalRefNumber,
                        type = TransactionType.CashOutPay

                )
            }

        } catch (e: Exception) {
            logger.log(e.localizedMessage)
            e.printStackTrace()
            //  initiateReversal(request, request.stan) // TODO change stan to authId
            return TransactionResponse(IsoUtils.TIMEOUT_CODE, authCode = "", stan = "", scripts = "", type = TransactionType.CashOutPay)
        }
    }

    private fun billPaymentPayAirtime(terminalInfo: TerminalInfo, txnInfo: TransactionInfo, paymentModel: BillPaymentModel): TransactionResponse? {

        var url = Constants.KIMONO_CASH_OUT_ENDPOINT_PAY
//        if (terminalInfo.isKimono3) {
//            url = Constants.KIMONO_3_END_POINT
//        }

        val requestBody: String = PurchaseRequest.toBillPaymentPayAirtimeString( terminalInfo, txnInfo, paymentModel)
        val body = RequestBody.create(MediaType.parse("text/xml"), requestBody)

        try {
            val responseBody = httpService.makeCashOutPayment(url, body, "bearer ${Prefs.getString("token", "")}").run()
            var purchaseResponse = responseBody.body()

            return if (!responseBody.isSuccessful || purchaseResponse == null) {
                TransactionResponse(
                        responseCode = IsoUtils.TIMEOUT_CODE,
                        authCode = "",
                        stan = "",
                        scripts = "",
                        responseDescription = responseBody.message(),
                        type = TransactionType.CashOutPay
                )
            } else {
                TransactionResponse(
                        responseCode = purchaseResponse.responseCode,//data.responseCode,
                        stan = purchaseResponse.stan,
                        authCode = purchaseResponse.authId,//purchaseResponse.authCode,// data.authCode,
                        scripts = purchaseResponse.stan,
                        responseDescription = purchaseResponse.description,//data.description
                        name = purchaseResponse.customerDescription,
                        ref = purchaseResponse.transactionRef,
                        rrn = purchaseResponse.retrievalRefNumber,
                        type = TransactionType.CashOutPay

                )
            }

        } catch (e: Exception) {
            logger.log(e.localizedMessage)
            e.printStackTrace()
            //  initiateReversal(request, request.stan) // TODO change stan to authId
            return TransactionResponse(IsoUtils.TIMEOUT_CODE, authCode = "", stan = "", scripts = "", type = TransactionType.CashOutPay)
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
                        responseDescription = purchaseResponse.description,//data.description
                        rrn = purchaseResponse.referenceNumber
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
                iccString = "",
                iccData = IccData(),
                src = src,
                csn = "",
                amount = paymentInfo.amount,
                stan = stan,
                purchaseType = PurchaseType.PayCode,
                accountType = AccountType.Default,
                pinKsn = ""
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
                        responseDescription = purchaseResponse.description,//data.description
                        rrn = purchaseResponse.referenceNumber
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
                        responseDescription = purchaseResponse.description,//data.description
                        rrn = purchaseResponse.referenceNumber
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
                        responseDescription = purchaseResponse.description,//data.description
                        rrn = purchaseResponse.referenceNumber
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
                        responseDescription = purchaseResponse.description,//data.description
                        rrn = purchaseResponse.referenceNumber
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


    fun logTransaction(result: TransactionResult) {
        // get and log transaction to storage
        val resultLog = TransactionLog.fromResult(result)
        transactionLogService.logTransactionResult(resultLog)
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
