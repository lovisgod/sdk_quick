package com.interswitchng.smartpos.emv.pax.emv

import com.solab.iso8583.IsoMessage

internal data class PurchaseRequest(
        val pan: DataElement,
        val processCode: DataElement,
        val txnAmount: DataElement,
        val dateAndTime: DataElement,
        val time: DataElement,
        val date: DataElement,
        val dateExpiration: DataElement,
        val merchantType: DataElement,
        val posEntryMode: DataElement,
        val appPanNumber: DataElement,
        val posConditionCode: DataElement,
        val posPinCaptureCode: DataElement,
        val txnFee: DataElement,
        val track2data: DataElement,
        val retrievalReferenceNumber: DataElement,
        val cardAcceptorTerminalID: DataElement,
        val cardAcceptorIDCode: DataElement,
        val cardAcceptorNameAndLocation: DataElement,
        val currencyCode: DataElement)

data class DataElement(val fieldNumber: Int, val fieldValue: String) {


    fun addElementTo(message: IsoMessage, template: IsoMessage) {
        val templateValue = template.getAt<Any>(fieldNumber)
        message.setValue(fieldNumber, fieldValue, templateValue.type, templateValue.length)
    }
}