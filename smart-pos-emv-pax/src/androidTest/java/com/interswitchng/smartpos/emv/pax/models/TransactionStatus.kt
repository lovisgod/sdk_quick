package com.interswitchng.smartpos.emv.pax.models

data class TransactionStatus(
        val responseMessage: String,
        val responseCode: String,
        val AID: String,
        val telephone: String
)