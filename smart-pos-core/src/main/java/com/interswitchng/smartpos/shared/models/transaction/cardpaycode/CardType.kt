package com.interswitchng.smartpos.shared.models.transaction.cardpaycode

/**
 * This enum class represents the different cards
 * supported for EMV Card Chip transactions
 */
enum class CardType {
    MASTER,
    VISA,
    VERVE,
    AMERICANEXPRESS,
    None,
}