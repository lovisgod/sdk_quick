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
import com.telpo.emv.EmvAmountData
import com.telpo.emv.EmvPinData
import com.telpo.emv.EmvService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel

class TelpoEmvCardReaderImpl (context: Context) : EmvCardReader, TelpoPinCallback {

    private val telpoEmvService by lazy { EmvService.getInstance() }

    private val telpoEmvImplementation by lazy { TelpoEmvImplementation(context, this) }
    private lateinit var channel: Channel<EmvMessage>
    private lateinit var channelScope: CoroutineScope

    private var isCancelled = false

    private val emvListener by lazy { object: TelpoEmvServiceListener() {
        override fun onInputAmount(p0: EmvAmountData?): Int {
            return super.onInputAmount(p0)
        }

        override fun onInputPin(p0: EmvPinData?): Int {
            return super.onInputPin(p0)
        }
    } }

    override suspend fun showInsertCard() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getPinResult(panBlock: String): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun enterPin(
        isOnline: Boolean,
        triesCount: Int,
        offlineTriesLeft: Int,
        panBlock: String
    ) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun showPinOk() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun setupTransaction(
        amount: Int,
        terminalInfo: TerminalInfo,
        channel: Channel<EmvMessage>,
        scope: CoroutineScope
    ) {
        telpoEmvImplementation.setAmount(amount)

        this.channel = channel
        this.channelScope = scope

        val result = telpoEmvImplementation.setupContactEmvTransaction(terminalInfo)

        val resultMsg = when (result) {
            EmvService.ERR_ICCCMD, EmvService.ERR_NOAPP,
            EmvService.ERR_NOPIN, EmvService.ERR_TIMEOUT, EmvService.ERR_NODATA -> 1
            else -> null
        }

        if (resultMsg != null) callTransactionCancelled(resultMsg, "Unable to read Card")
        else channel.send(EmvMessage.CardRead(CardType.None)) /*Still working on how to get card type*/
    }

    private suspend fun callTransactionCancelled(code: Int, reason: String) {
        if (!isCancelled) {
            channel.send(EmvMessage.TransactionCancelled(code, reason))
            cancelTransaction()
        }
    }

    override fun startTransaction(): EmvResult {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun completeTransaction(response: TransactionResponse): EmvResult {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun cancelTransaction() {
        if (!isCancelled) {
            isCancelled = true
//            ped.setInputPinListener(null) // remove the pin pad
        }
    }

    override fun getTransactionInfo(): EmvData? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}