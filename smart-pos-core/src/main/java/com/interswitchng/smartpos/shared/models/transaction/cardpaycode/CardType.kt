package com.interswitchng.smartpos.shared.models.transaction.cardpaycode

/**
 * This enum class represents the different cards
 * supported for EMV Card Chip transactions
 *
 * Note: The [code] parameter must match the AID's name tag in the xml config
 */
enum class CardType( val code: String) {
    MASTER("Master"),
    VISA("Visa"),
    VERVE("Verve"),
    AMERICANEXPRESS("AMEX"),
    CHINAUNIONPAY("CUP"),
    None("None");


    override fun toString(): String {
        return  when {
            this == MASTER -> "MasterCard"
            this == VISA -> "Visa Card"
            this == VERVE -> "Verve Card"
            this == AMERICANEXPRESS -> "American Express Card"
            this == CHINAUNIONPAY -> "China Union Pay Card"
            else -> super.toString()
        }
    }
}