package com.interswitchng.smartpos.shared.interfaces.device

/**
 * This interface represents the POS device to be used by the library,
 * providing access to its printer and card reader.
 */
interface POSDevice {

    /**
     * A member implementation of the printer [DevicePrinter]
     */
    val printer: DevicePrinter


    /**
     * Returns the emv card reader implementation [EmvCardReader]
     * that processes emv card chip transactions
     */
    // TODO rename function to getEmvCardReader
    fun getEmvCardReader(): EmvCardReader

}