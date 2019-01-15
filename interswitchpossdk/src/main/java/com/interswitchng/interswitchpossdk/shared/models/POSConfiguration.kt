package com.interswitchng.interswitchpossdk.shared.models

data class POSConfiguration(
            internal val alias: String,
            internal val terminalId: String,
            internal val merchantId: String,
            internal val terminalType: String,
            internal val uniqueId: String,
            internal val merchantLocation: String
)
