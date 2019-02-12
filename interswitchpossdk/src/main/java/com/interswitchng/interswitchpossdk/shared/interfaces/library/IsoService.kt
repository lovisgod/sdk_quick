package com.interswitchng.interswitchpossdk.shared.interfaces.library

import com.interswitchng.interswitchpossdk.shared.models.TerminalInfo
import com.interswitchng.interswitchpossdk.shared.models.transaction.cardpaycode.request.TransactionInfo

internal interface IsoService {

    fun downloadKey(terminalId: String): Boolean

    fun downloadTerminalParameters(terminalId: String): Boolean

    fun initiatePurchase(terminalInfo: TerminalInfo, transaction: TransactionInfo)

}