 package com.interswitchng.smartpos

import android.graphics.Bitmap
import android.util.Log
import com.gojuno.koptional.None
import com.gojuno.koptional.Some
import com.interswitchng.smartpos.modules.main.models.PaymentModel
import com.interswitchng.smartpos.shared.Constants
import com.interswitchng.smartpos.shared.interfaces.device.DevicePrinter
import com.interswitchng.smartpos.shared.interfaces.device.POSDevice
import com.interswitchng.smartpos.shared.interfaces.library.IsoService
import com.interswitchng.smartpos.shared.interfaces.library.KeyValueStore
import com.interswitchng.smartpos.shared.models.core.TerminalInfo
import com.interswitchng.smartpos.shared.models.printer.info.PrintStatus
import com.interswitchng.smartpos.shared.models.printer.info.TransactionType
import com.interswitchng.smartpos.shared.models.transaction.*
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.CardType
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.EmvMessage
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.EmvResult
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.request.AccountType
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.request.PurchaseType
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.request.TransactionInfo
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.response.TransactionResponse
import com.interswitchng.smartpos.shared.services.iso8583.utils.FileUtils
import com.interswitchng.smartpos.shared.services.iso8583.utils.IsoUtils
import com.interswitchng.smartpos.shared.services.kimono.models.AgentIdResponse
import com.interswitchng.smartpos.shared.services.kimono.models.AllTerminalInfo
import com.interswitchng.smartpos.shared.services.kimono.models.TerminalInformation
import com.interswitchng.smartpos.shared.utilities.DisplayUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import org.koin.core.parameter.parametersOf
import org.koin.standalone.KoinComponent
import org.koin.standalone.get
import org.koin.standalone.inject
import java.io.InputStream
import java.util.*

 class IswTxnHandler(private val posDevice: POSDevice? = null): KoinComponent {



    private val store : KeyValueStore by inject()

     val xterminalInfo = TerminalInfo.get(store)
     val isKimono = xterminalInfo?.isKimono ?: false
     internal val isoService: IsoService =  get { parametersOf(isKimono) }


     // communication channel with cardreader
     // this uses coroutine channels to update messages
     private val channel = Channel<EmvMessage>()
    private val emv by lazy { posDevice?.getEmvCardReader()!! }

    fun getCardPAN() = emv.getPan()


    /**
     * this method should be called when the card is inserted or when you want to start reading the card
     * @return[Unit]**/
    suspend fun setupTransaction(amount: Int, terminalInfo: TerminalInfo, scope: CoroutineScope) {
        emv.setupTransaction(amount, terminalInfo, channel, scope)
    }

    /**
     * this method should be called when the card is inserted or when you want to start reading the card
     * after [setupTransaction] method has returned a card read message
     * @return[EmvResult]**/
    suspend fun startTransaction(): EmvResult {
        return emv.startTransaction()
    }


    /**
     *initiate a transfer transaction*/
    fun processTransferTransaction(
            paymentModel: PaymentModel,
            accountType: AccountType,
            terminalInfo: TerminalInfo,
            destinationAccountNumber: String? = "",
            receivingInstitutionId: String? = ""
    ): CardReadTransactionResponse {
            val emvData = emv.getTransactionInfo()

            // return response based on data
            if (emvData != null) {
                // create transaction info and issue online transfer request
                val response =  {
                    val txnInfo =
                            TransactionInfo.fromEmv(
                                    emvData,
                                    paymentModel,
                                    PurchaseType.Card,
                                    accountType
                            )
                    println("this is this ${paymentModel.type}")
                      when (paymentModel.type) {
                         PaymentModel.TransactionType.BILL_PAYMENT ->
                             isoService.initiateTransfer(terminalInfo, txnInfo, destinationAccountNumber.toString(), receivingInstitutionId.toString())
                         PaymentModel.TransactionType.TRANSFER ->
                             isoService.initiateTransfer(terminalInfo, txnInfo, destinationAccountNumber.toString(), receivingInstitutionId.toString())

                         else -> null
                     }

                }.invoke()

                when (response) {
                    null -> {
                      return CardReadTransactionResponse(
                              onlineProcessResult = CardOnlineProcessResult.NO_RESPONSE,
                              transactionResponse = null,
                              emvData = null
                      )
                    }
                    else -> {
                        // complete transaction by applying scripts
                        // only when responseCode is 'OK'

                        // react to result code
                            return when (emv.completeTransaction(response)) {
                                EmvResult.OFFLINE_APPROVED -> CardReadTransactionResponse(
                                        onlineProcessResult = CardOnlineProcessResult.ONLINE_APPROVED,
                                        transactionResponse = response,
                                        emvData = emvData
                                )
                                else ->  CardReadTransactionResponse(
                                        onlineProcessResult = CardOnlineProcessResult.ONLINE_DENIED,
                                        transactionResponse = response,
                                        emvData = emvData
                                )
                            }

                    }
                }
            } else {
               return CardReadTransactionResponse(
                        onlineProcessResult = CardOnlineProcessResult.NO_EMV,
                        transactionResponse = null,
                        emvData = null
                )
            }
        }




     /**
      *This function is used in processing purchase transaction,
      * pre-authorization transaction, refund transaction and some other types of transactions*/
     fun processCardTransaction(
             paymentModel: PaymentModel,
             accountType: AccountType,
             terminalInfo: TerminalInfo
     ): CardReadTransactionResponse {
         val emvData = emv.getTransactionInfo()

         // return response based on data
         if (emvData != null) {
             // create transaction info and issue online transfer request
             val response =  {
                 val txnInfo =
                         TransactionInfo.fromEmv(
                                 emvData,
                                 paymentModel,
                                 PurchaseType.Card,
                                 accountType
                         )
                 println("this is this ${paymentModel.type}")
                 when (paymentModel.type) {
                     PaymentModel.TransactionType.CARD_PURCHASE ->  isoService.initiateCardPurchase(terminalInfo, txnInfo)
                     PaymentModel.TransactionType.PRE_AUTHORIZATION -> isoService.initiatePreAuthorization(terminalInfo, txnInfo)
                     PaymentModel.TransactionType.REFUND -> isoService.initiateRefund(terminalInfo, txnInfo)
                     else -> null
                 }

             }.invoke()

             when (response) {
                 null -> {
                     return CardReadTransactionResponse(
                             onlineProcessResult = CardOnlineProcessResult.NO_RESPONSE,
                             transactionResponse = null,
                             emvData = null
                     )
                 }
                 else -> {
                     // complete transaction by applying scripts
                     // only when responseCode is 'OK'

                     // react to result code
                     return when (emv.completeTransaction(response)) {
                         EmvResult.OFFLINE_APPROVED -> CardReadTransactionResponse(
                                 onlineProcessResult = CardOnlineProcessResult.ONLINE_APPROVED,
                                 transactionResponse = response,
                                 emvData = emvData
                         )
                         else ->  CardReadTransactionResponse(
                                 onlineProcessResult = CardOnlineProcessResult.ONLINE_DENIED,
                                 transactionResponse = response,
                                 emvData = emvData
                         )
                     }

                 }
             }
         } else {
             return CardReadTransactionResponse(
                     onlineProcessResult = CardOnlineProcessResult.NO_EMV,
                     transactionResponse = null,
                     emvData = null
             )
         }
     }




     /**
      *this function initiate a reversal transaction
      * @param `is` [TerminalInfo]
      * @return [Unit]*/
     internal fun processReversal(
             txnInfo: TransactionInfo,
             terminalInfo: TerminalInfo
     ): TransactionResponse? {
             val response =  {
                    isoService.initiateReversal(terminalInfo, txnInfo)
             }.invoke()

             when (response) {
                 null -> {
                     return null
                 }
                 else -> {
                     return  response
                 }
             }
     }



     /**
      *this function initiates a paycode transaction and returns the response
      * @param [terminalInfo]
      * @param [paymentInfo]
      * @param [code]
      * @return [Unit]*/
     internal fun processPayCode(
             terminalInfo: TerminalInfo,
             paymentInfo: PaymentInfo,
             code: String
     ): TransactionResult? {
         val response =  {
             isoService.initiatePaycodePurchase(terminalInfo, code, paymentInfo)
         }.invoke()

         return when (response) {
             null -> null
             else -> {

                 val now = Date()
                 val responseMsg = IsoUtils.getIsoResultMsg(response.responseCode)
                         ?: "Unknown Error"

                 // extract result
                 TransactionResult(
                         paymentType = PaymentType.PayCode,
                         dateTime = DisplayUtils.getIsoString(now),
                         amount = DisplayUtils.getAmountString(paymentInfo),
                         type = TransactionType.Purchase,
                         authorizationCode = response.authCode,
                         responseMessage = responseMsg,
                         responseCode = response.responseCode,
                         stan = response.stan, pinStatus = "", AID = "", code = "",
                         cardPan = "", cardExpiry = "", cardType = CardType.None,
                         telephone = "", cardTrack2 = "",
                         cardPin = "", icc = "", csn = "", src = "", time = -1L)
             }
         }
     }




    /**
     *initiate get token request and save the result in Pref
     * @param `is` [TerminalInfo]
     * @return [Unit]*/
    suspend fun getToken(terminalInfo: TerminalInfo) {
        isoService.getToken(terminalInfo)
    }


    suspend fun cancelTransaction() {
        emv.cancelTransaction()
    }

    /**
     *this function saves the terminal info in Pref
     * @param `is` [TerminalInfo]
     * @return [Unit]*/
    fun saveTerminalInfo(terminalInfo: TerminalInfo) {
        terminalInfo.persist(store)
    }

    /**
     *this function returns terminal info that is saved in pref
     * @return [TerminalInfo]*/

    fun getTerminalInfo(): TerminalInfo? {
        return TerminalInfo.get(store)
    }

    /**
     *this function helps print a slip in bitmap format
     * @param `` [Bitmap]
     * create your receipt layout any way
     * convert the layout to a bitmap then pass the bitmap as a parameter to this function*/

    fun printslip(bitmap: Bitmap): PrintStatus {
        return posDevice?.printer!!.printSlipNew(bitmap)
    }

    /**
     *this function check if a printer can print. This function should be called before the printslip method
     * @return [PrintStatus]*/

    fun checkPrintStatus(): PrintStatus {
        return posDevice?.printer!!.canPrint()
    }

     /**
      *this function downloads kimono parameters for a terminal and returns the response
      * @param [serialNumber]
      * @return [AllTerminalInfo]*/
    suspend fun downloadTmKimParam(serialNumber: String): AllTerminalInfo? {
        return isoService.downloadTerminalParametersForKimono(serialNumber)
    }

     /**
      *this function downloads nibbs parameters for a terminal and returns the response
      * @param [terminalId]
      * @param[ip]
      * @param [port]
      * @return [Boolean]*/
     fun downloadTmNibParam(terminalId: String, ip: String, port: Int): Boolean? {
         return isoService.downloadTerminalParameters(terminalId, ip, port)
     }


     /**
      * this function download keys for the terminals
      * @param [terminalId]
      * @param[ip]
      * @param [port]
      * @param [isNibbsTest]
      *  @return [Boolean]*/

     fun downloadKeys(terminalId: String, ip: String, port: Int, isNibbsTest: Boolean? = false): Boolean {
         return isoService.downloadKey(terminalId, ip, port, isNibbsTest!!)
     }

     /**
      * this function download agent information for the terminals
      * @param [terminalId]
      *  @return [Boolean]*/

     fun downloadAgentInfo(terminalId: String): AgentIdResponse? {
         return isoService.downloadAgentId(terminalId)
     }

     /**
      * this function retrieves the terminal information from a file (an input stream)
      * @param [xmlFile]
      *  @return [TerminalInformation]*/
     internal fun getTermInfo(xmlFile: InputStream): TerminalInformation {
         return FileUtils.readXml(TerminalInformation::class.java, xmlFile)
     }



     fun getTerminalInfoFromResponse(info : AllTerminalInfo): TerminalInfo{
         return TerminalInfo(
                 terminalId = info.terminalInfoBySerials?.terminalCode.toString(),
                 merchantId = info.terminalInfoBySerials?.merchantId.toString(),
                 merchantNameAndLocation = info.terminalInfoBySerials?.cardAcceptorNameLocation.toString(),
                 merchantCategoryCode = info.terminalInfoBySerials?.merchantCategoryCode.toString(),
                 countryCode = Constants.ISW_COUNTRY_CODE,
                 currencyCode = Constants.ISW_CURRENCY_CODE,
                 callHomeTimeInMin = Constants.ISW_CALL_HOME_TIME_IN_MIN.toIntOrNull() ?: -1,
                 serverTimeoutInSec = Constants.ISW_SERVER_TIMEOUT_IN_SEC.toIntOrNull() ?: -1,
                 isKimono = true,
                 capabilities = Constants.ISW_TERMINAL_CAPABILITIES,
                 serverIp = Constants.ISW_TERMINAL_IP,
                 serverUrl = Constants.ISW_KIMONO_BASE_URL,
                 serverPort = Constants.ISW_CTMS_PORT.toIntOrNull() ?: -1,
                 agentId = info.terminalInfoBySerials?.merchantPhoneNumber.toString(),
                 agentEmail = info.terminalInfoBySerials?.merchantEmail.toString()
         )
     }


}