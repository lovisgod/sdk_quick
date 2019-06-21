package com.interswitchng.smartpos.modules.ussdqr.viewModels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.content.Context
import com.interswitchng.smartpos.shared.interfaces.device.POSDevice
import com.interswitchng.smartpos.shared.interfaces.library.HttpService
import com.interswitchng.smartpos.shared.models.core.UserType
import com.interswitchng.smartpos.shared.models.posconfig.PrintObject
import com.interswitchng.smartpos.shared.models.transaction.PaymentType
import com.interswitchng.smartpos.shared.models.transaction.ussdqr.request.TransactionStatus
import com.interswitchng.smartpos.shared.models.transaction.ussdqr.response.PaymentStatus
import com.interswitchng.smartpos.shared.utilities.toast
import com.interswitchng.smartpos.shared.viewmodel.RootViewModel
import kotlinx.coroutines.*

internal abstract class BaseViewModel(protected val paymentService: HttpService) : RootViewModel() {

    private val _paymentStatus = MutableLiveData<PaymentStatus>()
    val paymentStatus: LiveData<PaymentStatus> = _paymentStatus

    private val _printButton = MutableLiveData<Boolean>()
    val printButton: LiveData<Boolean> get() = _printButton

    val showProgress = MutableLiveData<Boolean>()
    private var pollingJob: Job? = null


    fun pollTransactionStatus(status: TransactionStatus) {

        // poll for status on IO thread
        pollingJob = uiScope.launch(ioScope) {
            // continuously repeat polling process
            repeat(10) {

                // start polling for payment status
                poll(status)

                // delay for 5 seconds before next polling process
                // if its not the last iteration
                if (it < 9) delay(5_000)
            }

            pollingJob?.cancel()
        }
    }

    fun checkTransactionStatus(status: TransactionStatus) {
        pollingJob = uiScope.launch (ioScope) {
            poll(status)
        }
    }

    private suspend fun poll(status: TransactionStatus) {

        // get the type of payment
        val type = when (this) {
            is QrViewModel -> PaymentType.QR
            else -> PaymentType.USSD
        }

        // show progress indicator
        showProgress.postValue(true)

        // poll 3 times before timeout
        repeat(3) {

            // get payment status
            val paymentStatus = paymentService.checkPayment(type, status)

            // publish status
            _paymentStatus.postValue(paymentStatus)

            // wait for 3 seconds
            delay(3_000)

            // trigger timeout if its third iteration starting from 0
            if (it == 2) _paymentStatus.postValue(PaymentStatus.Timeout)
        }
    }


    fun printCode(context: Context, posDevice: POSDevice, user: UserType, slip: List<PrintObject>) {
        uiScope.launch {
            // get printer status on IO thread
            val printStatus = withContext(ioScope) { posDevice.printer.canPrint() }

            when (printStatus) {
                is Error -> context.toast(printStatus.message)
                else -> {
                    // disable print button
                    _printButton.value = false
                    // print code in IO thread
                    val status = withContext(ioScope) { posDevice.printer.printSlip(slip, user) }
                    // toast print message
                    context.toast(status.message)
                    // enable print button
                    _printButton.value = true
                }
            }
        }
    }

    fun cancelPoll() {
        pollingJob?.cancel()
    }
}