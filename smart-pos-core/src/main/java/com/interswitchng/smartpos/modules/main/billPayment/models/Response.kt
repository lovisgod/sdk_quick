package com.interswitchng.smartpos.modules.main.billPayment.models

data class Response(
    var code: String,
    val message: String,
    var refNum: String? = ""
)