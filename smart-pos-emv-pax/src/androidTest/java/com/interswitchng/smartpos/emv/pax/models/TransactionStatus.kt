package com.igweze.ebi.paxemvcontact.models

data class TransactionStatus(
        val responseMessage: String,
        val responseCode: String,
        val AID: String,
        val telephone: String
)