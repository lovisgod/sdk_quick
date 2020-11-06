package com.interswitch.smartpos.emv.telpo.emv

import android.content.Context
import com.interswitch.smartpos.emv.telpo.TelpoPOSDeviceImpl.Companion.INDEX_TPK
import com.interswitch.smartpos.emv.telpo.TelpoPinCallback
import com.interswitchng.smartpos.shared.interfaces.device.EmvCardReader
import com.interswitchng.smartpos.shared.models.core.TerminalInfo
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.EmvMessage
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.EmvResult
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.request.EmvData
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.response.TransactionResponse
import com.interswitchng.smartpos.shared.utilities.Logger
import com.telpo.emv.EmvPinData
import com.telpo.emv.EmvService
import com.telpo.pinpad.PinParam
import com.telpo.pinpad.PinpadService
import com.telpo.tps550.api.util.StringUtil
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlin.coroutines.coroutineContext

class TelpoEmvCardReaderImpl (private val context: Context) : EmvCardReader, TelpoPinCallback {

    private val telpoEmvImplementation by lazy { TelpoEmvImplementation(context, this) }
    private val logger = Logger.with("Telpo EMV Card Reader Implementation")
    private lateinit var channel: Channel<EmvMessage>
    private lateinit var channelScope: CoroutineScope

    private var amount: Int = 0

    private lateinit var terminalInfo: TerminalInfo

    private var isKimono: Boolean = false

    private var isCancelled = false

    private var cardPinResult: Int = EmvService.EMV_TRUE

    //private var pinData: String? = null
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

    override val pinResult: Int = cardPinResult

    override suspend fun enterPin(
            isOnline: Boolean,
            triesCount: Int,
            offlineTriesLeft: Int,
            panBlock: String,
            pinData: EmvPinData?
    ): Int {
        channel.send(EmvMessage.EnterPin)

            val pinParameter = PinParam(context)
            pinParameter.apply {
                PinBlockFormat = 0
                KeyIndex = INDEX_TPK
                WaitSec = 100
                MaxPinLen = 6
                MinPinLen = 4
                IsShowCardNo = 0
                Amount = (amount / 100.0).toString()
                CardNo = panBlock
            }

            PinpadService.Open(context)

            if (!isOnline) {
                cardPinResult = when (PinpadService.TP_PinpadGetPlainPin(pinParameter, 0, 0, 0)) {
                    PinpadService.PIN_ERROR_CANCEL -> EmvService.ERR_USERCANCEL
                    PinpadService.PIN_ERROR_TIMEOUT -> EmvService.ERR_TIMEOUT
                    PinpadService.PIN_ERROR_PINLEN -> EmvService.ERR_NOPIN
                    PinpadService.PIN_OK -> {
                        /*val pinString = StringUtil.toHexString(pinParameter.Pin_Block)
                        if(pinString!!.contains("00000000")){
                            channel.send(EmvMessage.EmptyPin)
                            EmvService.ERR_NOPIN
                        }*/
                        pinData?.Pin = pinParameter.Pin_Block
                        EmvService.EMV_TRUE
                    }
                    else -> EmvService.EMV_FALSE
                }
                //clear pinBlock and ksnData for offline pin
                StoreData.pinBlock = null
                StoreData.ksnData = null
            } else {

                if (isKimono) {

                    //if key is deleted for some reasons, re-inject keys
                    if(PinpadService.TP_PinpadCheckKey(PinpadService.KEY_TYPE_DUKPT,0) == -9){
                        PinpadService.TP_PinpadWriteDukptIPEK(IPEK_VALUE, KSN_VALUE, 0, 0, 0)
                    }

                    PinpadService.TP_PinpadDukptSessionStart(0)

                    cardPinResult = when (PinpadService.TP_PinpadDukptGetPin(pinParameter)) {
                        PinpadService.PIN_ERROR_CANCEL -> EmvService.ERR_USERCANCEL
                        PinpadService.PIN_ERROR_TIMEOUT -> EmvService.ERR_TIMEOUT
                        PinpadService.PIN_OK -> {
                            StoreData.pinBlock = StringUtil.toHexString(pinParameter.Pin_Block)
                            StoreData.ksnData = StringUtil.toHexString(pinParameter.Curr_KSN)
                            if (StoreData.pinBlock!!.contains("00000000")) {
                                EmvService.ERR_NOPIN
                            } else EmvService.EMV_TRUE
                        }
                        else -> EmvService.EMV_FALSE
                    }

                    PinpadService.TP_PinpadDukptSessionEnd()

                } else {
                    cardPinResult = when (PinpadService.TP_PinpadGetPin(pinParameter)) {
                        PinpadService.PIN_ERROR_CANCEL -> EmvService.ERR_USERCANCEL
                        PinpadService.PIN_ERROR_TIMEOUT -> EmvService.ERR_TIMEOUT
                        PinpadService.PIN_OK -> {
                            StoreData.pinBlock = StringUtil.toHexString(pinParameter.Pin_Block)
                            if (StoreData.pinBlock!!.contains("00000000")) {
                                EmvService.ERR_NOPIN
                            } else EmvService.EMV_TRUE
                        }
                        else -> EmvService.EMV_FALSE
                    }
                }
            }

        if(cardPinResult != EmvService.EMV_TRUE){
            return cardPinResult
        }
        return cardPinResult
    }

    override suspend fun showPinOk() = channel.send(EmvMessage.PinOk)

    override fun getPan(): String? = telpoEmvImplementation.cardPan

    override suspend fun setupTransaction(amount: Int, terminalInfo: TerminalInfo, channel: Channel<EmvMessage>, scope: CoroutineScope) {
        this.amount = amount
        this.terminalInfo = terminalInfo
        this.isKimono = terminalInfo.isKimono
        telpoEmvImplementation.setAmount(amount)

        this.channel = channel
        this.channelScope = scope

        val result = telpoEmvImplementation.setupContactEmvTransaction(terminalInfo)
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
            EmvService.EMV_TRUE -> {
                runBlocking {
                    channel.send(EmvMessage.CardDetails(telpoEmvImplementation.getCardType()))
                    delay(1000)
                }
                logger.log("me: Offline approved").let { EmvResult.ONLINE_REQUIRED }
            }
            else -> logger.log("me: Offline declined").let { EmvResult.OFFLINE_DENIED }
        } else {
            runBlocking {
                callTransactionCancelled(EmvService.ERR_USERCANCEL, "User cancelled PIN input")
            }

            logger.log("me: Transaction was cancelled").let { EmvResult.CANCELLED }
        }
    }

    override fun completeTransaction(response: TransactionResponse): EmvResult {
        telpoEmvImplementation.completeTransaction(response)
        return EmvResult.OFFLINE_APPROVED
    }

    override fun cancelTransaction() {
        if (!isCancelled) {
            isCancelled = true
        }
    }

    override fun getTransactionInfo(): EmvData? {

        logger.log("cardPin-pinData from getTransactionInfo outside the let " + StoreData.pinBlock)

        return telpoEmvImplementation.getTrack2()?.let {
            // get pinData (only for online PIN)
            val cardPin = StoreData.pinBlock ?: ""

            val pinKsn = StoreData.ksnData?.removeRange(0, 4) ?: ""
            logger.log("cardPin from getTransactionInfo $cardPin")
            //logger.log("cardPin-pinData from getTransactionInfo $pinData")

            // get track 2 string
            val track2data = StringUtil.toHexString(it)

            // extract pan and expiry
            val strTrack2 = track2data.split("F")[0]
            val pan = strTrack2.split("D")[0]
            val expiry = strTrack2.split("D")[1].substring(0, 4)
            val src = strTrack2.split("D")[1].substring(4, 7)

            val iccFull=telpoEmvImplementation.getIccFullData()

            val aid = telpoEmvImplementation.getTLVString(0x9F06)!!
            // get the card sequence number
            val csnStr = telpoEmvImplementation.getTLVString(ICCData.APP_PAN_SEQUENCE_NUMBER.tag)!!
            val csn = "0$csnStr"

            EmvData(cardPAN = pan, cardExpiry = expiry, cardPIN = cardPin, cardTrack2 = track2data, icc = iccFull, AID = aid, src = src, csn = csn, pinKsn = pinKsn)
        }
    }

    class StoreData {
        companion object {
            var pinBlock: String? = null
            var ksnData: String? = null
        }
    }

    companion object{
        internal val KSN_VALUE = StringUtil.toBytes("FFFF000002DDDDE00001")
        internal val IPEK_VALUE = StringUtil.toBytes("3F2216D8297BCE9C")
    }
}