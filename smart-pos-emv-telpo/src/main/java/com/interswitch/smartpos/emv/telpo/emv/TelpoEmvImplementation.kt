package com.interswitch.smartpos.emv.telpo.emv

import android.content.Context
import com.interswitch.smartpos.emv.telpo.TelpoPinCallback
import com.interswitch.smartpos.emv.telpo.models.getAllCapks
import com.interswitch.smartpos.emv.telpo.utils.DefaultAPPCAPK
import com.interswitch.smartpos.emv.telpo.utils.TelpoEmvUtils
import com.interswitchng.smartpos.shared.models.core.TerminalInfo
import com.interswitchng.smartpos.shared.models.posconfig.EmvAIDs
import com.interswitchng.smartpos.shared.models.posconfig.TerminalConfig
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.CardType
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.response.TransactionResponse
import com.interswitchng.smartpos.shared.services.iso8583.utils.FileUtils
import com.interswitchng.smartpos.shared.utilities.Logger
import com.telpo.emv.*
import com.telpo.pinpad.PinParam
import com.telpo.pinpad.PinpadService
import com.telpo.tps550.api.util.StringUtil
import kotlinx.coroutines.runBlocking
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.util.*

internal class TelpoEmvImplementation (
    private val context: Context,
    private val pinCallback: TelpoPinCallback
) {

    private val emvParameter = EmvParam()
    private val logger = Logger.with("Telpo EMV Implementation")

    private lateinit var selectedRID: String

    private val emvService by lazy { EmvService.getInstance().apply { setListener(TelpoEmvServiceListener()) }}

    lateinit var cardPan: String

    private val config: Pair<TerminalConfig, EmvAIDs> by lazy { FileUtils.getConfigurations(context) }

    private var amount: Int = 0

    fun setAmount(amount: Int) {
        this.amount = amount
    }

    private fun addCAPKIntoEmvLib(capks: List<EmvCAPK>) {
        capks.forEach { capk ->
            val ret = EmvService.Emv_AddCapk(capk)
            selectedRID = StringUtil.toHexString(capk.RID)
            logger.log("Add CAPK result == $ret")
            logger.log("Selected RID == $selectedRID")
        }
    }

    private fun writeKeys(): Int {
        return EmvService.EMV_TRUE
    }

    suspend fun setupContactEmvTransaction(): Int {
        var ret = EmvService.Open(context)
        if (ret != EmvService.EMV_TRUE) {
            logger.logErr("Emv Service Open Fail: RET CODE ==== $ret")
            return ret
        }

        ret = EmvService.deviceOpen()
        if (ret != 0) {
            logger.logErr("Emv Service Open Fail: RET CODE ==== $ret")
            return ret
        }

        EmvService.Emv_RemoveAllApp()
        val appList = TelpoEmvUtils.createAppList(config.first, config.second)
        appList.forEach { app ->
            val result = EmvService.Emv_AddApp(app)
            if (result == EmvService.EMV_TRUE) logger.logErr("Add app success : AID = ${StringUtil.toHexString(app.AID)}")
            else return result.also { logger.logErr("Add app failed : RET = $result, AID = ${StringUtil.toHexString(app.AID)}") }
        }

        EmvService.Emv_RemoveAllCapk()
        val capks = config.second.getAllCapks()
        addCAPKIntoEmvLib(capks)

        pinCallback.showInsertCard()

        return ret
    }

    fun startContactEmvTransaction(terminalInfo: TerminalInfo): Int {
        var ret = EmvService.IccCard_Poweron()
        logger.logErr("EmvService Power On: RET = $ret")

        ret = emvService.Emv_TransInit()
        logger.logErr("Initialize transaction: RET CODE ==== $ret")

        val terminalConfig = config.first

        emvService.Emv_GetParam(emvParameter)

        emvParameter.apply {
            MerchName = terminalInfo.merchantNameAndLocation.toByteArray()
            MerchId = terminalInfo.merchantId.toByteArray()
            MerchCateCode = terminalInfo.merchantNameAndLocation.toByteArray()
            TermId = terminalInfo.terminalId.toByteArray()
            TerminalType = terminalConfig.terminaltype.toByte()
            Capability = StringUtil.toBytes(terminalConfig.terminalcapability)
            ExCapability = StringUtil.toBytes(terminalConfig.extendedterminalcapability)
            CountryCode = StringUtil.toBytes(terminalInfo.countryCode)
        }

        emvService.Emv_SetParam(emvParameter)

        ret = emvService.Emv_StartApp(EmvService.EMV_FALSE)
        logger.logErr("Emv Service Start Application: RET CODE ==== $ret")

        return ret
    }

    fun completeTransaction(response: TransactionResponse): Int {
        val authCode = response.authCode.toByteArray()
        val responseCode = response.responseCode.toByteArray()
        val authTLV = EmvTLV(6).apply { Value = authCode }
        val responseTLV = EmvTLV(2).apply { Value = responseCode }
        emvService.Emv_SetTLV(authTLV)
        emvService.Emv_SetTLV(responseTLV)

        return EmvService.EMV_TRUE
    }

    fun getCardType(): CardType {
        val aids = config.second
        val selected = aids.cards.firstOrNull { ::selectedRID.isInitialized && it.aid.startsWith(selectedRID) }
        val isCard = { type: CardType -> selected?.name?.startsWith(type.code, true) ?: false}

        var cardType = CardType.None
        // find matching card
        for (type in CardType.values()) {
            if (isCard(type)) cardType = type
        }

        // return type
        return cardType
    }

    fun getTLV(tag: Int): ByteArray? {
        val tlv = EmvTLV(tag)
        if (emvService.Emv_GetTLV(tlv) == EmvService.EMV_TRUE) {
            val tlvData = tlv.Value
            return Arrays.copyOfRange(tlvData, 0, tlvData.size)
        }
        return null
    }

    fun getTrack2() = getTLV(0x57)

    fun getPan(): String? {
        return getTrack2()?.let {
            val strTrack2 = StringUtil.toHexString(it).split("F")[0]
            return@let strTrack2.split("D")[0]
        }
    }

    private fun extractTags() {
        logger.log("---------------------------------------------")
        for (tag in REQUEST_TAGS) {
            val tlv = getTLV(tag.tag)
            val str = tlv?.let { StringUtil.toHexString(it) }
            logger.log("tag: ${tag.name}, hex: $str")
        }
        logger.log("---------------------------------------------")
    }

    internal fun getIccData(): String {
        val tagValues: MutableList<Pair<ICCData, ByteArray?>> = mutableListOf()

        for (tag in REQUEST_TAGS) {
            val tlv = getTLV(tag.tag)
            tagValues.add(Pair(tag, tlv))
        }

        return TelpoEmvUtils.buildIccString(tagValues)
    }

    fun ICCData.getTlv() {

    }

    inner class TelpoEmvServiceListener : EmvServiceListener() {

        override fun onInputAmount(amountData: EmvAmountData?): Int {
            amountData?.let {
                it.Amount = amount.toLong()
                it.TransCurrCode = 566.toShort() /*566 is Nigeria's currency number*/
                it.ReferCurrCode = 566.toShort()
                it.CashbackAmount = 0L
                it.TransactionType = 0.toByte()
                it.ReferCurrCon = 0
                it.ReferCurrExp = 0.toByte()
                it.TransCurrExp = 0.toByte()
            }
            return EmvService.EMV_TRUE
        }

        override fun onInputPin(pinData: EmvPinData?): Int {
            val pinParameter = PinParam(context)
            val pan = EmvTLV(0x5A)
            var ret = emvService.Emv_GetTLV(pan)
            if (ret == EmvService.EMV_TRUE) {
                val panValue = StringBuffer(StringUtil.toHexString(pan.Value))
                if (panValue[panValue.toString().length - 1] == 'F') {
                    panValue.deleteCharAt(panValue.toString().length - 1)
                }
                pinParameter.CardNo = panValue.toString()
                logger.logErr("This is the card number ======= $panValue")
            }
            pinParameter.apply {
                KeyIndex = 1
                WaitSec = 100
                MaxPinLen = 6
                MinPinLen= 4
                IsShowCardNo = 1
                Amount = "$amount"
            }

            PinpadService.Open(context)
            ret = PinpadService.TP_PinpadGetPin(pinParameter)
            when (ret) {
                PinpadService.PIN_ERROR_CANCEL -> return EmvService.ERR_USERCANCEL
                PinpadService.PIN_OK -> {
                    return if (StringUtil.toHexString(pinParameter.Pin_Block).contains("00000000")) {
                        EmvService.ERR_NOPIN
                    } else EmvService.EMV_TRUE
                }
            }

            return EmvService.EMV_TRUE
        }

        override fun onOnlineProcess(p0: EmvOnlineData?): Int = runBlocking {
            pinCallback.showPinOk()
            EmvService.EMV_TRUE
        }

        override fun onRequireDatetime(dataTime: ByteArray?): Int {
            val formatter = SimpleDateFormat.getDateTimeInstance()
            val date = Date(System.currentTimeMillis())
            val dateString = formatter.format(date)
            return try {
                val dateTimeByte = dateString.toByteArray(Charset.forName("ascii"))
                dataTime?.let {
                    System.arraycopy(dateTimeByte, 0, it, 0, it.size)
                }
                EmvService.EMV_TRUE
            } catch (exception: UnsupportedEncodingException) {
                exception.printStackTrace()
                logger.logErr(exception.localizedMessage)
                EmvService.EMV_FALSE
            }
        }

        override fun OnCheckException_qvsdc(index: Int, pan: String?): Int = EmvService.EMV_TRUE

        override fun onMir_Hint(): Int = 0

        override fun onFinishReadAppData(): Int = EmvService.EMV_TRUE

        override fun OnCheckException(PAN: String?): Int {
            cardPan = PAN ?: ""
            return EmvService.EMV_FALSE
        }

        override fun onReferProc(): Int = EmvService.EMV_TRUE

        override fun onMir_DataExchange(): Int = 0

        override fun onRequireTagValue(tag: Int, length: Int, value: ByteArray?): Int = EmvService.EMV_TRUE

        override fun onVerifyCert(): Int = EmvService.EMV_TRUE

        override fun onSelectApp(p0: Array<out EmvCandidateApp>?): Int = p0!![0].index.toInt()

        override fun onMir_FinishReadAppData(): Int = 0

        override fun onSelectAppFail(p0: Int): Int = EmvService.EMV_TRUE
    }
}