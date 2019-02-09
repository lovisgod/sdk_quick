package com.igweze.ebi.paxemvcontact

import android.content.Context
import android.content.SharedPreferences
import android.content.res.AssetManager
import android.content.res.Resources
import com.igweze.ebi.paxemvcontact.iso8583.NibbsCommunication
import com.igweze.ebi.paxemvcontact.iso8583.NibssIsoSocket
import com.igweze.ebi.paxemvcontact.iso8583.TerminalParameter
import com.igweze.ebi.paxemvcontact.iso8583.TripleDES
import com.igweze.ebi.paxemvcontact.utilities.Constants
import com.igweze.ebi.paxemvcontact.utilities.EmvUtils.str2Bcd
import com.igweze.ebi.paxemvcontact.utilities.Logger
import com.nhaarman.mockitokotlin2.*
import org.jpos.iso.IFA_NUMERIC
import org.jpos.iso.ISOMsg
import org.jpos.iso.packager.ISO87APackager
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.MockitoAnnotations.initMocks
import java.io.File
import java.io.InputStream


class NibssParseTest {

    private val logger = Logger.with("NibssParseTest")
    private val map = mutableMapOf<String, String>()
    private var currentStan = 0

    @Mock
    lateinit var editor: SharedPreferences.Editor

    @Mock
    lateinit var sharedPreferences: SharedPreferences

    @Mock
    lateinit var context: Context

    @Mock
    lateinit var mockAssets:AssetManager


    @Before
    fun setup() {
        initMocks(this)

        val mockAssets:AssetManager = mock {
            on (mock.open(anyString())) doAnswer {
                return@doAnswer (it.arguments[0] as String).let { Utilities.getStream(it) }
            }
        }

        val mockResources: Resources = mock { on { mock.assets } doReturn mockAssets }

        whenever(context.resources).thenReturn(mockResources)
        whenever(context.getSharedPreferences(anyString(), anyInt())).thenReturn(sharedPreferences)
        whenever(sharedPreferences.edit()).thenReturn(editor)


        whenever(editor.putInt(anyString(), anyInt())).thenAnswer {
            val value = it.arguments[1] as Int
            currentStan = value

            return@thenAnswer editor
        }

        whenever(editor.putString(anyString(), anyString())).thenAnswer {
            val key = it.arguments[0] as String
            val value = it.arguments[1] as String
            map[key] = value

            return@thenAnswer editor
        }

        whenever(sharedPreferences.getString(anyString(), anyString())).thenAnswer {
            val key = it.arguments[0] as String
            val value = it.arguments[1] as String
            return@thenAnswer map[key] ?: value
        }
    }


    @Test
    fun shouldProduceCorrectMasterKey() {
        val packager = ISO87APackager()

        val key = str2Bcd("7D40CB82D6DDB3A7CDA37E01DA4BB24C")
        packager.setFieldPackager(53, IFA_NUMERIC(32, "Security Related Data Control"))
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

        assertEquals(expected, actual)
    }

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
        val socket =  NibssIsoSocket(Constants.SERVER_IP, Constants.SERVER_PORT, Constants.TIMEOUT * 3) // timeout 3 minutes
        socket.connect()

        val request = txnRequest.writeData()
        val response = socket.sendReceive(request)
        val responseMsg = ISOMsg()

        responseMsg.packager = ISO87APackager()
        responseMsg.unpack(response)
        responseMsg.dump(System.out, "")
    }

    @Test
    fun test () {
        val sampleResponse = Utilities.getJson("success-code-response.json")
        println(sampleResponse)
    }


}