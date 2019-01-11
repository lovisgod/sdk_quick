package com.interswitchng.interswitchpossdk.base

import android.app.Application
import com.interswitchng.interswitchpossdk.IswPos
import com.interswitchng.interswitchpossdk.shared.models.POSConfiguration

class MockApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        val config = POSConfiguration()
        IswPos.configureTerminal(this, config)
    }

}