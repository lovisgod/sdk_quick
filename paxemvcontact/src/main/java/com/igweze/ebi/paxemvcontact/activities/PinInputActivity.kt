package com.igweze.ebi.paxemvcontact.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.igweze.ebi.paxemvcontact.R
import com.igweze.ebi.paxemvcontact.utilities.EmvUtils.bcd2Str
import com.igweze.ebi.paxemvcontact.utilities.Logger
import com.pax.dal.entity.EKeyCode
import com.pax.dal.entity.EPedType
import com.pax.dal.entity.EPinBlockMode
import com.pax.dal.exceptions.PedDevException
import com.pax.jemv.clcommon.ACType
import com.pax.jemv.clcommon.RetCode
import kotlinx.android.synthetic.main.activity_pin_input.*
import android.R.attr.tag
import com.igweze.ebi.paxemvcontact.emv.*
import com.igweze.ebi.paxemvcontact.iso8583.*
import com.igweze.ebi.paxemvcontact.utilities.EmvUtils.str2Bcd
import java.util.*


class PinInputActivity : AppCompatActivity(), PinCallback {


    private val logger by lazy { Logger.with("MainActivity") }
    private val ped by lazy { POSDevice.dal.getPed(EPedType.INTERNAL) }
    private val emvImpl by lazy { EmvImplementation(this) }

    private var pin = ""

    private var pinData: ByteArray? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pin_input)

        // start emv transaction
        startTransaction()
    }


    private fun startTransaction() {
        Thread {
            val amount = 5000L
            emvImpl.setAmount(amount)
            val result = emvImpl.startContactEmvTransaction()

            if (result == TransactionResult.EMV_OFFLINE_APPROVED) emvImpl.completeContactEmvTransaction()
            else if (result == TransactionResult.EMV_ARQC) processOnline()
        }.start()
    }


    override fun enterPin(isOnline: Boolean, offlineTriesLeft: Int, panBlock: String) {
        if (isOnline) getOnlinePin(panBlock)
        else getOfflinePin()
    }

    override fun getPinResult(panBlock: String): Int {
        logger.log("Pindata after getResult: " + pinData?.let { bcd2Str(it) })


        return if (pinData == null) RetCode.EMV_NO_PASSWORD else RetCode.EMV_OK
    }

    override fun showInputCard() {
        runOnUiThread { tvPassword.text = "Insert Card into terminal" }

        // try and detect card
        while (true) {
            if (POSDevice.dal.icc.detect(0x00)) break
        }
        runOnUiThread { tvPassword.text = " " }
    }


    private fun getOnlinePin(panBlock: String) {

        try {

            ped.setIntervalTime(1, 1)

            ped.setInputPinListener {
                // react to key press
                val temp = when (it) {
                    EKeyCode.KEY_CLEAR -> ""
                    EKeyCode.KEY_ENTER, EKeyCode.KEY_CANCEL -> "" + tvPassword.text
                    else -> "*" + tvPassword.text
                }

                // get key values
                extractKeys(it)

                // set password text
                runOnUiThread { tvPassword.text = temp }

                if (it == EKeyCode.KEY_ENTER) emvImpl.pinEntryReady()
            }

            // show pin dialog
            val pinData = ped.verifyPlainPin(0x00, "4", 0x00, 60 * 1000)
            val pinResult = bcd2Str(pinData)
            logger.log("Pindata before getResult: " + pinData?.let { bcd2Str(it) })
        } catch (e: PedDevException) {
            logger.logErr("Error occured: code - ${e.errCode}, msg - ${e.errMsg}")
        }
    }

    private fun getOfflinePin() {

    }

    private fun extractKeys(key: EKeyCode) {
        val keyValue = when (key) {
            EKeyCode.KEY_0 -> "0"
            EKeyCode.KEY_1 -> "1"
            EKeyCode.KEY_2 -> "2"
            EKeyCode.KEY_3 -> "3"
            EKeyCode.KEY_4 -> "4"
            EKeyCode.KEY_5 -> "5"
            EKeyCode.KEY_6 -> "6"
            EKeyCode.KEY_7 -> "7"
            EKeyCode.KEY_8 -> "8"
            EKeyCode.KEY_9 -> "9"
            else -> ""
        }

        pin = if (keyValue == "" && key == EKeyCode.KEY_CLEAR) "" else "$pin$keyValue"
    }


    private fun processOnline() {
        val communication = NibbsCommunication(this) { ip, port, timeout -> NibssIsoSocket(ip, port, timeout.toInt()) }
        val SRCI = 53
        val RESPONSE_CODE = 39

        val build = { tag: Int ->
            emvImpl.getTlv(tag)?.let {
                Integer.toHexString(tag) +
                        String.format(Locale.getDefault(), "0%s", Integer.toHexString(it.size)) +
                        bcd2Str(it)
            }
        }

        Thread {

            try {

                val stringBuilder = StringBuilder()
                for (tag in REQUEST_TAGS) {
                    val hex = build(tag.tag)
                    hex?.apply { stringBuilder.append(this) }
                }

                val masterKeyMessage = communication.makeGetMasterKeyCall().getField<String>(SRCI)
                val masterKeyDescrypted = TripleDES.soften(NibbsCommunication.CLEAR_MASTER_KEY,
                        masterKeyMessage.value);
                communication.put(NibbsCommunication.KEY_MASTER_KEY, masterKeyDescrypted);

                logger.log("Decrypted Master => $masterKeyDescrypted");
                val sessionKeyIsoMsg = communication.makeGetSessionKeyCall().getField<String>(SRCI)
                val decryptedSessionKey = TripleDES.soften(communication.get(NibbsCommunication.KEY_MASTER_KEY),
                        sessionKeyIsoMsg.value);
                communication.put(NibbsCommunication.KEY_SESSION_KEY, decryptedSessionKey);
                logger.log("Decrypted Session => $decryptedSessionKey");

                val pinKeyMsg = communication.makeGetPinKeyCall().getField<String>(SRCI)
                val decryptedPinKey = TripleDES.soften(communication.get(NibbsCommunication.KEY_MASTER_KEY),
                        pinKeyMsg.value)
                communication.put(NibbsCommunication.KEY_PIN_KEY, decryptedPinKey);
                logger.log("Decrypted Pin => $decryptedPinKey");

                val managementData = communication.makeGetParametersCall();
                val terminalData = managementData.getString(62);


                val data = TerminalParameter.parse(terminalData);
                if (data != null) {
                    data.persist(communication.preferences);
                }

                val transaction = Transaction();
                val card = Card()

                transaction.amount = 5000;

                card.expiry = "1912"
                card.pin = "1992"
                card.pan = emvImpl.getPan()
                card.track2Data = emvImpl.getTrack2()?.let(::bcd2Str)

                transaction.card = card;
//                val txnResponse = communication.purchaseTransaction(transaction, stringBuilder.toString());
//                logger.log("Transaction Response => " + txnResponse.getString(RESPONSE_CODE));
            } catch (e: Exception) {
                logger.logErr(e.localizedMessage);
            }
        }.start()
    }


}