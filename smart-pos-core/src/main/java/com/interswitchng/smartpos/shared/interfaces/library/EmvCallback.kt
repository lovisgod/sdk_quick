package com.interswitchng.smartpos.shared.interfaces.library

import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.CardType


interface EmvCallback {

    fun showInsertCard()

    fun onCardDetected()

    fun onCardRead(cardType: CardType)

    fun onCardRemoved()

    fun showEnterPin()

    fun setPinText(text: String)

    fun showPinOk()

    fun onTransactionCancelled(code: Int, reason: String)

    fun showPinError(remainCount: Int)
}