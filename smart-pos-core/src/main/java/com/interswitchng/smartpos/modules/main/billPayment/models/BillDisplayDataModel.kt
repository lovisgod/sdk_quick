package com.interswitchng.smartpos.modules.main.billPayment.models

data class BillDisplayDataModel(
        val title: String,
        val subtitle: String,
        var billerCode: String? = ""
)
