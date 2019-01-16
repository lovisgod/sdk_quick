package com.interswitchng.interswitchpossdk.shared.models.request

data class TransactionStatus(
        internal val type: String,
        internal val reference: String,
        internal val merchantCode: String
)