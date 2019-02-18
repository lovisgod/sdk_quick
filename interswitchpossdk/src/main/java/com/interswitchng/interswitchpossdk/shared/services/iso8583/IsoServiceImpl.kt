package com.interswitchng.interswitchpossdk.shared.services.iso8583

import android.content.Context
import com.interswitchng.interswitchpossdk.R
import com.interswitchng.interswitchpossdk.shared.Constants.KEY_MASTER_KEY
import com.interswitchng.interswitchpossdk.shared.Constants.KEY_PIN_KEY
import com.interswitchng.interswitchpossdk.shared.Constants.KEY_SESSION_KEY
import com.interswitchng.interswitchpossdk.shared.interfaces.library.IKeyValueStore
import com.interswitchng.interswitchpossdk.shared.interfaces.library.IsoService
import com.interswitchng.interswitchpossdk.shared.interfaces.library.IsoSocket
import com.interswitchng.interswitchpossdk.shared.models.TerminalInfo
import com.interswitchng.interswitchpossdk.shared.models.transaction.cardpaycode.request.TransactionInfo
import com.interswitchng.interswitchpossdk.shared.models.transaction.cardpaycode.response.TransactionResponse
import com.interswitchng.interswitchpossdk.shared.services.iso8583.utils.*
import com.interswitchng.interswitchpossdk.shared.services.iso8583.utils.DateUtils.dateFormatter
import com.interswitchng.interswitchpossdk.shared.services.iso8583.utils.DateUtils.timeAndDateFormatter
import com.interswitchng.interswitchpossdk.shared.services.iso8583.utils.DateUtils.timeFormatter
import com.interswitchng.interswitchpossdk.shared.services.iso8583.utils.IsoUtils.TIMEOUT_CODE
import com.interswitchng.interswitchpossdk.shared.utilities.Logger
import com.solab.iso8583.parse.ConfigParser
import java.io.StringReader
import java.io.UnsupportedEncodingException
import java.text.ParseException
import java.util.*

internal class IsoServiceImpl(
        private val context: Context,
        private val store: IKeyValueStore,
        private val socket: IsoSocket) : IsoService {

    private val logger by lazy { Logger.with("IsoServiceImpl") }
    private val messageFactory by lazy {
        try {

            val data = FileUtils.getFromAssets(context)
            val string = String(data!!)
            val stringReader = StringReader(string)
            val messageFactory = ConfigParser.createFromReader(stringReader)
            messageFactory.isUseBinaryBitmap = false //NIBSS usebinarybitmap = false
            messageFactory.characterEncoding = "UTF-8"

            return@lazy messageFactory

        } catch (e: Exception) {
            logger.logErr(e.localizedMessage)
            e.printStackTrace()
            throw e
        }
    }

    private fun makeKeyCall(terminalId: String, code: String, key: String): String? {
        try {

            val now = Date()
            val stan = getNextStan()

            val message = NibssIsoMessage(messageFactory.newMessage(0x800))
            message
                    .setValue(3, code)
                    .setValue(7, timeAndDateFormatter.format(now))
                    .setValue(11, stan)
                    .setValue(12, timeFormatter.format(now))
                    .setValue(13, dateFormatter.format(now))
                    .setValue(41, terminalId)

            // remove unset fields
            message.message.removeFields(62, 64)
            message.dump(System.out, "request -- ")

            // open to socket endpoint
            socket.open()

            // send request and process response
            val response = socket.sendReceive(message.message.writeData())
            // close connection
            socket.close()

            // read message
            val msg = NibssIsoMessage(messageFactory.parseMessage(response, 0))
            msg.dump(System.out, "response -- ")


            // extract encrypted key with clear key
            val encryptedKey = msg.message.getField<String>(SRCI)
            val decryptedKey = TripleDES.soften(key, encryptedKey.value)
            logger.log("Decrypted Key => $decryptedKey")

            return decryptedKey
        } catch (e: UnsupportedEncodingException) {
            logger.logErr(e.localizedMessage)
        } catch (e: ParseException) {
            logger.logErr(e.localizedMessage)
        }

        return null
    }

    override fun downloadKey(terminalId: String): Boolean {
        // get clear key
        val cms = context.getString(R.string.cms)

        // get master key & save
        val isDownloaded = makeKeyCall(terminalId, "9A0000", cms)?.let { masterKey ->
            store.saveString(KEY_MASTER_KEY, masterKey)

            // get pin key & save
            val isSessionSaved = makeKeyCall(terminalId, "9B0000", masterKey)?.let { sessionKey ->
                store.saveString(KEY_SESSION_KEY, sessionKey)
                true
            }

            // get pin key & save
            val isPinSaved = makeKeyCall(terminalId, "9G0000", masterKey)?.let { pinKey ->
                store.saveString(KEY_PIN_KEY, pinKey)
                true
            }

            isPinSaved == true && isSessionSaved == true
        }

        return isDownloaded == true
    }

    override fun downloadTerminalParameters(terminalId: String): Boolean {
        try {
            val code = "9C0000"
            val field62 = "01009280824266"

            val now = Date()
            val stan = getNextStan()

            val message = NibssIsoMessage(messageFactory.newMessage(0x800))
            message
                    .setValue(3, code)
                    .setValue(7, timeAndDateFormatter.format(now))
                    .setValue(11, stan)
                    .setValue(12, timeFormatter.format(now))
                    .setValue(13, dateFormatter.format(now))
                    .setValue(41, terminalId)
                    .setValue(62, field62)


            val bytes = message.message.writeData()
            val length = bytes.size
            val temp = ByteArray(length - 64)
            if (length >= 64) {
                System.arraycopy(bytes, 0, temp, 0, length - 64)
            }


            // confirm that key was downloaded
            val key = store.getString(KEY_SESSION_KEY, "")
            if (key.isEmpty()) return false

            val hashValue = IsoUtils.getMac(key, temp) //SHA256
            message.setValue(64, hashValue)
            message.dump(System.out, "parameter request ---- ")

            // open socket connection
            socket.open()

            // send request and receive response
            val response = socket.sendReceive(message.message.writeData())
            // close connection
            socket.close()

            // read message
            val responseMessage = NibssIsoMessage(messageFactory.parseMessage(response, 0))
            responseMessage.dump(System.out, "parameter response ---- ")


            // get string formatted terminal info
            val terminalDataString = responseMessage.message.getField<String>(62).value
            logger.log("Terminal Data String => $terminalDataString")

            // parse and save terminal info
            val terminalData = TerminalInfoParser.parse(terminalId, terminalDataString)?.also { it.persist(store) }
            logger.log("Terminal Data => $terminalData")

            return true
        } catch (e: Exception) {
            logger.log(e.localizedMessage)
            e.printStackTrace()
        }

        return false
    }

    override fun initiatePurchase(terminalInfo: TerminalInfo, transaction: TransactionInfo): TransactionResponse? {
        try {
            val now = Date()
            val message = NibssIsoMessage(messageFactory.newMessage(0x200))
            val processCode = "00" + transaction.accountType.value + "00"
            val hasPin = transaction.cardPIN.isNotEmpty()
            val stan = getNextStan()
            val randomReference = "000000$stan"

            message
                    .setValue(2, transaction.cardPAN)
                    .setValue(3, processCode)
                    .setValue(4, String.format(Locale.getDefault(), "%012d", transaction.amount))
                    .setValue(7, timeAndDateFormatter.format(now))
                    .setValue(11, stan)
                    .setValue(12, timeFormatter.format(now))
                    .setValue(13, dateFormatter.format(now))
                    .setValue(14, transaction.cardExpiry)
                    .setValue(18, terminalInfo.merchantCategoryCode)
                    .setValue(22, "051")
                    .setValue(23, "001")
                    .setValue(25, "00")
                    .setValue(26, "06")
                    .setValue(28, "C00000000")
                    .setValue(35, transaction.cardTrack2)
                    .setValue(37, randomReference)
                    .setValue(40, "601")
                    .setValue(41, terminalInfo.terminalId)
                    .setValue(42, terminalInfo.merchantId)
                    .setValue(43, terminalInfo.merchantNameAndLocation)
                    .setValue(49, terminalInfo.currencyCode)
                    .setValue(55, transaction.icc)

            if (hasPin) {
                val pinKey = store.getString(KEY_PIN_KEY, "")
                if (pinKey.isEmpty()) return null

                val pinData = TripleDES.harden(pinKey, transaction.cardPIN)
                message.setValue(52, pinData)
                    .setValue(123, "510101511344101")

                // remove unset fields
                message.message.removeFields(32, 59)
            } else {
                message.setValue(123, "511101511344101")
                // remove unset fields
                message.message.removeFields(32, 52, 59)
            }

            // set message hash
            val bytes = message.message.writeData()
            val length = bytes.size
            val temp = ByteArray(length - 64)
            if (length >= 64) {
                System.arraycopy(bytes, 0, temp, 0, length - 64)
            }

            val sessionKey = store.getString(KEY_SESSION_KEY, "")
            val hashValue = IsoUtils.getMac(sessionKey, temp) //SHA256
            message.setValue(128, hashValue)
            message.dump(System.out, "request -- ")

            // open connection
            socket.open()

            socket.setTimeout(2 * 60000)
            val request = message.message.writeData()
            val response = socket.sendReceive(request)
            // close connection
            socket.close()

            val responseMsg = NibssIsoMessage(messageFactory.parseMessage(response, 0))
            responseMsg.dump(System.out, "")


            // return response
            return responseMsg.message.let {
                val code = it.getObjectValue<String>(39)
                val script = it.getObjectValue<String>(128)
                return@let TransactionResponse(code, stan, script)
            }
        } catch (e: Exception) {
            logger.log(e.localizedMessage)
            e.printStackTrace()
            return TransactionResponse(TIMEOUT_CODE, stan = "", scripts = "")
        }
    }

    private fun getNextStan(): String {
        var stan = store.getNumber(KEY_STAN, 0)

        val newStan = if (stan > 999999) 0 else ++stan
        store.saveNumber(KEY_STAN, newStan)

        return String.format(Locale.getDefault(), "%06d", newStan)
    }


    companion object {
        private const val KEY_STAN = "stan"
        private const val SRCI = 53

    }

}