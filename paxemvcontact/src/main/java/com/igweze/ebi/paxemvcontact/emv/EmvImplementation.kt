package com.igweze.ebi.paxemvcontact.emv

import android.util.SparseArray
import com.igweze.ebi.paxemvcontact.utilities.EmvUtils
import com.igweze.ebi.paxemvcontact.utilities.EmvUtils.bcd2Str
import com.igweze.ebi.paxemvcontact.utilities.EmvUtils.bytes2String
import com.igweze.ebi.paxemvcontact.utilities.EmvUtils.str2Bcd
import com.interswitchng.interswitchpossdk.shared.models.TerminalInfo
import com.interswitchng.interswitchpossdk.shared.utilities.Logger
import com.pax.jemv.clcommon.ACType
import com.pax.jemv.clcommon.EMV_APPLIST
import com.pax.jemv.clcommon.OnlineResult
import com.pax.jemv.clcommon.RetCode
import com.pax.jemv.device.DeviceManager
import com.pax.jemv.emv.api.EMVCallback
import com.pax.jemv.emv.model.EmvEXTMParam
import com.pax.jemv.emv.model.EmvMCKParam
import com.pax.jemv.emv.model.EmvParam
import java.util.*

class EmvImplementation(private val pinCallback: PinCallback) {

    private val emvParameters = EmvParam()
    private val tlvs = SparseArray<ByteArray>()
    private val mckParameters = EmvMCKParam().also { it.extmParam = EmvEXTMParam() }
    private val emvCallback = EMVCallback.getInstance().also { it.setCallbackListener(Listener()) }
    private val logger = Logger.with("EMVImplementation")

    private var amount: Int = 0

    init {
        // set IDevice implementation
        DeviceManager.getInstance().setIDevice(DeviceImplNeptune.getInstance())
    }


    fun setAmount(amount: Int) {
        this.amount = amount
    }

    private fun addCAPKIntoEmvLib(): Int {

        var ret: Int
        val dataList = com.pax.jemv.clcommon.ByteArray()

        ret = EMVCallback.EMVGetTLVData(0x4F.toShort(), dataList)

        if (ret != RetCode.EMV_OK) {
            ret = EMVCallback.EMVGetTLVData(0x84.toShort(), dataList)
        }

        if (ret == RetCode.EMV_OK) {
            val rid = ByteArray(5)
            System.arraycopy(dataList.data, 0, rid, 0, 5)

            ret = EMVCallback.EMVGetTLVData(0x8F.toShort(), dataList)
            if (ret == RetCode.EMV_OK) {
                val keyId = dataList.data[0]
                logger.log("keyID=$keyId")

                for (capk in EmvTestCAPKList.genCapks()) {
                    if (bytes2String(capk!!.rID) == String(rid)) {
                        if (keyId.toInt() == -1 || capk.keyID == keyId) {

                            // log certified authority public key
                            logger.log("EMVGetTLVData rid=" + bcd2Str(capk.rID))
                            logger.log("EMVGetTLVData keyID=" + capk.keyID)
                            logger.log("EMVGetTLVData exponentLen=" + capk.exponentLen)
                            logger.log("EMVGetTLVData hashInd=" + capk.hashInd)
                            logger.log("EMVGetTLVData arithInd=" + capk.arithInd)
                            logger.log("EMVGetTLVData modulLen=" + capk.modulLen)
                            logger.log("EMVGetTLVData checkSum=" + bcd2Str(capk.checkSum))


                            // add mutual CAPK
                            ret = EMVCallback.EMVAddCAPK(capk)
                            logger.log("EMVAddCAPK ret=$ret")
                        }
                    }
                }
            }
        }
        return ret
    }

    fun setupContactEmvTransaction(terminalInfo: TerminalInfo): Int {

        // initialize emv library data elements
        val initResult = EMVCallback.EMVCoreInit()
        // ensure initialization succeeded
        if (initResult != RetCode.EMV_OK) return initResult

        // TODO figure out what this does
        EMVCallback.EMVSetCallback()


        // get default parameters
        EMVCallback.EMVGetParameter(emvParameters)

        // configure other parameters
        emvParameters.apply {
            capability = str2Bcd("E0F8C8") // str2Bcd("E000F0A001")
            countryCode = str2Bcd(terminalInfo.countryCode)
            transCurrCode = str2Bcd(terminalInfo.currencyCode)
            merchName = terminalInfo.merchantNameAndLocation.toByteArray()
            termId = terminalInfo.terminalId.toByteArray()
            merchId = terminalInfo.merchantId.toByteArray()
            forceOnline = 0
            terminalType = 34
            exCapability = str2Bcd("E000F0A001")
        }

        // set configured parameters
        EMVCallback.EMVSetParameter(emvParameters)

        // get kernel MCK default parameters
        EMVCallback.EMVGetMCKParam(mckParameters)
        // configure MCK parameters
        mckParameters.apply {
            ucBypassPin = 1
            ucBatchCapture = 1
            mckParameters.extmParam.apply {
                aucTermAIP = str2Bcd("0800")
                ucUseTermAIPFlg = 1
                ucBypassAllFlg = 1
            }
        }

        // set configured MCK parameters
        EMVCallback.EMVSetMCKParam(mckParameters)

        // set PCI mode to allow offline pin
        EMVCallback.EMVSetPCIModeParam(1, "4,5".toByteArray(), 60 * 1000)

        // remove all applications from terminal app list
        EMVCallback.EMVDelAllApp()

        // use test apps to find matching AID
        for (app in EmvTestAIDList.genApplists()) {
            val addResult = EMVCallback.EMVAddApp(app)

            if (addResult == RetCode.EMV_OK) logger.log("AddApp succeeded: aid - ${app.aid}")
            else return addResult.also { logger.logErr("AddApp failed: code - $addResult: aid - ${app.aid}") }
        }


        // TODO verify that DELAllCAPK is available
        // remove CAPKs
        for (capk in EmvTestCAPKList.genCapks())
            capk?.apply { EMVCallback.EMVDelCAPK(keyID, rID) }


        // add all CAPKs
        addCAPKIntoEmvLib()


        // show Input card info
        pinCallback.showInsertCard()


        // trigger app selection by specifying card slot and transactionCount
        val appSelectResult = EMVCallback.EMVAppSelect(0x00, 100)
        if (appSelectResult != RetCode.EMV_OK)
            return appSelectResult.also { logger.logErr("AppSelection Error: code - $appSelectResult") }


        // read app data from chip
        val readResult = EMVCallback.EMVReadAppData()
        if (readResult != RetCode.EMV_OK)
            return readResult.also { logger.logErr("ReadAppData Error: code - $readResult") }
        else extractTags()


        // authenticate card
        val cardAuthResult = EMVCallback.EMVCardAuth()
        if (cardAuthResult != RetCode.EMV_OK)
            return cardAuthResult.also { logger.log("EMVCardAuth Error: code - $cardAuthResult") }
        else extractTags()


        // get authentication results
        val errorCode = ByteArray(10)
        val debugInfoResult = EMVCallback.EMVGetDebugInfo(0, errorCode)

        if (debugInfoResult == RetCode.EMV_OK) {
            logger.log("EMVDebugInfo succeeded")
            logger.log("DebugResult ${bcd2Str(errorCode)}")
        } else logger.log("EMVDebugInfo Error: code - $debugInfoResult")


        return cardAuthResult
    }

    fun startContactEmvTransaction(): Int {
        // app cryptogram
        val ac = ACType()
        logger.log("Before EMVStartTransaction")
        logger.log("AC type - ${ac.type}")


        val startTransactionResult = EMVCallback.EMVStartTrans(amount.toLong(), 0, ac)

        logger.log("After EMVStartTransaction")
        logger.log("AC type - ${ac.type}")

        if (startTransactionResult != RetCode.EMV_OK)
            logger.logErr("StartTransaction error: code $startTransactionResult")
        else extractTags()


        return when (ac.type) {
            ACType.AC_TC,
            ACType.AC_AAC,
            ACType.AC_ARQC -> ac.type
            else -> startTransactionResult
        }
    }

    fun completeContactEmvTransaction(): Int {
        val authCode = "123456".toByteArray()

        EMVCallback.EMVSetTLVData(0x89, authCode, 6)
        EMVCallback.EMVSetTLVData(0x8A, "00".toByteArray(), 2)

        val script = str2Bcd("9F1804AABBCCDD86098424000004AABBCCDD86098418000004AABBCCDD86098416000004AABBCCDD")
        val response = OnlineResult.ONLINE_APPROVE
        val ac = ACType()

        val completionResult = EMVCallback.EMVCompleteTrans(response, script, script.size, ac)
        if (completionResult != RetCode.EMV_OK) return completionResult.also { logger.logErr("Complete Transaction Error: code: $completionResult") }


        val datalist = com.pax.jemv.clcommon.ByteArray(5)
        EMVCallback.EMVGetTLVData(0x95, datalist)
        logger.log("TLV - TVR 0x95: ${bcd2Str(datalist.data)}")

        EMVCallback.EMVGetTLVData(0x9B, datalist)
        logger.log("TLV - TVR 0x9B: ${bcd2Str(datalist.data)}")

        logger.log("AC_Type = ${ac.type}")

        // TODO remove from demo
        ac.type = ACType.AC_TC
        if (ac.type == ACType.AC_TC) return TransactionResult.EMV_ONLINE_APPROVED


        return completionResult
    }

    fun enterPin(isOnline: Boolean, offlineTriesLeft: Int, pan: String) {
        logger.log("offline tries left: $offlineTriesLeft")
        pinCallback.enterPin(isOnline, offlineTriesLeft, pan)
    }


    inner class Listener : EMVCallback.EmvCallbackListener {

        override fun emvInputAmount(amountInfo: LongArray) {
            amountInfo[0] = amount.toLong()
            emvCallback.setCallBackResult(RetCode.EMV_OK)

            val bytes = str2Bcd(Integer.toHexString(amount.toInt()))
            val resultAmt = EMVCallback.EMVSetTLVData(ICCData.TRANSACTION_AMOUNT.tag.toShort(), bytes, bytes.size)
            if (resultAmt != RetCode.EMV_OK) logger.log("Error setting amount TLV: code - $resultAmt")
        }

        override fun emvVerifyPINfailed(pinInfo: ByteArray): Int {
            logger.log("Verify pin failed")

            // return 1 to cancel or 0 to retry
            return 0
        }


        override fun emvWaitAppSel(retryCount: Int, appList: Array<out EMV_APPLIST>, appNumber: Int) {
            emvCallback.setCallBackResult(RetCode.EMV_OK)
        }

        override fun emvSetParam(): Int {
            logger.log("certVerify: For PBOC, so not needed")
            return RetCode.EMV_OK
        }

        override fun emvGetHolderPwd(tryFlag: Int, remainCount: Int, pin: ByteArray?) {
            if (pin == null) {
                logger.log("emvGetHolderPwd pin is null, tryFlag=$tryFlag remainCnt:$remainCount")
            } else {
                logger.log("emvGetHolderPwd pin is not null, tryFlag=$tryFlag remainCnt:$remainCount")
            }

            var ret: Int

            if (pin != null && pin[0].toInt() != 0) {
                ret = pin[0].toInt()
                logger.log("emvGetHolderPwd ret=$ret")
            } else {

                val pan = getPan()!!
                enterPin(pin == null, remainCount, pan)
                logger.log("emvGetHolderPwd enterPin finish")

                ret = pinCallback.getPinResult(pan)
                logger.log("GetPinEmv GetPinResult = $ret")
            }

            emvCallback.setCallBackResult(ret)
        }

        override fun emvVerifyPINOK() {
            pinCallback.showPinOk()
            logger.log("certVerify: For PBOC, so not needed")
        }


        override fun emvUnknowTLVData(tag: Short, data: com.pax.jemv.clcommon.ByteArray): Int {
            // copy value into data reference, based on specified value
            when (tag.toInt()) {
                0x9A -> {
                    val date = ByteArray(7)
                    DeviceManager.getInstance().getTime(date)
                    System.arraycopy(date, 1, data.data, 0, 3)
                }
                0x9F1E -> {
                    val sn = ByteArray(10)
                    DeviceManager.getInstance().readSN(sn)
                    System.arraycopy(sn, 0, data.data, 0, Math.min(data.data.size, sn.size))
                }
                0x9F21 -> {
                    val time = ByteArray(7)
                    DeviceManager.getInstance().getTime(time)
                    System.arraycopy(time, 4, data.data, 0, 3)
                }
                0x9F37 -> {
                    //byte[] random = TradeApplication.dal.getSys().getRandom(data.data.length);
                    //System.arraycopy(random, 0, data.data, 0, data.data.length);
                    val random = ByteArray(4)
                    DeviceManager.getInstance().getRand(random, 4)
                    System.arraycopy(random, 0, data.data, 0, data.data.size)
                }
                0xFF01 -> Arrays.fill(data.data, 0x00.toByte())
                else -> return RetCode.EMV_PARAM_ERR
            }

            data.length = data.data.size

            return RetCode.EMV_OK
        }

        override fun certVerify() {
            logger.log("certVerify: For PBOC, so not needed")
            emvCallback.setCallBackResult(RetCode.EMV_OK)
        }

        override fun emvAdviceProc() {
            logger.log("certVerify: For PBOC, so not needed")
        }


        override fun cRFU2(): Int = RetCode.EMV_OK
    }

    fun getTlv(tag: Int): ByteArray? {
        val byteArray = com.pax.jemv.clcommon.ByteArray()
        if (EMVCallback.EMVGetTLVData(tag.toShort(), byteArray) == RetCode.EMV_OK) {
            val data = Arrays.copyOfRange(byteArray.data, 0, byteArray.length)
            logger.log("asd" + Integer.toHexString(tag) + ":" + data.size)
            return data
        }
        return null
    }

    fun getTrack2() = getTlv(0x57)

    fun getPan(): String? {
        return getTrack2()?.let {
            val strTrack2 = bcd2Str(it).split("F")[0]
            return@let strTrack2.split("D")[0]
        }
    }

    private fun extractTags() {
        logger.log("---------------------------------------------")
        for (tag in REQUEST_TAGS) {
            val tlv = getTlv(tag.tag)
            val str = tlv?.let { bcd2Str(it) }
            logger.log("tag: ${tag.name}, hex: $str")
        }
        logger.log("---------------------------------------------")
    }

    internal fun getIccData(): String {
        val tagValues: MutableList<Pair<Int, ByteArray?>> = mutableListOf()

        for (tag in REQUEST_TAGS) {
            val tlv = getTlv(tag.tag)
            tagValues.add(Pair(tag.tag, tlv))
        }

        return EmvUtils.buildIccString(tagValues)
    }

}