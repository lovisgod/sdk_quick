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


        val masterKeyMessage = communication.makeGetMasterKeyCall();
        val masterKeyDecrypted = TripleDES.soften(NibbsCommunication.CLEAR_MASTER_KEY,
                masterKeyMessage.getString(SRCI));
        communication.put(NibbsCommunication.KEY_MASTER_KEY, masterKeyDecrypted);

        logger.log("Decrypted Master => $masterKeyDecrypted");
        val sessionKeyIsoMsg = communication.makeGetSessionKeyCall();
        val decryptedSessionKey = TripleDES.soften(communication.get(NibbsCommunication.KEY_MASTER_KEY),
                sessionKeyIsoMsg.getString(SRCI));
        communication.put(NibbsCommunication.KEY_SESSION_KEY, decryptedSessionKey);
        logger.log("Decrypted Session => $decryptedSessionKey");

        val pinKeyMsg = communication.makeGetPinKeyCall();
        val decryptedPinKey = TripleDES.soften(communication.get(NibbsCommunication.KEY_MASTER_KEY),
                pinKeyMsg.getString(SRCI));
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
    fun anotherTest() {
        val message = "0200F23C46D128E08200000000000000002116418742210021217700100000000000200009191258573378891258570919210660120510000003D0000000006418742374187422100212177=2106226164712330000020390033788920390007203900000000033PLOT 1161,Mem Drive,CBDABUJA ABNG       5662588407A000000003101082021C005F3401009A031809199C01315F2A0205669F02060000000000009F03060000000000009F1A0205665F2A0205669F2701809F360200EA9F2608C2B4A74601BCD0259F101800000A03A0A0000A0200000000003F28B24E9F3501229F3704BAE4888A9F3303E0F8C8950540800418009F34030203000152112015133440023fa4263e154f9b633546e398b5739312154ba84690d9d0a0ecb24e3bb249d834"
//
//        val isoMsg = ISOMsg()
//        isoMsg.packager = ISO87APackager()
//        isoMsg.unpack(message.toByteArray())
//
//        isoMsg.dump(System.out, "")

        val communication = NibbsCommunication(context) { ip, port, timeout -> NibssIsoSocket(ip, port, timeout.toInt()) }
        communication.init()

        val request = NibssIsoMessage(communication.messageFactory.newMessage(0x200))
        request
                .setValue(2, "4187422100212177")
                .setValue(3, "001000")
                .setValue(4, "000000002000")
                .setValue(7, "0919125857")
                .setValue(11, "337889")
                .setValue(12, "125857")
                .setValue(13, "0919")
                .setValue(14, "2106")
                .setValue(18, "6012")
                .setValue(22, "051")
                .setValue(23, "000")
                .setValue(25, "00")

                .setValue(26, "03")
                .setValue(28, "D00000000")
//                .setValue(32, "418742")
                .setValue(35, "4187422100212177=21062261647123300000")
                .setValue(37, "203900337889")
                .setValue(41, "20390007")
                .setValue(42, "203900000000033")
                .setValue(43, "PLOT 1161,Mem Drive,CBDABUJA ABNG       ")
                .setValue(49, "566")
                .setValue(55, "8407A000000003101082021C005F3401009A031809199C01315F2A0205669F02060000000000009F03060000000000009F1A0205665F2A0205669F2701809F360200EA9F2608C2B4A74601BCD0259F101800000A03A0A0000A0200000000003F28B24E9F3501229F3704BAE4888A9F3303E0F8C8950540800418009F3403020300")
                .setValue(123, "211201513344002")
                .setValue(128, "3fa4263e154f9b633546e398b5739312154ba84690d9d0a0ecb24e3bb249d834")

        val actual = request.message.debugString()
        request.dump(System.out)
        assertEquals(message, actual)

        val socket =  NibssIsoSocket(SERVER_IP, SERVER_PORT, TIMEOUT * 3) // timeout 3 minutes
        socket.connect()

        val response = socket.sendReceive(request.message.writeData())
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
                masterKeyMessage.getString(SRCI));
        communication.put(NibbsCommunication.KEY_MASTER_KEY, masterKeyDecrypted);

        logger.log("Decrypted Master => $masterKeyDecrypted");
        val sessionKeyIsoMsg = communication.makeGetSessionKeyCall();
        val decryptedSessionKey = TripleDES.soften(communication.get(NibbsCommunication.KEY_MASTER_KEY),
                sessionKeyIsoMsg.getString(SRCI));
        communication.put(NibbsCommunication.KEY_SESSION_KEY, decryptedSessionKey);
        logger.log("Decrypted Session => $decryptedSessionKey");

        val pinKeyMsg = communication.makeGetPinKeyCall();
        val decryptedPinKey = TripleDES.soften(communication.get(NibbsCommunication.KEY_MASTER_KEY),
                pinKeyMsg.getString(SRCI));
        communication.put(NibbsCommunication.KEY_PIN_KEY, decryptedPinKey);
        logger.log("Decrypted Pin => $decryptedPinKey")

        val managementData = communication.makeGetParametersCall()
        val terminalDataString = managementData.getString(62)
        logger.log("Terminal Data String => $terminalDataString")

        val terminalData = TerminalParameter.parse(terminalDataString);
        terminalData?.persist(communication.preferences)
        logger.log("Terminal Data => $terminalData")


        val purchasePackger = ISO87APackager()
        purchasePackger.setFieldPackager(128, IFA_LLCHAR(64, "MAC 2"))

        val now = Calendar.getInstance().time
        val timeAndDateFormatter = SimpleDateFormat("MMDDhhmmss", Locale.getDefault()) // field 7
        val timeFormatter = SimpleDateFormat("hhmmss", Locale.getDefault()) // field 12
        val dateFormatter = SimpleDateFormat("MMDD", Locale.getDefault()) // field 13


        val purchase = ISOMsg()
        purchase.packager = purchasePackger
        purchase.mti = "0200"
        purchase.set(2, "5061011234567890008")
        purchase.set(3, "010000")
        purchase.set(4, "000000010000")
        purchase.set(7, timeAndDateFormatter.format(now))
        purchase.set(11, "000066")
        purchase.set(12, timeFormatter.format(now))
        purchase.set(13, dateFormatter.format(now))
//        purchase.set(14, "2106")
        purchase.set(18, "1371")
        purchase.set(22, "000")
        purchase.set(25, "00")
//        purchase.set(26, "06")
        purchase.set(28, "C00005000")
//        purchase.set(35, "5061000107015530538D2106601017371941")
        purchase.set(37, "000000000008")
        purchase.set(40, "601")
        purchase.set(41, "20390007")
        purchase.set(42, terminalData.merchantId)
        purchase.set(43, terminalData.merchantLocation)
        purchase.set(49, terminalData.currencyCode)
        purchase.set(59, "90")
        purchase.set(123, "510101511344101")



        // clone and get hashed value of message
        val clone = purchase.clone() as ISOMsg
        val cloneBytes = clone.pack()

        val cloneLength = cloneBytes.size
        val copy = ByteArray(cloneLength - 64)
        System.arraycopy(cloneBytes, 0, copy, 0, cloneLength - 64)
        val hashValue = TripleDES.getMac(decryptedSessionKey, copy) // SHA256

        purchase.set(128, hashValue)
        purchase.dump(System.out, "---- ")


        val socket = NibssIsoSocket("196.6.103.72", 5043, 60 * 5 * 1000)
        socket.connect()

        val request = purchase.pack()
        val response = socket.sendReceive(request)
        val responseMsg = ISOMsg()
        responseMsg.packager = purchasePackger
        responseMsg.unpack(response)
        responseMsg.dump(System.out, "")


    }



    @Test
    fun shouldProduceCorrectMasterKey() {
        val packager = ISO87APackager()
        packager.setFieldPackager(53, IFA_NUMERIC(32, "Security Related Data Control"))

        val key = EmvUtils.str2Bcd("7D40CB82D6DDB3A7CDA37E01DA4BB24C")
        val response = ISOMsg()
        response.mti = "0800"
        response.set(3, "9A0000")
        response.set(7, "000830")
        response.set(11, "12345")
        response.set(12, "181830")
        response.set(13, "1029")
        response.set(41, "20390007")
        response.set(53, key)
        response.packager = packager

        val result = response.pack()
        val expected = String(result)

        val msg = ISOMsg()
        msg.packager = packager
        msg.unpack(result)
        val actual = String((msg.clone() as ISOMsg).pack())

        Assert.assertEquals(expected, actual)
    }


    @Test
    fun shouldParseCorrectPurchaseResponse() {
        val packager = ISO87APackager()
        packager.setFieldPackager(35, IFA_LLNUM(48, "Security Related Data Control"))

        val hexmsg =  "009830383130303233383030303030323830303830303130323931383138333530303030323231383138333531303239303032303339303030373744343043423832443644444233413743444133374530314441344242323443353136453137303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030"
        // convert hex string to byte array
        val bmsg = ISOUtil.hex2byte(hexmsg)
        val m = ISOMsg()
        // set packager, change ISO87BPackager for the matching one.
        m.packager = packager
        //unpack the message using the packager
        m.unpack(bmsg)
        //dump the message to standar output
        m.dump(System.out, "")
    }


}