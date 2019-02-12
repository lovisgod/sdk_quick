package com.igweze.ebi.paxemvcontact.services

import android.os.SystemClock
import com.igweze.ebi.paxemvcontact.emv.EmvImplementation
import com.igweze.ebi.paxemvcontact.emv.EmvResult
import com.igweze.ebi.paxemvcontact.emv.POSDevice
import com.igweze.ebi.paxemvcontact.emv.PinCallback
import com.igweze.ebi.paxemvcontact.utilities.EmvUtils
import com.interswitchng.interswitchpossdk.shared.interfaces.device.EmvCardTransaction
import com.interswitchng.interswitchpossdk.shared.interfaces.library.EmvCallback
import com.interswitchng.interswitchpossdk.shared.models.CardDetail
import com.interswitchng.interswitchpossdk.shared.models.TerminalInfo
import com.interswitchng.interswitchpossdk.shared.models.transaction.TransactionResult
import com.interswitchng.interswitchpossdk.shared.utilities.Logger
import com.pax.dal.IPed
import com.pax.dal.entity.EKeyCode
import com.pax.dal.entity.EPedType
import com.pax.dal.entity.EPinBlockMode
import com.pax.dal.exceptions.EPedDevException
import com.pax.dal.exceptions.PedDevException
import com.pax.jemv.clcommon.ACType
import com.pax.jemv.clcommon.RetCode


class EmvTransactionService : EmvCardTransaction, PinCallback, IPed.IPedInputPinListener {

    private val logger by lazy { Logger.with("EmvTransactionService") }
    private val ped by lazy { POSDevice.dal.getPed(EPedType.INTERNAL) }

    private var text = ""

    private val emvImpl by lazy { EmvImplementation(this) }
    private var emvCallback: EmvCallback? = null

    private var pinResult: Int = RetCode.EMV_OK
    private var pinData: String? = null


    //----------------------------------------------------------
    //     Implementation for ISW EmvCardTransaction interface
    //----------------------------------------------------------

    override fun setEmvCallback(callback: EmvCallback) {
        emvCallback = callback
    }

    override fun removeEmvCallback(callback: EmvCallback) {
        emvCallback = null
    }

    override fun setupTransaction(amount: Int, terminalInfo: TerminalInfo) {
        emvImpl.setAmount(amount)
        val setupResult = emvImpl.setupContactEmvTransaction(terminalInfo)

        val resultMsg = when(setupResult) {
            EmvResult.EMV_ERR_ICC_CMD.errCode -> EmvResult.EMV_ERR_ICC_CMD
            EmvResult.EMV_ERR_NO_APP.errCode -> EmvResult.EMV_ERR_NO_APP
            EmvResult.EMV_ERR_NO_PASSWORD.errCode -> EmvResult.EMV_ERR_NO_PASSWORD
            EmvResult.EMV_ERR_TIME_OUT.errCode -> EmvResult.EMV_ERR_TIME_OUT
            else -> null
        }

        if (resultMsg != null) emvCallback?.onTransactionCancelled(resultMsg.errCode, resultMsg.errMsg)
    }

    override fun startTransaction(): TransactionResult {
        val result = emvImpl.startContactEmvTransaction()

        return when (result) {
            ACType.AC_TC -> emvImpl.completeContactEmvTransaction().let { TransactionResult.OFFLINE_APPROVED }
            ACType.AC_ARQC -> logger.log("online should be processed").let { TransactionResult.ONLINE_REQUIRED } //processOnline()
            else -> logger.log("offline declined").let { TransactionResult.OFFLINE_DENIED }
        }
    }

    override fun getCardDetail(): CardDetail {
        return CardDetail("", "")
    }

    override fun completeTransaction() {

    }

    override fun cancelTransaction() {
        ped.setInputPinListener(null) // remove the pin pad
        emvCallback = null
    }


    //----------------------------------------------------------
    //      Implementation for PAX PinCallback interface
    //----------------------------------------------------------
    override fun showInsertCard() {
        // notify callback
        emvCallback?.showInsertCard()

        // try and detect card
        while (true) {
            if (POSDevice.dal.icc.detect(0x00)) break
        }

        // notify callback of card detected
        emvCallback?.onCardDetected()
    }

    override fun getPinResult(panBlock: String) = pinResult


    override fun enterPin(isOnline: Boolean, offlineTriesLeft: Int, panBlock: String) {
        try {
            // reset text
            text = ""
            // notify callback to show pin
            emvCallback?.showEnterPin()
            // set empty string as pin
            emvCallback?.setPinText(text)

            // set check interval
            ped.setIntervalTime(1, 1)
            // set input listener
            ped.setInputPinListener(this)

            // trigger pin input based flag
            if (isOnline) getOnlinePin(panBlock)
            else getOfflinePin()

        } catch (e: PedDevException) {
            logger.logErr("Error occured verifying pin: isOnline - $isOnline, code - ${e.errCode}, msg - ${e.errMsg}")
            setPinResult(e.errCode) // set the pin result
        }
    }

    override fun showPinOk() {
        emvCallback?.showPinOk()
    }

    private fun getOnlinePin(panBlock: String) {
        val pinBlock = ped.getPinBlock(INDEX_TPK, "4,5", panBlock.toByteArray(), EPinBlockMode.ISO9564_0, 60 * 1000)
        if (pinBlock == null)
            pinResult = RetCode.EMV_NO_PASSWORD
        else {
            pinResult = RetCode.EMV_OK
            pinData = EmvUtils.bcd2Str(pinBlock)
        }
    }

    private fun getOfflinePin() {
        // make system sleep so that PED screen will show up
        SystemClock.sleep(300)
    }



    //----------------------------------------------------------
    //      Implementation for PAX PedInputListener interface
    //----------------------------------------------------------

    override fun onKeyEvent(key: EKeyCode?) {
        // react to key press
        text = when (key) {
            EKeyCode.KEY_CLEAR -> ""
            EKeyCode.KEY_ENTER,
            EKeyCode.KEY_CANCEL -> {
                // remove input listener
                ped.setInputPinListener(null)
                "" + text
            }
            else -> "*$text"
        }

        // check if the user cancelled pin input
        if (key == EKeyCode.KEY_CANCEL)
            emvCallback?.onTransactionCancelled(RetCode.EMV_USER_CANCEL, "User cancelled PIN input")
        else emvCallback?.setPinText(text) // set password text
    }

    private fun setPinResult(errCode: Int) {
        pinResult = when (errCode) {
            EPedDevException.PED_ERR_INPUT_CANCEL.errCodeFromBasement -> RetCode.EMV_USER_CANCEL
            EPedDevException.PED_ERR_INPUT_TIMEOUT.errCodeFromBasement -> RetCode.EMV_TIME_OUT
            EPedDevException.PED_ERR_PIN_BYPASS_BYFUNKEY.errCodeFromBasement -> RetCode.EMV_NO_PASSWORD
            EPedDevException.PED_ERR_NO_PIN_INPUT.errCodeFromBasement -> RetCode.EMV_NO_PASSWORD
            else -> RetCode.EMV_NO_PINPAD
        }
    }

    companion object {
        private const val INDEX_TPK: Byte = 0x03
    }

}