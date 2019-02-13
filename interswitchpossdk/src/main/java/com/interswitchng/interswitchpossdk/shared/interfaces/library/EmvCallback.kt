package com.interswitchng.interswitchpossdk.shared.interfaces.library


interface EmvCallback {

    fun showInsertCard()

    fun onCardDetected()

    fun onCardRemoved()

    fun showEnterPin()

    fun setPinText(text: String)

    fun showPinOk()

    fun onTransactionCancelled(code: Int, reason: String)

}