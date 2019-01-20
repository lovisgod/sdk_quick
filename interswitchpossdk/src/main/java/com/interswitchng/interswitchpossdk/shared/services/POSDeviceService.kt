package com.interswitchng.interswitchpossdk.shared.services

import com.interswitch.posinterface.posshim.*
import com.interswitchng.interswitchpossdk.shared.errors.DeviceError
import com.interswitchng.interswitchpossdk.shared.interfaces.CardInsertedCallback
import com.interswitchng.interswitchpossdk.shared.interfaces.POSDevice
import com.interswitchng.interswitchpossdk.shared.models.CardDetail
import com.interswitchng.interswitchpossdk.shared.models.posconfig.PrintObject

internal class POSDeviceService(private val device: PosInterface) : POSDevice, CardService.Callback {

    private var cardCallback: CardInsertedCallback? = null


    //----------------------------------------------------------
    //      Implementation for ISW POSDevice interface
    //----------------------------------------------------------
    override fun attachCallback(callback: CardInsertedCallback) {
        cardCallback = callback
        // attach callback to device
        device.attachCallback(this)
    }

    override fun detachCallback(callback: CardInsertedCallback) {
        device.removeCallback(this)
        cardCallback = null
    }

    override fun printReceipt(printSlip: List<PrintObject>) {
        val printConfig = PrintConfiguration()

        // extract slip items with device's print config
        for (item in printSlip) {
            when (item) {
                is PrintObject.Line -> printConfig.addLine()
                is PrintObject.BitMap -> printConfig.addBitmap(item.bitmap)
                is PrintObject.Data -> printConfig.addString(item.data)
            }
        }

        // print slip
        device.print(printConfig)
    }


    //---------------------------------------------------------------
    // Implementation for NeptuneSDK CardService.Callback interface
    //----------------------------------------------------------------
    override fun onCardDetected() {
        cardCallback?.onCardDetected()
    }

    override fun onCardRead(card: Card?) {
        card?.apply {
            val cardCopy = CardDetail(pan, expiry)
            cardCallback?.onCardRead(cardCopy)
        }
    }

    override fun onCardRemoved() {
        cardCallback?.onCardRemoved()
    }

    override fun onError(error: PosError?) {
        error?.apply {
            // TODO get the correct error code from POSError
            val errorCode = DeviceError.ERR_CARD_ERROR
            // translate error to domain error
            val deviceError = DeviceError(errorMessage, errorCode)
            cardCallback?.onError(deviceError)
        }
    }

}