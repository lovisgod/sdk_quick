package com.interswitchng.smartpos

import android.support.test.InstrumentationRegistry
import com.interswitchng.smartpos.shared.services.iso8583.IsoServiceImpl
import com.interswitchng.smartpos.shared.services.iso8583.tcp.NibssIsoSocket
import com.interswitchng.smartpos.shared.services.storage.KeyValueStore
import com.interswitchng.smartpos.shared.services.storage.SharePreferenceManager
import org.junit.Assert.assertTrue
import org.junit.Test

class IsoServiceTest {

    val context = InstrumentationRegistry.getContext()

    @Test
    fun sendTerminalRequest() {
        val prefMnager = SharePreferenceManager(context)
        val keyValueStore = KeyValueStore(prefMnager)

        val ip = context.resources.getString(R.string.isw_nibss_ip)
        val port = context.resources.getInteger(R.integer.iswNibssPort)
        val iso = NibssIsoSocket(ip, port, 6000)
        val isoService = IsoServiceImpl(context, keyValueStore, iso)

        val terminalId = "20390007"
        val result = isoService.downloadKey(terminalId)

        val paramResult = isoService.downloadTerminalParameters(terminalId)

        assertTrue(result)
        assertTrue(paramResult)
    }
}