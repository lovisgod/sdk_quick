package com.interswitchng.smartpos.modules.main.billPayment.models

import com.interswitchng.smartpos.modules.main.transfer.models.BankModel

interface NetworkListCallBackListener<T, M> {
    fun onDataReceived(data: T, extra: M?)
}