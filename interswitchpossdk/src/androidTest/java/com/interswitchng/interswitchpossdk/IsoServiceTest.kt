package com.interswitchng.interswitchpossdk

import android.support.test.InstrumentationRegistry
import com.interswitchng.interswitchpossdk.shared.services.iso8583.IsoServiceImpl
import com.interswitchng.interswitchpossdk.shared.services.iso8583.tcp.NibssIsoSocket
import com.interswitchng.interswitchpossdk.shared.services.storage.KeyValueStore
import com.interswitchng.interswitchpossdk.shared.services.storage.SharePreferenceManager
import org.junit.Assert.assertTrue
import org.junit.Test

class IsoServiceTest {

    val context = InstrumentationRegistry.getContext()

    @Test
    fun sendTerminalRequest() {
        val prefMnager = SharePreferenceManager(context)
        val keyValueStore = KeyValueStore(prefMnager)
        val isoService = IsoServiceImpl(context, keyValueStore) { ip, port, time -> NibssIsoSocket(ip, port, time) }

        val terminalId = "20390007"
        val result = isoService.downloadKey(terminalId)

        val paramResult = isoService.downloadTerminalParameters(terminalId)

        assertTrue(result)
        assertTrue(paramResult)
    }
}