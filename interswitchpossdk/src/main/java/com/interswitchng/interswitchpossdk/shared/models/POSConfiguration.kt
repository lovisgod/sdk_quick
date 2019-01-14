package com.interswitchng.interswitchpossdk.shared.models

data class POSConfiguration(
            private val alias: String,
            private val terminalId: String,
            private val merchantId: String,
            private val terminalType: String,
            private val uniqueId: String,
            private val merchantLocation: String
)
