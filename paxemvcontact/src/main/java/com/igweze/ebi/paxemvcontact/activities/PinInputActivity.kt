package com.igweze.ebi.paxemvcontact.activities

import android.os.Bundle
import android.os.SystemClock
import android.support.v7.app.AppCompatActivity
import com.igweze.ebi.paxemvcontact.R
import com.igweze.ebi.paxemvcontact.emv.*
import com.igweze.ebi.paxemvcontact.utilities.EmvUtils.bcd2Str
import com.interswitchng.interswitchpossdk.shared.models.TerminalInfo
import com.interswitchng.interswitchpossdk.shared.utilities.Logger
import com.pax.dal.IPed
import com.pax.dal.entity.EKeyCode
import com.pax.dal.entity.EPedType
import com.pax.dal.entity.EPinBlockMode
import com.pax.dal.exceptions.EPedDevException
import com.pax.dal.exceptions.PedDevException
import com.pax.jemv.clcommon.RetCode
import kotlinx.android.synthetic.main.activity_pin_input.*


class PinInputActivity : AppCompatActivity(), PinCallback, IPed.IPedInputPinListener {


    private val logger by lazy { Logger.with("MainActivity") }
    private val ped by lazy { POSDevice.dal.getPed(EPedType.INTERNAL) }
    private val emvImpl by lazy { EmvImplementation(this) }
    private var pinResult: Int = RetCode.EMV_OK

    private var pinData: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pin_input)

        // start emv transaction
        startTransaction()
    }


    override fun onDestroy() {
        super.onDestroy()
        ped.setInputPinListener(null) // remove the pin pad
    }


    private fun startTransaction() {
        Thread {
            val amount = 500
            emvImpl.setAmount(amount)
            val empty = ""
            val terminalInfo = TerminalInfo("20390007", "20390007",
                    empty, empty, "0566", "0566", 1200, 1200)
            emvImpl.setupContactEmvTransaction(terminalInfo)
            val result = emvImpl.startContactEmvTransaction()

            if (result == TransactionResult.EMV_OFFLINE_APPROVED) emvImpl.completeContactEmvTransaction()
            else if (result == TransactionResult.EMV_ARQC) logger.log("online should be processed") //processOnline()
        }.start()
    }


    override fun enterPin(isOnline: Boolean, offlineTriesLeft: Int, panBlock: String) {
        try {

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

    override fun getPinResult(panBlock: String): Int {
        logger.log("pin result : $pinResult")
        return pinResult
    }

    override fun showInsertCard() {
        runOnUiThread { tvPassword.text = "Insert Card into terminal" }

        // try and detect card
        while (true) {
            if (POSDevice.dal.icc.detect(0x00)) break
        }
        runOnUiThread { tvPassword.text = " " }
    }

    override fun showPinOk() {
        // todo show pin ok
    }

    private fun getOnlinePin(panBlock: String) {
        val pinBlock = ped.getPinBlock(INDEX_TPK, "4,5", panBlock.toByteArray(), EPinBlockMode.ISO9564_0, 60 * 1000)
        if (pinBlock == null)
            pinResult = RetCode.EMV_NO_PASSWORD
        else {
            pinResult = RetCode.EMV_OK
            pinData = bcd2Str(pinBlock)
        }
    }

    private fun getOfflinePin() {
        // make system sleep so that PED screen will show up
//        Thread.sleep(500)
        SystemClock.sleep(300)
    }

    override fun onKeyEvent(key: EKeyCode?) {
        // react to key press
        val temp = when (key) {
            EKeyCode.KEY_CLEAR -> ""
            EKeyCode.KEY_ENTER,
            EKeyCode.KEY_CANCEL -> {
                // remove input listener
                ped.setInputPinListener(null)
                "" + tvPassword.text
            }
            else -> "*" + tvPassword.text
        }

        // set password text
        runOnUiThread { tvPassword.text = temp }
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