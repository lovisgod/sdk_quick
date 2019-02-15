package com.interswitchng.interswitchpossdk.mockservices

import com.interswitchng.interswitchpossdk.shared.interfaces.device.EmvCardTransaction
import com.interswitchng.interswitchpossdk.shared.interfaces.library.EmvCallback
import com.interswitchng.interswitchpossdk.shared.models.CardDetail
import com.interswitchng.interswitchpossdk.shared.models.TerminalInfo
import com.interswitchng.interswitchpossdk.shared.models.transaction.EmvResult

class EmvTransactionImpl: EmvCardTransaction {
    override fun setEmvCallback(callback: EmvCallback) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun removeEmvCallback(callback: EmvCallback) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setupTransaction(amount: Int, terminalInfo: TerminalInfo) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun startTransaction(): EmvResult {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getCardDetail(): CardDetail {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun completeTransaction() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun cancelTransaction() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}