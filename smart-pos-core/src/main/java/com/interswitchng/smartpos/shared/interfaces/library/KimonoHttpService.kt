package com.interswitchng.smartpos.shared.interfaces.library

import com.gojuno.koptional.Optional
import com.interswitchng.smartpos.shared.models.core.TerminalInfo
import com.interswitchng.smartpos.shared.models.kimono.request.CallHomeModel
import com.interswitchng.smartpos.shared.models.transaction.PaymentInfo
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.request.TransactionInfo
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.response.TransactionResponse

internal interface KimonoHttpService {

    suspend fun callHome(data: CallHomeModel): Optional<String>

    fun initiatePaycodePurchase(terminalInfo: TerminalInfo, code: String, paymentInfo: PaymentInfo): TransactionResponse?
    fun initiateCardPurchase(terminalInfo: TerminalInfo, transaction: TransactionInfo): TransactionResponse?

    fun initiatePreAuthorization(
            terminalInfo: TerminalInfo,
            transaction: TransactionInfo
    ): TransactionResponse?

    fun initiateCompletion(terminalInfo: TerminalInfo, transaction: TransactionInfo): TransactionResponse?

}