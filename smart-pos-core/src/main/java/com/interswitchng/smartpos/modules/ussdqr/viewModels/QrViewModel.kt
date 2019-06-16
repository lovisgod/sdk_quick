package com.interswitchng.smartpos.modules.ussdqr.viewModels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.content.Context
import com.gojuno.koptional.Optional
import com.gojuno.koptional.Some
import com.interswitchng.smartpos.shared.interfaces.device.POSDevice
import com.interswitchng.smartpos.shared.interfaces.library.HttpService
import com.interswitchng.smartpos.shared.models.core.UserType
import com.interswitchng.smartpos.shared.models.posconfig.PrintObject
import com.interswitchng.smartpos.shared.models.transaction.ussdqr.request.CodeRequest
import com.interswitchng.smartpos.shared.models.transaction.ussdqr.response.CodeResponse
import com.interswitchng.smartpos.shared.utilities.toast
import kotlinx.coroutines.*

internal class QrViewModel(private val paymentService: HttpService): ViewModel() {

    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)
    private val ioScope = uiScope.coroutineContext + Dispatchers.IO

    private val _qrCode = MutableLiveData<Optional<CodeResponse>>()
    val qrCode: LiveData<Optional<CodeResponse>> get() = _qrCode

    private val _printButton = MutableLiveData<Boolean>()
    val printButton: LiveData<Boolean> get() = _printButton

    fun getQrCode(request: CodeRequest, context: Context) {
        // retrieve the qr code
        uiScope.launch {
            // get qr code in IO thread
            val response = uiScope.async(Dispatchers.IO) {
                // get the code
                val code = paymentService.initiateQrPayment(request)

                // if code was returned, generate the image in IO thread
                if (code is Some) code.value.setBitmap(context)

                // return value
                return@async code
            }

            // set result in main
            _qrCode.value = response.await()
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

}