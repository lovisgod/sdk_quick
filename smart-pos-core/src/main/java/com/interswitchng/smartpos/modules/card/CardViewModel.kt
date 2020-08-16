package com.interswitchng.smartpos.modules.card

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.gojuno.koptional.None
import com.gojuno.koptional.Optional
import com.gojuno.koptional.Some
import com.interswitchng.smartpos.modules.main.models.PaymentModel
import com.interswitchng.smartpos.modules.main.models.PaymentModel.TransactionType
import com.interswitchng.smartpos.shared.interfaces.device.POSDevice
import com.interswitchng.smartpos.shared.interfaces.library.IsoService
import com.interswitchng.smartpos.shared.models.core.TerminalInfo
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.EmvMessage
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.EmvResult
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.request.*
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.response.TransactionResponse
import com.interswitchng.smartpos.shared.services.iso8583.utils.IsoUtils
import com.interswitchng.smartpos.shared.utilities.toast
import com.interswitchng.smartpos.shared.viewmodel.RootViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class CardViewModel(private val posDevice: POSDevice, private val isoService: IsoService) :
        RootViewModel() {

    // communication channel with cardreader
    private val channel = Channel<EmvMessage>()

    // card removed flag
    private var cardRemoved = false

    private val _emvMessage = MutableLiveData<EmvMessage>()
    val emvMessage: LiveData<EmvMessage> = _emvMessage

    private val _onlineResult = MutableLiveData<OnlineProcessResult>()
    val onlineResult: LiveData<OnlineProcessResult> get() = _onlineResult

    private val _transactionResponse =
            MutableLiveData<Optional<Pair<TransactionResponse, EmvData>>>()
    val transactionResponse: LiveData<Optional<Pair<TransactionResponse, EmvData>>> get() = _transactionResponse

    private val emv by lazy { posDevice.getEmvCardReader() }

    private var transactionType: TransactionType = TransactionType.CARD_PURCHASE

    private lateinit var originalTxnData: OriginalTransactionInfoData

    fun getCardPAN() = emv.getPan()


    fun setupTransaction(amount: Int, terminalInfo: TerminalInfo) {

        with(uiScope) {
            // launch job in IO thread to listen for messages
            launch(ioScope) {
                // listen and  publish all received messages
                for (message in channel) {
                    cardRemoved = cardRemoved || message is EmvMessage.CardRemoved
                    _emvMessage.postValue(message)
                }
            }

            // trigger transaction setup in IO thread
            launch(ioScope) {
                // setup transaction
                emv.setupTransaction(amount, terminalInfo, channel, uiScope)
            }
        }
    }

    fun readCard() {
        emv.startTransaction()
    }

    fun startTransaction(
            context: Context
    ) {
        uiScope.launch {
            //  start card transaction in IO thread
            // paymentInfo.amount=paymentInfo.amount*100;
            val result = withContext(ioScope) { emv.startTransaction() }

            when (result) {
                EmvResult.ONLINE_REQUIRED -> {
                    // set message as transaction processing
                    _emvMessage.value = EmvMessage.ProcessingTransaction
                    // trigger online transaction process in IO thread
//                    val response = withContext(ioScope) { processOnline(paymentInfo, accountType, terminalInfo) }
//                    // publish transaction response
//                    _transactionResponse.value = response
                }

                EmvResult.CANCELLED -> {
                    // transaction has already been cancelled
                    context.toast("Transaction was cancelled")
                }
                else -> {
                    context.toast("Error processing card transaction")

                    // show cancelled transaction if its not
                    // already triggered by card removal
                    if (!cardRemoved) {
                        // trigger transaction cancel
                        val reason = "Unable to process card transaction"
                        _emvMessage.value = EmvMessage.TransactionCancelled(-1, reason)
                    }
                }
            }
        }
    }

    /* fun startTransaction(context: Context, paymentInfo: PaymentInfo, accountType: AccountType, terminalInfo: TerminalInfo) {
         uiScope.launch {
             //  start card transaction in IO thread
             // paymentInfo.amount=paymentInfo.amount*100;
             val result = withContext(ioScope) { emv.startTransaction() }

             when (result) {
                 EmvResult.ONLINE_REQUIRED -> {
                     // set message as transaction processing
                     _emvMessage.value = EmvMessage.ProcessingTransaction
                     // trigger online transaction process in IO thread
                     val response = withContext(ioScope) { processOnline(paymentInfo, accountType, terminalInfo) }
                     // publish transaction response
                     _transactionResponse.value = response
                 }

                 EmvResult.CANCELLED -> {
                     // transaction has already been cancelled
                     context.toast("Transaction was cancelled")
                 }
                 else -> {
                     context.toast("Error processing card transaction")

                     // show cancelled transaction if its not
                     // already triggered by card removal
                     if (!cardRemoved) {
                         // trigger transaction cancel
                         val reason = "Unable to process card transaction"
                         _emvMessage.value = EmvMessage.TransactionCancelled(-1, reason)
                     }
                 }
             }
         }
     }

     fun startTransaction(context: Context, paymentInfo: PaymentInfo, accountType: AccountType, terminalInfo: TerminalInfo, billPaymentModel: BillPaymentModel) {
         uiScope.launch {
             //  start card transaction in IO thread
             // paymentInfo.amount=paymentInfo.amount*100;
             val result = withContext(ioScope) { emv.startTransaction() }

             when (result) {
                 EmvResult.ONLINE_REQUIRED -> {
                     // set message as transaction processing
                     _emvMessage.value = EmvMessage.ProcessingTransaction
                     // trigger online transaction process in IO thread
                     val response = withContext(ioScope) { processOnlineBP(paymentInfo, accountType, terminalInfo, billPaymentModel) }
                     // publish transaction response
                     _transactionResponse.value = response
                 }

                 EmvResult.CANCELLED -> {
                     // transaction has already been cancelled
                     context.toast("Transaction was cancelled")
                 }
                 else -> {
                     context.toast("Error processing card transaction")

                     // show cancelled transaction if its not
                     // already triggered by card removal
                     if (!cardRemoved) {
                         // trigger transaction cancel
                         val reason = "Unable to process card transaction"
                         _emvMessage.value = EmvMessage.TransactionCancelled(-1, reason)
                     }
                 }
             }
         }
     }
 */


    fun processOnline(
            paymentModel: PaymentModel,
            accountType: AccountType,
            terminalInfo: TerminalInfo
    ) {
        uiScope.launch {
            // get emv data captured by card
            val emvData = emv.getTransactionInfo()

            // return response based on data
            if (emvData != null) {
                // create transaction info and issue online purchase request
                val response = withContext(ioScope) {
                    val txnInfo =
                            TransactionInfo.fromEmv(
                                    emvData,
                                    paymentModel,
                                    PurchaseType.Card,
                                    accountType
                            )
                    initiateTransaction(paymentModel.type!!, terminalInfo, txnInfo)
                }

                when (response) {
                    null -> {
                        _onlineResult.value = OnlineProcessResult.NO_RESPONSE
                        _transactionResponse.value = None
                    }
                    else -> {
                        // complete transaction by applying scripts
                        // only when responseCode is 'OK'
                        if (response.responseCode == IsoUtils.OK) {
                            // get result code of applying server response

                            // react to result code
                            when (emv.completeTransaction(response)) {
                                EmvResult.OFFLINE_APPROVED -> _onlineResult.postValue(
                                        OnlineProcessResult.ONLINE_APPROVED
                                )
                                else -> _onlineResult.value = OnlineProcessResult.ONLINE_DENIED
                            }
                        }

                        _transactionResponse.value = Some(Pair(response, emvData))
                    }
                }
            } else {
                _onlineResult.postValue(OnlineProcessResult.NO_EMV)
                _transactionResponse.value = None
            }
        }
    }


    fun processOnlineBP(
            paymentModel: PaymentModel,
            accountType: AccountType,
            terminalInfo: TerminalInfo
    ) {

        //var responseProcessed: Optional<Pair<TransactionResponse, EmvData>> = None

        uiScope.launch {
            // get emv data captured by card
            val emvData = emv.getTransactionInfo()

            // return response based on data
            if (emvData != null) {
                // create transaction info and issue online purchase request
                val response = withContext(ioScope) {
                    val txnInfo =
                            TransactionInfo.fromEmv(
                                    emvData,
                                    paymentModel,
                                    PurchaseType.Card,
                                    accountType
                            )
                    initiateTransactionBillPayment(transactionType, terminalInfo, txnInfo)
                }

                when (response) {
                    null -> {
                        _onlineResult.value = OnlineProcessResult.NO_RESPONSE
                        _transactionResponse.value = None
                    }
                    else -> {
                        // complete transaction by applying scripts
                        // only when responseCode is 'OK'
                        if (response.responseCode == IsoUtils.OK) {
                            // get result code of applying server response

                            // react to result code
                            when (emv.completeTransaction(response)) {
                                EmvResult.OFFLINE_APPROVED -> _onlineResult.postValue(
                                        OnlineProcessResult.ONLINE_APPROVED
                                )
                                else -> _onlineResult.value = OnlineProcessResult.ONLINE_DENIED
                            }
                        }

                        _transactionResponse.value = Some(Pair(response, emvData))
                    }
                }
            } else {
                _onlineResult.postValue(OnlineProcessResult.NO_EMV)
                _transactionResponse.value = None
            }
        }
    }

    /*fun processOnlineBP(paymentInfo: PaymentInfo, accountType: AccountType, terminalInfo: TerminalInfo, billPaymentModel: BillPaymentModel): Optional<Pair<TransactionResponse, EmvData>> {

        // get emv data captured by card
        val emvData = emv.getTransactionInfo()


        // return response based on data
        if (emvData != null) {
            // create transaction info and issue online purchase request
            val txnInfo = TransactionInfo.fromEmv(emvData, paymentInfo, PurchaseType.Card, accountType)
            val response = initiateTransactionBillPayment(transactionType, terminalInfo, txnInfo, billPaymentModel)


            when (response) {
                null -> {
                    _onlineResult.postValue(OnlineProcessResult.NO_RESPONSE)
                    return None
                }
                else -> {
                    // complete transaction by applying scripts
                    // only when responseCode is 'OK'
                    if (response.responseCode == IsoUtils.OK) {
                        // get result code of applying server response
                        val completionResult = emv.completeTransaction(response)

                        // react to result code
                        when (completionResult) {
                            EmvResult.OFFLINE_APPROVED -> _onlineResult.postValue(OnlineProcessResult.ONLINE_APPROVED)
                            else -> _onlineResult.postValue(OnlineProcessResult.ONLINE_DENIED)
                        }
                    }

                    return Some(Pair(response, emvData))
                }
            }
        } else {
            _onlineResult.postValue(OnlineProcessResult.NO_EMV)
            return None
        }
    }*/

    fun processOnlineCNP(
            paymentModel: PaymentModel,
            accountType: AccountType,
            terminalInfo: TerminalInfo,
            expiryDate: String,
            cardPan: String
    ) {

        //var responseProcessed: Optional<Pair<TransactionResponse, EmvData>> = None

        uiScope.launch {

            val emvData = emv.getTransactionInfo() //For CardNotPresent,there is no emvData
            if (emvData != null) {//so remove this condition
                val response = withContext(ioScope) {

                    emvData.cardExpiry = expiryDate
                    emvData.cardPAN = cardPan

                    // return response based on data

                    // create transaction info and issue online purchase request
                    val txnInfo = TransactionInfo.fromEmv(
                            emvData,
                            paymentModel,
                            PurchaseType.Card,
                            accountType
                    )

                    initiateCNPTransaction(paymentModel.type!!, terminalInfo, txnInfo)
                    //Logger.with("response CardViewModel").logErr(response.toString())

                }

                when (response) {
                    null -> {
                        _onlineResult.postValue(OnlineProcessResult.NO_RESPONSE)
                        //return@withContext None
                        _transactionResponse.value = None
                    }
                    else -> {
                        //                            // complete transaction by applying scripts
                        //                            // only when responseCode is 'OK'
                        //
                        //                                // get result code of applying server response
                        val completionResult = emv.completeTransaction(response)

                        // react to result code
                        when (completionResult) {
                            EmvResult.OFFLINE_APPROVED -> _onlineResult.postValue(
                                    OnlineProcessResult.ONLINE_APPROVED
                            )
                            else -> _onlineResult.postValue(OnlineProcessResult.ONLINE_DENIED)
                        }


                        //return@withContext Some(Pair(response, emvData))
                        _transactionResponse.value = Some(Pair(response, emvData))
                    }
                }
            } else {
                _onlineResult.postValue(OnlineProcessResult.NO_EMV)
                //return None
            }
        }

    }

    // get emv data captured by card
    //return responseProcessed


    private fun initiateTransaction(
            transactionType: TransactionType,
            terminalInfo: TerminalInfo,
            txnInfo: TransactionInfo
    ): TransactionResponse? {
        return when (transactionType) {
            TransactionType.CARD_PURCHASE -> isoService.initiateCardPurchase(terminalInfo, txnInfo)
            TransactionType.PRE_AUTHORIZATION -> isoService.initiatePreAuthorization(
                    terminalInfo,
                    txnInfo
            )
            TransactionType.REFUND -> isoService.initiateRefund(terminalInfo, txnInfo)
            TransactionType.COMPLETION -> {
                if (::originalTxnData.isInitialized) {
                    txnInfo.originalTransactionInfoData = originalTxnData
                }
                isoService.initiateCompletion(terminalInfo, txnInfo)
            }
            else -> null
        }
    }

    private fun initiateTransactionBillPayment(transactionType: TransactionType, terminalInfo: TerminalInfo, txnInfo: TransactionInfo): TransactionResponse? {

        return isoService.initiateBillPayment(terminalInfo, txnInfo)
    }

    private fun initiateCNPTransaction(
            transactionType: TransactionType,
            terminalInfo: TerminalInfo,
            txnInfo: TransactionInfo
    ): TransactionResponse? {
        return when (transactionType) {
            TransactionType.CARD_PURCHASE -> isoService.initiateCNPPurchase(terminalInfo, txnInfo)
            TransactionType.PRE_AUTHORIZATION -> isoService.initiatePreAuthorization(
                    terminalInfo,
                    txnInfo
            )
            TransactionType.REFUND -> isoService.initiateRefund(terminalInfo, txnInfo)
            TransactionType.COMPLETION -> {
                txnInfo.originalTransactionInfoData = originalTxnData
                isoService.initiateCompletion(terminalInfo, txnInfo)
            }
            else -> null
        }
    }

    override fun onCleared() {
        super.onCleared()

        // cancel any ongoing transaction
        cancelTransaction()
    }

    fun cancelTransaction() {
        emv.cancelTransaction()
    }

    enum class OnlineProcessResult {
        NO_EMV,
        NO_RESPONSE,
        ONLINE_DENIED,
        ONLINE_APPROVED
    }

    fun setTransactionType(selectedTransactionType: TransactionType) {
        transactionType = selectedTransactionType
    }

    fun setOriginalTxnInfo(originalTxnInfo: OriginalTransactionInfoData) {
        this.originalTxnData = originalTxnInfo
    }


}
