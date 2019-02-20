package com.interswitchng.smartpos.shared.interfaces.library

import com.interswitchng.smartpos.shared.models.transaction.PaymentInfo
import com.interswitchng.smartpos.shared.models.TerminalInfo
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.request.TransactionInfo
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.response.TransactionResponse

internal interface IsoService {

    fun downloadKey(terminalId: String): Boolean

    fun downloadTerminalParameters(terminalId: String): Boolean

    fun initiateCardPurchase(terminalInfo: TerminalInfo, transaction: TransactionInfo): TransactionResponse?

    fun initiatePaycodePurchase(terminalInfo: TerminalInfo, code: String, paymentInfo: PaymentInfo): TransactionResponse?

}