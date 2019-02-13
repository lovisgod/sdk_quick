package com.interswitchng.interswitchpossdk.shared.interfaces.device

import com.interswitchng.interswitchpossdk.shared.interfaces.library.EmvCallback
import com.interswitchng.interswitchpossdk.shared.models.CardDetail
import com.interswitchng.interswitchpossdk.shared.models.TerminalInfo
import com.interswitchng.interswitchpossdk.shared.models.transaction.TransactionResult
import com.interswitchng.interswitchpossdk.shared.models.transaction.cardpaycode.request.EmvData
import com.interswitchng.interswitchpossdk.shared.models.transaction.cardpaycode.request.TransactionInfo

interface EmvCardTransaction {

    fun setEmvCallback(callback: EmvCallback)

    fun removeEmvCallback(callback: EmvCallback)

    fun setupTransaction(amount: Int, terminalInfo: TerminalInfo)

    fun startTransaction(): TransactionResult

    fun getCardDetail(): CardDetail

    fun completeTransaction()

    fun cancelTransaction()

    fun getTransactionInfo(): EmvData?

}