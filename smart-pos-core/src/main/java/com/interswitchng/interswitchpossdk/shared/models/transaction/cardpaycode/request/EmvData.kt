package com.interswitchng.interswitchpossdk.shared.models.transaction.cardpaycode.request

data class EmvData(
    val cardExpiry: String,
    val cardPIN: String,
    val cardPAN: String,
    val cardTrack2: String,
    val AID: String,
    val icc: String)