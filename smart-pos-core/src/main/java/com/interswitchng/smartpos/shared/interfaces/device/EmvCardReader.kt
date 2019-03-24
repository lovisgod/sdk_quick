package com.interswitchng.smartpos.shared.interfaces.device

import com.interswitchng.smartpos.shared.interfaces.library.EmvCallback
import com.interswitchng.smartpos.shared.models.core.TerminalInfo
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.CardDetail
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.EmvResult
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.request.EmvData
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.response.TransactionResponse

interface EmvCardReader {

    fun setEmvCallback(callback: EmvCallback)

    fun removeEmvCallback(callback: EmvCallback)

    fun setupTransaction(amount: Int, terminalInfo: TerminalInfo)

    fun startTransaction(): EmvResult

    fun completeTransaction(response: TransactionResponse): EmvResult

    fun cancelTransaction()

    fun getTransactionInfo(): EmvData?

}