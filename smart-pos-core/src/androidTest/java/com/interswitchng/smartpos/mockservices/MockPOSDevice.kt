package com.interswitchng.smartpos.mockservices

import com.interswitchng.smartpos.shared.interfaces.device.EmvCardTransaction
import com.interswitchng.smartpos.shared.interfaces.device.IPrinter
import com.interswitchng.smartpos.shared.interfaces.device.POSDevice

internal class MockPOSDevice(override val emvCardTransaction: EmvCardTransaction, override val printer: IPrinter) : POSDevice {


}