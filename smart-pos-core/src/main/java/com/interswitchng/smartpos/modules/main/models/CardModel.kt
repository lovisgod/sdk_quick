package com.interswitchng.smartpos.modules.main.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CardModel (
    var cardPan: String? = null,
    var cvv: String? = null,
    var expiryDate: String? = null
): Parcelable

fun cardModel (block: CardModel.() -> Unit): CardModel {
    return CardModel().apply(block)
}