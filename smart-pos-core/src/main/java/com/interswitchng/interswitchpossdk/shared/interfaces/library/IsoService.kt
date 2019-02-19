package com.interswitchng.interswitchpossdk.shared.interfaces.library

import com.interswitchng.interswitchpossdk.shared.models.TerminalInfo
import com.interswitchng.interswitchpossdk.shared.models.transaction.cardpaycode.request.TransactionInfo
import com.interswitchng.interswitchpossdk.shared.models.transaction.cardpaycode.response.TransactionResponse

internal interface IsoService {

    fun downloadKey(terminalId: String): Boolean

    fun downloadTerminalParameters(terminalId: String): Boolean

    fun initiateCardPurchase(terminalInfo: TerminalInfo, transaction: TransactionInfo): TransactionResponse?

    fun initiatePaycodePurchase(terminalInfo: TerminalInfo, code: String, amount: Int): TransactionResponse?

}