package com.interswitchng.interswitchpossdk.base

import android.app.Application
import com.igweze.ebi.paxemvcontact.POSDeviceService
import com.igweze.ebi.paxemvcontact.posshim.CardService
import com.igweze.ebi.paxemvcontact.posshim.PosInterface
import com.interswitchng.interswitchpossdk.IswPos
import com.interswitchng.interswitchpossdk.shared.models.posconfig.POSConfiguration
import org.koin.dsl.module.module
import org.koin.standalone.StandAloneContext.loadKoinModules

class MockApplication: Application() {

    override fun onCreate() {
        super.onCreate()


        val alias = "000007"
        val terminalId = "2069018M"
        val merchantId = "IBP000000001384"
        val merchantLocation = "AIRTEL NETWORKS LIMITED PH MALL"
        // val currencyCode = "566"
        // val posGeoCode = "0023400000000056"
        val terminalType = "PAX"
        val uniqueId = "280-820-589"
        val merchantCode = "MX5882"

        val config = POSConfiguration(alias, terminalId, merchantId, terminalType, uniqueId, merchantLocation, merchantCode)


        // setup device for terminal
        val cardService = CardService.getInstance(applicationContext)
        PosInterface.setDalInstance(applicationContext)
        val pos = PosInterface.getInstance(cardService)
        val deviceService = POSDeviceService(pos)
        IswPos.configureTerminal(this, deviceService)

        // load mock modules based on runner arguments
        if (shouldMock) {
            // override existing modules
            loadKoinModules(mockAppModules)
        }
    }

    companion object {
        internal var shouldMock = true
    }

}