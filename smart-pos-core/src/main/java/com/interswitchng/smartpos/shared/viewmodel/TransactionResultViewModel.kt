package com.interswitchng.smartpos.shared.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.interswitchng.smartpos.shared.interfaces.device.POSDevice
import com.interswitchng.smartpos.shared.interfaces.library.EmailService
import com.interswitchng.smartpos.shared.interfaces.library.IsoService
import com.interswitchng.smartpos.shared.interfaces.library.TransactionLogService
import com.interswitchng.smartpos.shared.models.core.TerminalInfo
import com.interswitchng.smartpos.shared.models.core.UserType
import com.interswitchng.smartpos.shared.models.email.CustomArguments
import com.interswitchng.smartpos.shared.models.email.Email
import com.interswitchng.smartpos.shared.models.email.EmailMessage
import com.interswitchng.smartpos.shared.models.email.Personalization
import com.interswitchng.smartpos.shared.models.posconfig.PrintObject
import com.interswitchng.smartpos.shared.models.posconfig.PrintStringConfiguration
import com.interswitchng.smartpos.shared.models.printer.info.PrintStatus
import com.interswitchng.smartpos.shared.models.printer.slips.TransactionSlip
import com.interswitchng.smartpos.shared.models.transaction.TransactionLog
import com.interswitchng.smartpos.shared.models.transaction.TransactionResult
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.request.TransactionInfo
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.response.TransactionResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class TransactionResultViewModel(private val posDevice: POSDevice,
                                          private val emailService: EmailService,
                                          private val transactionLogService: TransactionLogService,
                                          private val isoService: IsoService) : RootViewModel() {



    private val _printButton = MutableLiveData<Boolean>()
    val printButton: LiveData<Boolean> get() = _printButton

    private val _printerMessage = MutableLiveData<String>()
    val printerMessage: LiveData<String> get() = _printerMessage

    private val _printedCustomerCopy = MutableLiveData<Boolean>()
    val printedCustomerCopy: LiveData<Boolean> get() = _printedCustomerCopy

    private val _printedMerchantCopy = MutableLiveData<Boolean>()
    val printedMerchantCopy: LiveData<Boolean> get() = _printedMerchantCopy

    private val _emailStatus = MutableLiveData<Boolean>()
    val emailStatus: LiveData<Boolean> = _emailStatus

    private val _emailDialog = MutableLiveData<Boolean>()
    val emailDialog: LiveData<Boolean> = _emailDialog

    private val _transactionResponse = MutableLiveData<TransactionResponse>()
    val transactionResponse: LiveData<TransactionResponse> get() = _transactionResponse


    private val emv by lazy { posDevice.getEmvCardReader() }

    fun sendMail(email: String, result: TransactionResult, terminalInfo: TerminalInfo) {

        val personlizedMsg = Personalization(
                to = listOf(Email(email)),
                templateData = CustomArguments(
                        terminalId = terminalInfo.terminalId,
                        merchantName = terminalInfo.merchantNameAndLocation,
                        amount = result.amount,
                        stan = result.stan,
                        date = result.dateTime,
                        paymentType = result.paymentType.toString(),
                        responseCode = result.responseCode,
                        responseMessage = result.responseMessage,
                        aid = result.AID,
                        telephone = result.telephone
                )
        )

        // create email message to be sent
        val message = EmailMessage(from = Email(email), personalizations = listOf(personlizedMsg))


        uiScope.launch {
            // show dialog
            _emailDialog.value = true
            val isSent = withContext(ioScope) { emailService.send(message) }
            _emailStatus.value = isSent
            // hide dialog
            _emailDialog.value = false
        }

    }


    fun printSlip(user: UserType, slip: TransactionSlip, reprint: Boolean = false) {

        uiScope.launch {
            // get printer status on IO thread
            val printStatus = withContext(ioScope) {
                posDevice.printer.canPrint()
            }

            when (printStatus) {
                is PrintStatus.Error -> {
                    _printerMessage.value = printStatus.message
                }
                else -> {
                    // disable print button
                    _printButton.value = false

                    // get items to print
                    val slipItems = slip.getSlipItems(reprint)

                    // print code in IO thread
                    val status = withContext(ioScope) { posDevice.printer.printSlip(slipItems, user) }
                    // publish print message
                    _printerMessage.value = status.message
                    // enable print button
                    _printButton.value = true

                    // set printed flags for customer and merchant copies
                    val printed = status !is PrintStatus.Error
                    _printedCustomerCopy.value = printed && user == UserType.Customer
                    _printedMerchantCopy.value = printed && user == UserType.Merchant
                }
            }
        }
    }


    fun logTransaction(result: TransactionResult) {
        // get and log transaction to storage
        val resultLog = TransactionLog.fromResult(result)
        transactionLogService.logTransactionResult(resultLog)
    }

    fun updateTransaction(result: TransactionResult) {
        // get and log transaction to storage
        val resultLog = TransactionLog.fromResult(result)
        transactionLogService.updateTransactionResult(resultLog)
    }

    fun initiateReversal(terminalInfo: TerminalInfo, transactionInfo: TransactionInfo) {
        println("Called me the chairman of reversal")
        logger.log("Called reversal inside vm")
        CoroutineScope(ioScope).launch {
            logger.log("Called reversal inside vm ioscope")
            val response = isoService.initiateReversal(terminalInfo, transactionInfo)
            _transactionResponse.postValue(response)
            logger.log(response?.responseCode!!)
            println("Called reversal response code --->${response.responseCode}")
        }
    }
}