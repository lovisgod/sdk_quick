package com.interswitchng.smartpos.modules.main.transfer.models

data class BankModel(
    val recvInstId: String,
    val selBankCodes: String?,
    val bankName: String
)
