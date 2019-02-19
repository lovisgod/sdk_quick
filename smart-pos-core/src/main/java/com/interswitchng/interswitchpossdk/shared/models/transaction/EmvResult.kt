package com.interswitchng.interswitchpossdk.shared.models.transaction

enum class EmvResult(val code: Int) {
    OFFLINE_DENIED(0), // AC_AAC = 0
    OFFLINE_APPROVED(1), // AC_TC = 1
    ONLINE_REQUIRED(2) // AC_ARQC = 2
}