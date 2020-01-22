package com.interswitchng.smartpos.shared.models.transaction.cardpaycode.request

/**
 * This enum type identifies the
 * type of transactions issued out
 * to the EPMS through ISO message format
 */
internal enum class PurchaseType {
    PayCode,
    Card
}


/**
 * This enum type identifies the
 * different Bank Account types
 */
enum class AccountType(val value: String) {
    Default("00"),
    Savings("10"),
    Current("20"),
    Credit("30")
}