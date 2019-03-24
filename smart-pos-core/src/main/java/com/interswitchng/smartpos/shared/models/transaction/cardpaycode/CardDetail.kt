package com.interswitchng.smartpos.shared.models.transaction.cardpaycode

data class CardDetail(val pan: String, val expiry: String, val type: CardType)