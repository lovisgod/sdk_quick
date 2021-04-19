package com.interswitchng.smartpos.modules.main.billPayment.models

interface DialogCallBackListener<T>  {
    fun onDialogDataReceived(data: T)
}