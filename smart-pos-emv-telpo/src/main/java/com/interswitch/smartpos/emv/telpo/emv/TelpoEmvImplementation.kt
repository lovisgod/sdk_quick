package com.interswitch.smartpos.emv.telpo.emv

import android.content.Context
import com.interswitch.smartpos.emv.telpo.TelpoPinCallback
import com.interswitch.smartpos.emv.telpo.utils.DefaultAPPCAPK
import com.interswitchng.smartpos.shared.models.core.TerminalInfo
import com.interswitchng.smartpos.shared.models.posconfig.EmvAIDs
import com.interswitchng.smartpos.shared.models.posconfig.TerminalConfig
import com.interswitchng.smartpos.shared.services.iso8583.utils.FileUtils
import com.interswitchng.smartpos.shared.utilities.Logger
import com.telpo.emv.EmvCAPK
import com.telpo.emv.EmvParam
import com.telpo.emv.EmvService
import com.telpo.tps550.api.util.StringUtil

internal class TelpoEmvImplementation (
    private val context: Context, pinCallback: TelpoPinCallback
) {

    private val emvParameter = EmvParam()
    private val logger = Logger.with("Telpo EMV Implementation")

    private val emvService = EmvService.getInstance()

    private val config: Pair<TerminalConfig, EmvAIDs> by lazy { FileUtils.getConfigurations(context) }

    private var amount: Int = 0

    fun setAmount(amount: Int) {
        this.amount = amount
    }

    private fun addCAPKIntoEmvLib(capks: List<EmvCAPK>) {
        EmvService.Emv_RemoveAllApp()
        DefaultAPPCAPK.Add_All_APP()
        EmvService.Emv_RemoveAllCapk()
        DefaultAPPCAPK.Add_All_CAPK()
    }

    private fun writeKeys(): Int {
//        var ret =
        return -1
    }

    suspend fun setupContactEmvTransaction(terminalInfo: TerminalInfo): Int {
        val terminalConfig = config.first
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

        return -1
    }
}