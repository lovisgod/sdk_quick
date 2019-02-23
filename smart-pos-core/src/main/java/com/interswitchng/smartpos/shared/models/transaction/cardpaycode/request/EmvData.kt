package com.interswitchng.smartpos.shared.models.transaction.cardpaycode.request

data class EmvData(
    val cardExpiry: String,
    val cardPIN: String,
    val cardPAN: String,
    val cardTrack2: String,
    val src: String, // service restriction code
    val AID: String,
    val icc: String)