package com.interswitchng.smartpos.modules.callhome

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.gojuno.koptional.None
import com.gojuno.koptional.Optional
import com.gojuno.koptional.Some
import com.interswitchng.smartpos.shared.interfaces.library.IsoService
import com.interswitchng.smartpos.shared.services.kimono.models.CallHomeModel
import com.interswitchng.smartpos.shared.viewmodel.RootViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


//
//internal class CallHomeViewModel(private val kimonoService: IsoService): RootViewModel()
//{
//
//
//    private val _callHomeResponse = MutableLiveData<Optional<String>>()
//    val callHomeResponse: LiveData<Optional<String>> get() = _callHomeResponse
//    fun callHome(request: CallHomeModel, context: Context) {
//        // retrieve the qr code
//        uiScope.launch {
//
//            val result = withContext(ioScope) {
//                val response = kimonoService.callHome(request)
//
//                return@withContext when (response) {
//                    null -> None
//                    else -> {
//
//
//                        // extract result
//                        Some(response)
//                    }
//                }
//            }
//
//
//            _callHomeResponse.value = result.component1()
//        }
//    }
//}