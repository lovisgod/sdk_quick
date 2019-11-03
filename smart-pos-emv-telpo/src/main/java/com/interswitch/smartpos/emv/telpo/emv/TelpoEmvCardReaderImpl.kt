package com.interswitch.smartpos.emv.telpo.emv

import android.content.Context
import com.interswitch.smartpos.emv.telpo.TelpoPinCallback
import com.interswitchng.smartpos.shared.interfaces.device.EmvCardReader
import com.interswitchng.smartpos.shared.models.core.TerminalInfo
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

//        val resultMsg = when (setupResult) {
//            EmvResult.EMV_ERR_ICC_CMD.errCode -> EmvResult.EMV_ERR_ICC_CMD
//            EmvResult.EMV_ERR_NO_APP.errCode -> EmvResult.EMV_ERR_NO_APP
//            EmvResult.EMV_ERR_NO_PASSWORD.errCode -> EmvResult.EMV_ERR_NO_PASSWORD
//            EmvResult.EMV_ERR_TIME_OUT.errCode -> EmvResult.EMV_ERR_TIME_OUT
//            EmvResult.EMV_ERR_NO_DATA.errCode -> EmvResult.EMV_ERR_NO_DATA
//            else -> null
//        }
//
//        if (resultMsg != null) callTransactionCancelled(resultMsg.errCode, "Unable to read Card")
//        else channel.send(EmvMessage.CardRead(emvImpl.getCardType()))
    }

    override fun startTransaction(): EmvResult {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun completeTransaction(response: TransactionResponse): EmvResult {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun cancelTransaction() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getTransactionInfo(): EmvData? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}