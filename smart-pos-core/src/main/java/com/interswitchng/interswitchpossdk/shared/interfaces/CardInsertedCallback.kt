package com.interswitchng.interswitchpossdk.shared.interfaces

import com.interswitchng.interswitchpossdk.shared.errors.DeviceError
import com.interswitchng.interswitchpossdk.shared.models.transaction.cardpaycode.CardDetail


interface CardInsertedCallback {
    fun onCardRead(cardDetail: CardDetail)
    fun onCardDetected()
    fun onCardRemoved()
    fun onError(error: DeviceError)
}