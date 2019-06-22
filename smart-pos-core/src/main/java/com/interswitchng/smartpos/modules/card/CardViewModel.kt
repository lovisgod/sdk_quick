package com.interswitchng.smartpos.modules.card

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import com.gojuno.koptional.None
import com.gojuno.koptional.Optional
import com.gojuno.koptional.Some
import com.interswitchng.smartpos.shared.interfaces.device.POSDevice
import com.interswitchng.smartpos.shared.interfaces.library.IsoService
import com.interswitchng.smartpos.shared.models.core.TerminalInfo
import com.interswitchng.smartpos.shared.models.transaction.PaymentInfo
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.EmvMessage
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.EmvResult
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.request.AccountType
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.request.EmvData
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.request.PurchaseType
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.request.TransactionInfo
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.response.TransactionResponse
import com.interswitchng.smartpos.shared.services.iso8583.utils.IsoUtils
import com.interswitchng.smartpos.shared.utilities.toast
import com.interswitchng.smartpos.shared.viewmodel.RootViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel

internal class CardViewModel(private val posDevice: POSDevice, private val isoService: IsoService) : RootViewModel() {

    // communication channel with cardreader
    private val channel = Channel<EmvMessage>()

    private val _emvMessage = MutableLiveData<EmvMessage>()
    val emvMessage: LiveData<EmvMessage> = _emvMessage

    private val _onlineResult = MutableLiveData<OnlineProcessResult>()
    val onlineResult: LiveData<OnlineProcessResult> get() = _onlineResult

    private val _transactionResponse = MutableLiveData<Optional<Pair<TransactionResponse, EmvData>>>()
    val transactionResponse: LiveData<Optional<Pair<TransactionResponse, EmvData>>> get() = _transactionResponse

    private val emv by lazy { posDevice.getEmvCardReader() }

    fun setupTransaction(amount: Int, terminalInfo: TerminalInfo) {

        with(uiScope) {
            // launch job in IO thread to listen for messages
            launch(ioScope) {
                // listen and  publish all received messages
                for (message in channel)
                    _emvMessage.postValue(message)
            }

            // trigger transaction setup in IO thread
            launch(ioScope) {
                // setup transaction
                emv.setupTransaction(amount, terminalInfo, channel, uiScope)
            }
        }
    }


    fun startTransaction(context: Context, paymentInfo: PaymentInfo, accountType: AccountType, terminalInfo: TerminalInfo) {
        uiScope.launch {
            //  start card transaction in IO thread
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
                    // trigger transaction cancel
                    _emvMessage.value = EmvMessage.TransactionCancelled(-1, "Unable to process card transaction")
                }
            }
        }
    }


    private fun processOnline(paymentInfo: PaymentInfo, accountType: AccountType, terminalInfo: TerminalInfo): Optional<Pair<TransactionResponse, EmvData>> {

        // get emv data captured by card
        val emvData = emv.getTransactionInfo()

        // return response based on data
        if (emvData != null) {
            // create transaction info and issue online purchase request
            val txnInfo = TransactionInfo.fromEmv(emvData, paymentInfo, PurchaseType.Card, accountType)
            val response = isoService.initiateCardPurchase(terminalInfo, txnInfo)


            when (response) {
                null ->  {
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
    }

    override fun onCleared() {
        super.onCleared()

        // cancel any ongoing transaction
        emv.cancelTransaction()
    }


    enum class OnlineProcessResult {
        NO_EMV,
        NO_RESPONSE,
        ONLINE_DENIED,
        ONLINE_APPROVED
    }

}