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
import com.telpo.tps550.api.util.StringUtil

internal class TelpoEmvImplementation (
    private val context: Context, pinCallback: TelpoPinCallback
) {

    private val emvParameter = EmvParam()
    private val logger = Logger.with("Telpo EMV Implementation")

    private lateinit var selectedRID: String

    private val emvService = EmvService.getInstance()

    private val config: Pair<TerminalConfig, EmvAIDs> by lazy { FileUtils.getConfigurations(context) }

    private var amount: Int = 0

    fun setAmount(amount: Int) {
        this.amount = amount
    }

    private fun addCAPKIntoEmvLib(capks: List<EmvCAPK>) {
        capks.forEach { capk ->
            EmvService.Emv_AddCapk(capk)
            selectedRID = StringUtil.toHexString(capk.RID)
        }
    }

    private fun writeKeys(): Int {
        return EmvService.EMV_TRUE
    }

    suspend fun setupContactEmvTransaction(terminalInfo: TerminalInfo): Int {
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

        val appList = TelpoEmvUtils.createAppList(config.first, config.second)
        EmvService.Emv_RemoveAllApp()
        appList.forEach { emvApp ->
            EmvService.Emv_AddApp(emvApp)
        }

        val capks = config.second.getAllCapks()
        EmvService.Emv_RemoveAllCapk()
        addCAPKIntoEmvLib(capks)

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

        return ret
    }

    fun startContactEmvTransaction(): Int {
        var ret: Int = EmvService.IccOpenReader()
        logger.logErr("Open Reader: RET CODE ==== $ret")

        ret = EmvService.IccCheckCard(300)
        logger.logErr("Check Card: RET CODE ==== $ret")

        ret = EmvService.IccCard_Poweron()
        logger.logErr("Power On: RET CODE ==== $ret")

        ret = emvService.Emv_TransInit()
        logger.logErr("Initialize transaction: RET CODE ==== $ret")

        ret = emvService.Emv_StartApp(0)
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
}