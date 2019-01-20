package com.interswitchng.interswitchpossdk.shared.interfaces

import com.interswitchng.interswitchpossdk.shared.errors.DeviceError
import com.interswitchng.interswitchpossdk.shared.models.CardDetail


internal interface CardInsertedCallback {
    fun onCardDetected()
    fun onCardRead(cardDetail: CardDetail)
    fun onCardRemoved()
    fun onError(error: DeviceError)
}