package com.interswitchng.smartpos.emv.pax.models


internal fun aid(closure: EmvAIDs.() -> Unit): EmvAIDs  {
    val anAID = EmvAIDs()
    closure(anAID)
    return anAID
}

internal fun card(anAID: EmvAIDs, closure: EmvCard.() -> Unit): EmvCard {
    val card = EmvCard()
    closure(card)
    anAID.cards.add(card)
    return card
}

internal fun key(aCard: EmvCard, closure: EmvCapk.() -> Unit): EmvCapk {
    val key = EmvCapk()
    closure(key)
    aCard.keys.add(key)
    return key
}