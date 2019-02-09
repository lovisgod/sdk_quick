package com.interswitchng.interswitchpossdk

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.interswitchng.interswitchpossdk.shared.services.iso8583.*
import com.interswitchng.interswitchpossdk.shared.services.iso8583.utils.*
import com.interswitchng.interswitchpossdk.shared.services.iso8583.tcp.*
import com.interswitchng.interswitchpossdk.shared.services.iso8583.utils.Constants.SERVER_IP
import com.interswitchng.interswitchpossdk.shared.services.iso8583.utils.Constants.SERVER_PORT
import com.interswitchng.interswitchpossdk.shared.services.iso8583.utils.Constants.TIMEOUT
import com.interswitchng.interswitchpossdk.utils.Logger
import org.junit.Test
import org.junit.runner.RunWith


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
        val terminalDataString = managementData.getField<String>(62).value
        logger.log("Terminal Data String => $terminalDataString")

        val terminalData = TerminalParameter.parse(terminalDataString);
        terminalData?.persist(communication.preferences)
        logger.log("Terminal Data => $terminalData")



        val txnRequest = communication.purchaseMessage
        val socket =  NibssIsoSocket(SERVER_IP, SERVER_PORT, TIMEOUT * 3) // timeout 3 minutes
        socket.connect()

        val request = txnRequest.writeData()
        val response = socket.sendReceive(request)
        val responseMsg = NibssIsoMessage(communication.messageFactory.parseMessage(response, 0))
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
        val terminalDataString = managementData.getField<String>(62).value
        logger.log("Terminal Data String => $terminalDataString")

        val terminalData = TerminalParameter.parse(terminalDataString);
        terminalData?.persist(communication.preferences)
        logger.log("Terminal Data => $terminalData")




        val txnRequest = communication.paycodeTransaction
        val socket =  NibssIsoSocket(SERVER_IP, SERVER_PORT, TIMEOUT * 3) // timeout 3 minutes
        socket.connect()

        val request = txnRequest.writeData()
        val response = socket.sendReceive(request)
        val responseMsg = NibssIsoMessage(communication.messageFactory.parseMessage(response, 0))
        responseMsg.dump(System.out, "")
    }


}