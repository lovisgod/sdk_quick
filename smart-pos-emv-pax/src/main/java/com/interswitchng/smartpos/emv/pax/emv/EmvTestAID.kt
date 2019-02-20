package com.interswitchng.smartpos.emv.pax.emv

import com.interswitchng.smartpos.emv.pax.utilities.EmvUtils.str2Bcd
import com.pax.jemv.clcommon.EMV_APPLIST


class EmvTestAID(

        var id: Int,
        /**
         * name
         */
        var appName: String?,
        /**
         * aid, 应用标志
         */
        var aid: String?,
        /**
         * 选择标志(PART_MATCH 部分匹配 FULL_MATCH 全匹配)
         */
        var selFlag: Int,
        /**
         * priority
         */
        var priority: Int,

        /**
         * 目标百分比数
         */
        var targetPer: Int,
        /**
         * 最大目标百分比数
         */
        var maxTargetPer: Int,
        /**
         * 是否检查最低限额
         */
        var floorLimitCheck: Int,
        /**
         * 是否进行随机交易选择
         */
        var randTransSel: Int,
        /**
         * 是否进行频度检测
         */
        var velocityCheck: Int,
        /**
         * 最低限额
         */
        var floorLimit: Long,
        /**
         * 阀值
         */
        var threshold: Long,
        /**
         * 终端行为代码(拒绝)
         */
        var tacDenial: String?,
        /**
         * 终端行为代码(联机)
         */
        var tacOnline: String?,
        /**
         * 终端行为代码(缺省)
         */
        var tacDefault: String?,
        /**
         * 收单行标志־
         */
        var acquirerId: String?,
        /**
         * 终端缺省DDOL
         */
        var ddol: String?,
        /**
         * 终端缺省TDOL
         */
        var tdol: String?,
        /**
         * 应用版本
         */
        var version: String?,
        /**
         * 风险管理数据
         */
        var riskManageData: String?) {




        /***************************
         * EmvAidParam to AidParam
         */
    fun toAPPListItem(): EMV_APPLIST {
        val aidParam = EMV_APPLIST()
        aidParam.appName = appName!!.toByteArray()
        aidParam.aid = aid?.let(::str2Bcd)
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
        aidParam.tacDenial = tacDenial?.let(::str2Bcd)
        aidParam.tacOnline = tacOnline?.let(::str2Bcd)
        aidParam.tacDefault = tacDefault?.let(::str2Bcd)
        if (acquirerId != null) {
            aidParam.acquierId = acquirerId?.let(::str2Bcd)
        }
        if (ddol != null) {
            aidParam.dDOL = ddol?.let(::str2Bcd)
        }
        if (tdol != null) {
            aidParam.tDOL = tdol?.let(::str2Bcd)
        }
        aidParam.version = version?.let(::str2Bcd)
        if (riskManageData != null) {
            aidParam.riskManData = riskManageData?.let(::str2Bcd)
        }
        return aidParam
    }


}
