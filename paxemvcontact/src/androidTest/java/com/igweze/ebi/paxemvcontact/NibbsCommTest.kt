package com.igweze.ebi.paxemvcontact

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.igweze.ebi.paxemvcontact.iso8583.*
import com.igweze.ebi.paxemvcontact.utilities.Constants.SERVER_IP
import com.igweze.ebi.paxemvcontact.utilities.Constants.SERVER_PORT
import com.igweze.ebi.paxemvcontact.utilities.Constants.TIMEOUT
import com.igweze.ebi.paxemvcontact.utilities.EmvUtils
import com.igweze.ebi.paxemvcontact.utilities.Logger
import com.solab.iso8583.IsoMessage
import org.jpos.iso.*
import org.jpos.iso.packager.ISO87APackager
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import java.text.SimpleDateFormat
import java.util.*


@RunWith(AndroidJUnit4::class)
class NibbsCommTest {

    private val context = InstrumentationRegistry.getContext()
    private val logger = Logger.with("NibbsCommunicationTest")


    @Test
    fun sendAuthRequest() {

        val communication = NibbsCommunication(context) { ip, port, timeout -> NibssIsoSocket(ip, port, timeout.toInt()) }
        val SRCI = 53
        val RESPONSE_CODE = 39


        val masterKeyMessage = communication.makeGetMasterKeyCall().getField<String>(SRCI)
        val masterKeyDecrypted = TripleDES.soften(NibbsCommunication.CLEAR_MASTER_KEY, masterKeyMessage.value)

        communication.put(NibbsCommunication.KEY_MASTER_KEY, masterKeyDecrypted);
        logger.log("Decrypted Master => $masterKeyDecrypted")

        val sessionKeyIsoMsg = communication.makeGetSessionKeyCall().getField<String>(SRCI)
        val decryptedSessionKey = TripleDES.soften(communication.get(NibbsCommunication.KEY_MASTER_KEY),
                sessionKeyIsoMsg.value)

        communication.put(NibbsCommunication.KEY_SESSION_KEY, decryptedSessionKey);
        logger.log("Decrypted Session => $decryptedSessionKey")

        val pinKeyMsg = communication.makeGetPinKeyCall().getField<String>(SRCI)
        val decryptedPinKey = TripleDES.soften(communication.get(NibbsCommunication.KEY_MASTER_KEY),
                pinKeyMsg.value)

        communication.put(NibbsCommunication.KEY_PIN_KEY, decryptedPinKey);
        logger.log("Decrypted Pin => $decryptedPinKey")

        val managementData = communication.makeGetParametersCall()
        val terminalDataString = managementData.getString(62)
        logger.log("Terminal Data String => $terminalDataString")

        val terminalData = TerminalParameter.parse(terminalDataString);
        terminalData?.persist(communication.preferences)
        logger.log("Terminal Data => $terminalData")



        val txnRequest = communication.purchaseMessage
        val socket =  NibssIsoSocket(SERVER_IP, SERVER_PORT, TIMEOUT * 3) // timeout 3 minutes
        socket.connect()

        val request = txnRequest.writeData()
        val response = socket.sendReceive(request)
        val responseMsg = ISOMsg()

        responseMsg.packager = ISO87APackager()
        responseMsg.unpack(response)
        responseMsg.dump(System.out, "")
    }

    @Test
    fun sendProcessingCodeRequest() {

        val communication = NibbsCommunication(context) { ip, port, timeout -> NibssIsoSocket(ip, port, timeout.toInt()) }
        val SRCI = 53
        val RESPONSE_CODE = 39


        val masterKeyMessage = communication.makeGetMasterKeyCall();
        val masterKeyDecrypted = TripleDES.soften(NibbsCommunication.CLEAR_MASTER_KEY,
                masterKeyMessage.getObjectValue(SRCI));
        communication.put(NibbsCommunication.KEY_MASTER_KEY, masterKeyDecrypted);

        logger.log("Decrypted Master => $masterKeyDecrypted")
        val sessionKeyIsoMsg = communication.makeGetSessionKeyCall().getField<String>(SRCI)
        val decryptedSessionKey = TripleDES.soften(communication.get(NibbsCommunication.KEY_MASTER_KEY),
                sessionKeyIsoMsg.value);
        communication.put(NibbsCommunication.KEY_SESSION_KEY, decryptedSessionKey);
        logger.log("Decrypted Session => $decryptedSessionKey");

        val pinKeyMsg = communication.makeGetPinKeyCall().getField<String>(SRCI)
        val decryptedPinKey = TripleDES.soften(communication.get(NibbsCommunication.KEY_MASTER_KEY),
                pinKeyMsg.value);
        communication.put(NibbsCommunication.KEY_PIN_KEY, decryptedPinKey);
        logger.log("Decrypted Pin => $decryptedPinKey")

        val managementData = communication.makeGetParametersCall()
        val terminalDataString = managementData.getString(62)
        logger.log("Terminal Data String => $terminalDataString")

        val terminalData = TerminalParameter.parse(terminalDataString);
        terminalData?.persist(communication.preferences)
        logger.log("Terminal Data => $terminalData")




        val txnRequest = communication.paycodeTransaction
        val socket =  NibssIsoSocket(SERVER_IP, SERVER_PORT, TIMEOUT * 3) // timeout 3 minutes
        socket.connect()

        val request = txnRequest.writeData()
        val response = socket.sendReceive(request)
        val responseMsg = ISOMsg()

        responseMsg.packager = ISO87APackager()
        responseMsg.unpack(response)
        responseMsg.dump(System.out, "")
    }


}