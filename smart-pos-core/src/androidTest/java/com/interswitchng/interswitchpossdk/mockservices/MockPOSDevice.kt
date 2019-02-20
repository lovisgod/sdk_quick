package com.interswitchng.interswitchpossdk.mockservices

import com.interswitchng.interswitchpossdk.shared.interfaces.device.EmvCardTransaction
import com.interswitchng.interswitchpossdk.shared.interfaces.device.IPrinter
import com.interswitchng.interswitchpossdk.shared.interfaces.device.POSDevice

internal class MockPOSDevice(override val emvCardTransaction: EmvCardTransaction, override val printer: IPrinter) : POSDevice {


}