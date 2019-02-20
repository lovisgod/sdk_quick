package com.interswitchng.smartpos.shared.models.transaction.ussdqr.request

data class TransactionStatus(
        internal val reference: String,
        internal val merchantCode: String
)