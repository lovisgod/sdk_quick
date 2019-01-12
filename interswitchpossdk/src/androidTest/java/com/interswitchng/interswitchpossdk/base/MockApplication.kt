package com.interswitchng.interswitchpossdk.base

import android.app.Application
import com.interswitchng.interswitchpossdk.IswPos
import com.interswitchng.interswitchpossdk.shared.models.POSConfiguration
import org.koin.standalone.StandAloneContext.loadKoinModules

class MockApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        val config = POSConfiguration()

        IswPos.configureTerminal(this, config)
        // override existing modules
        loadKoinModules(mockAppModules)
    }

}