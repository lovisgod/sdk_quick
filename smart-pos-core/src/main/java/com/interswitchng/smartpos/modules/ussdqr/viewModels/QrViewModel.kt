package com.interswitchng.smartpos.modules.ussdqr.viewModels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class QrViewModel(paymentService: HttpService): BaseViewModel(paymentService) {


    private val _qrCode = MutableLiveData<Optional<CodeResponse>>()
    val qrCode: LiveData<Optional<CodeResponse>> get() = _qrCode

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
}