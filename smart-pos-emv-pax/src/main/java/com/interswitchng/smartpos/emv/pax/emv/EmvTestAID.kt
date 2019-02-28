package com.interswitchng.smartpos.emv.pax.emv

import com.interswitchng.smartpos.emv.pax.utilities.EmvUtils.str2Bcd
import com.pax.jemv.clcommon.EMV_APPLIST


class EmvTestAID(

        private val id: Int,
        /**
         * name
         */
        private val appName: String,
        /**
         * aid
         */
        private val aid: String,
        /**
         * (PART_MATCH | FULL_MATCH )
         */
        private val selFlag: Int,

        private val priority: Int,
        private val targetPer: Int,
        private val maxTargetPer: Int,
        private val randTransSel: Int,
        private val velocityCheck: Int,
        private val threshold: Long,
        private val tdol: String? = "0F9F02065F2A029A039C0195059F3704"
) {


    private val floorLimitCheck: Int = 1
    private val floorLimit: Long = 100
    private val tacDenial: String = "0010000000"
    private val tacOnline: String = "FCF8E4F880"
    private val tacDefault: String = "FCF0E40800"
    private val acquirerId: String = "000000123456"
    private val ddol: String = "9F3704"
    private val version: String = "0020"
    private val riskManageData: String? = null


    /***************************
     * EmvAidParam to AidParam
     */
    fun toAPPListItem(): EMV_APPLIST {
        val aidParam = EMV_APPLIST()
        aidParam.appName = appName.toByteArray()
        aidParam.aid = aid.let(::str2Bcd)
        aidParam.aidLen = aidParam.aid.size.toByte()
        aidParam.selFlag = selFlag.toByte()
        aidParam.priority = priority.toByte()
        aidParam.floorLimit = floorLimit
        aidParam.floorLimitCheck = floorLimitCheck.toByte()
        aidParam.threshold = threshold
        aidParam.targetPer = targetPer.toByte()
        aidParam.maxTargetPer = maxTargetPer.toByte()
        aidParam.randTransSel = randTransSel.toByte()
        aidParam.velocityCheck = velocityCheck.toByte()
        aidParam.tacDenial = tacDenial.let(::str2Bcd)
        aidParam.tacOnline = tacOnline.let(::str2Bcd)
        aidParam.tacDefault = tacDefault.let(::str2Bcd)
        aidParam.acquierId = acquirerId.let(::str2Bcd)
        aidParam.dDOL = ddol.let(::str2Bcd)
        aidParam.version = version.let(::str2Bcd)

        if (tdol != null) {
            aidParam.tDOL = tdol.let(::str2Bcd)
        }

        return aidParam
    }


}
