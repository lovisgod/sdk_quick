package com.interswitchng.smartpos.modules.main.transfer.models

interface CallbackListener {
    fun onDataReceived(data: BankModel)
}

