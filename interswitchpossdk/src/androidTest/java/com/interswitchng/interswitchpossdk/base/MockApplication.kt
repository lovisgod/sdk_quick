package com.interswitchng.interswitchpossdk.base

import android.app.Application
import com.interswitchng.interswitchpossdk.IswPos
import com.interswitchng.interswitchpossdk.mockservices.MockPOSDevice
import com.interswitchng.interswitchpossdk.shared.models.posconfig.POSConfiguration
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
        val device = MockPOSDevice()
        IswPos.configureTerminal(this, device)

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