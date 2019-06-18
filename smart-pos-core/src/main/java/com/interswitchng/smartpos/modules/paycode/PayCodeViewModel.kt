package com.interswitchng.smartpos.modules.paycode

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.gojuno.koptional.None
import com.gojuno.koptional.Optional
import com.gojuno.koptional.Some
import com.interswitchng.smartpos.IswPos
import com.interswitchng.smartpos.shared.interfaces.library.IsoService
import com.interswitchng.smartpos.shared.models.core.TerminalInfo
import com.interswitchng.smartpos.shared.models.printer.info.TransactionType
import com.interswitchng.smartpos.shared.models.transaction.PaymentInfo
import com.interswitchng.smartpos.shared.models.transaction.PaymentType
import com.interswitchng.smartpos.shared.models.transaction.TransactionResult
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.CardType
import com.interswitchng.smartpos.shared.services.iso8583.utils.IsoUtils
import com.interswitchng.smartpos.shared.utilities.DisplayUtils
import kotlinx.coroutines.*
import java.util.*

internal class PayCodeViewModel(private val isoService: IsoService, private val iswPos: IswPos) : ViewModel() {

    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)
    private val ioScope = uiScope.coroutineContext + Dispatchers.IO


    private val _transactionResult = MutableLiveData<Optional<TransactionResult>>()
    val transactionResult: LiveData<Optional<TransactionResult>> get() = _transactionResult


    fun processOnline(terminalInfo: TerminalInfo, code: String, paymentInfo: PaymentInfo) {
        uiScope.launch {

            val result = withContext(ioScope) {
                val response = isoService.initiatePaycodePurchase(terminalInfo, code, paymentInfo)

                return@withContext when (response) {
                    null -> None
                    else -> {

                        val now = Date()
                        val responseMsg = IsoUtils.getIsoResultMsg(response.responseCode)
                                ?: "Unknown Error"

                        // extract result
                        Some(TransactionResult(
                                paymentType = PaymentType.PayCode,
                                dateTime = DisplayUtils.getIsoString(now),
                                amount = DisplayUtils.getAmountString(paymentInfo),
                                type = TransactionType.Purchase,
                                authorizationCode = response.authCode,
                                responseMessage = responseMsg,
                                responseCode = response.responseCode,
                                stan = response.stan, pinStatus = "", AID = "", code = "",
                                cardPan = "", cardExpiry = "", cardType = CardType.None,
                                telephone = iswPos.config.merchantTelephone))
                    }
                }
            }


            _transactionResult.value = result
        }


    }

}