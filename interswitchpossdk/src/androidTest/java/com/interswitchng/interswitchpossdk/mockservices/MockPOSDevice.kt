package com.interswitchng.interswitchpossdk.mockservices

import com.interswitchng.interswitchpossdk.shared.interfaces.CardInsertedCallback
import com.interswitchng.interswitchpossdk.shared.interfaces.POSDevice
import com.interswitchng.interswitchpossdk.shared.models.CardDetail
import com.interswitchng.interswitchpossdk.shared.models.posconfig.PrintObject

internal class MockPOSDevice: POSDevice {

    override fun attachCallback(callback: CardInsertedCallback) {

        Thread(Runnable {

            Thread.sleep(3000)

            val cardDetail = CardDetail("1238248527504837", "09-21")
            callback.onCardRead(cardDetail)

            Thread.sleep(4000)

        }).start()

    }

    override fun detachCallback(callback: CardInsertedCallback) {}

    override fun printReceipt(printSlip: List<PrintObject>) {

    }

    override fun checkPin(string: String) {

    }
}