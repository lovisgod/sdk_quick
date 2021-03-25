package com.interswitchng.smartpos.modules.main.billPayment.models

import com.interswitchng.smartpos.modules.main.transfer.models.BankModel

interface NetworkListCallBackListener<T> {
    fun onDataReceived(data: T)
}