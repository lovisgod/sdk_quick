package com.interswitch.smartpos.emv.telpo.emv

import android.content.Context
import com.interswitch.smartpos.emv.telpo.TelpoPinCallback
import com.interswitchng.smartpos.shared.interfaces.device.EmvCardReader
import com.interswitchng.smartpos.shared.models.core.TerminalInfo
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.CardType
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.EmvMessage
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.EmvResult
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.request.EmvData
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.response.TransactionResponse
import com.interswitchng.smartpos.shared.utilities.Logger
import com.telpo.emv.EmvService
import com.telpo.tps550.api.util.StringUtil
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlin.coroutines.coroutineContext

class TelpoEmvCardReaderImpl (private val context: Context) : EmvCardReader, TelpoPinCallback {

    private val telpoEmvImplementation by lazy { TelpoEmvImplementation(context, this) }
    private val logger = Logger.with("Telpo EMV Card Reader Implementation")
    private lateinit var channel: Channel<EmvMessage>
    private lateinit var channelScope: CoroutineScope

    private lateinit var terminalInfo: TerminalInfo

    private var isCancelled = false
    private var pinResult: Int = EmvService.EMV_TRUE
    private var pinData: String? = null
    private var hasEnteredPin: Boolean = false

    override suspend fun showInsertCard() {
        //Prompts the user to insert card
        channel.send(EmvMessage.InsertCard)
        //Opens the device card reader
        EmvService.IccOpenReader()

        while (coroutineContext.isActive && !isCancelled) {
            if (EmvService.IccCheckCard(300) == 0){
                break
            }
        }

        channel.send(EmvMessage.CardDetected)

        channelScope.launch(Dispatchers.IO) {
            startWatchingCard()
        }
    }

    override fun getPinResult(panBlock: String): Int = EmvService.EMV_TRUE

    override suspend fun enterPin(
        isOnline: Boolean,
        triesCount: Int,
        offlineTriesLeft: Int,
        panBlock: String
    ) {

    }

    override suspend fun showPinOk() = channel.send(EmvMessage.PinOk)

    override fun getPan(): String? = telpoEmvImplementation.cardPan

    override suspend fun setupTransaction(
        amount: Int,
        terminalInfo: TerminalInfo,
        channel: Channel<EmvMessage>,
        scope: CoroutineScope
    ) {
        this.terminalInfo = terminalInfo
        telpoEmvImplementation.setAmount(amount)

        this.channel = channel
        this.channelScope = scope

        val result = telpoEmvImplementation.setupContactEmvTransaction()
        logger.logErr("Result: $result")

        val resultMsg = when (result) {
            EmvService.ERR_ICCCMD, EmvService.ERR_NOAPP,
            EmvService.ERR_NOPIN, EmvService.ERR_TIMEOUT, EmvService.ERR_NODATA -> 1
            else -> null
        }

        if (resultMsg != null) callTransactionCancelled(resultMsg, "Unable to read Card")
        else channel.send(EmvMessage.CardRead(telpoEmvImplementation.getCardType()))
    }

    private suspend fun callTransactionCancelled(code: Int, reason: String) {
        if (!isCancelled) {
            channel.send(EmvMessage.TransactionCancelled(code, reason))
            cancelTransaction()
        }
    }

    private suspend fun startWatchingCard() {
        // try and detect card
        while (channelScope.isActive) {
            // check if card cannot be detected
            if (EmvService.IccCheckCard(10) != 0) {
                // notify callback of card removal
                channel.send(EmvMessage.CardRemoved)
                break
            }

            delay(500)
        }
    }

    override fun startTransaction(): EmvResult {
        val result = telpoEmvImplementation.startContactEmvTransaction(terminalInfo)

        logger.logErr("Result code: $result")

        hasEnteredPin = true

        return if (!isCancelled) when (result) {
            EmvService.EMV_TRUE -> logger.log("Offline approved").let { EmvResult.ONLINE_REQUIRED }
            else -> logger.log("Offline declined").let { EmvResult.OFFLINE_DENIED }
        } else logger.log("Transaction was cancelled").let { EmvResult.CANCELLED }
    }

    override fun completeTransaction(response: TransactionResponse): EmvResult {
        telpoEmvImplementation.completeTransaction(response)
        return EmvResult.OFFLINE_APPROVED
    }

    override fun cancelTransaction() {
        if (!isCancelled) {
            isCancelled = true
//            ped.setInputPinListener(null) // remove the pin pad
        }
    }

    override fun getTransactionInfo(): EmvData? {
        return telpoEmvImplementation.getTrack2()?.let {
            // get pinData (only for online PIN)
            val carPin = pinData ?: ""

            // get track 2 string
            val track2data = StringUtil.toHexString(it)

            // extract pan and expiry
            val strTrack2 = track2data.split("F")[0]
            val pan = strTrack2.split("D")[0]
            val expiry = strTrack2.split("D")[1].substring(0, 4)
            val src = strTrack2.split("D")[1].substring(4, 7)


            val icc = telpoEmvImplementation.getIccData()
            val aid = StringUtil.toHexString(telpoEmvImplementation.getTLV(0x9F06)!!)
            // get the card sequence number
            val csnStr = StringUtil.toHexString(telpoEmvImplementation.getTLV(ICCData.APP_PAN_SEQUENCE_NUMBER.tag)!!)
            val csn = "0$csnStr"
            EmvData(cardPAN = pan, cardExpiry = expiry, cardPIN = carPin, cardTrack2 = track2data, icc = icc, AID = aid, src = src, csn = csn)
        }
    }
}