package com.interswitchng.smartpos.shared.models.transaction.cardpaycode.request

internal enum class PurchaseType {
    PayCode,
    Card
}

internal enum class AccountType(val value: String) {
    Default("00"),
    Savings("10"),
    Current("20"),
    Credit("30")
}