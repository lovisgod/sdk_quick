package com.interswitchng.smartpos.shared.interfaces.device

import com.interswitchng.smartpos.shared.interfaces.library.EmvCallback
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.CardDetail
import com.interswitchng.smartpos.shared.models.TerminalInfo
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.EmvResult
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.request.EmvData

interface EmvCardTransaction {

    fun setEmvCallback(callback: EmvCallback)

    fun removeEmvCallback(callback: EmvCallback)

    fun setupTransaction(amount: Int, terminalInfo: TerminalInfo)

    fun startTransaction(): EmvResult

    fun getCardDetail(): CardDetail

    fun completeTransaction()

    fun cancelTransaction()

    fun getTransactionInfo(): EmvData?

}