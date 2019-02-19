package com.interswitchng.smartpos.emv.pax.emv

import com.interswitchng.smartpos.emv.pax.utilities.EmvUtils.str2Bcd
import com.pax.jemv.clcommon.EMV_CAPK


open class EmvTestCAPK (
    var id: Int = 0,

    // registered application identifier
    var rid: String,
    // 密钥索引
    var keyID: Int,
    // HASH算法标志
    var hashInd: Int,
    // RSA算法标志
    var arithInd: Int,
    // 模
    var module: String?,
    // 指数
    var exponent: String?,
    // 有效期(YYMMDD)
    var expDate: String,
    // 密钥校验和
    var checkSum: String) {

    companion object {

        /********************************
         * EmvCapk to Capk
         */
        fun toCapk(readCapk: EmvTestCAPK): EMV_CAPK? {
            if (readCapk.module == null || readCapk.exponent == null)
                return null
            val capk = EMV_CAPK()
            capk.rID = str2Bcd(readCapk.rid)
            capk.keyID = readCapk.keyID.toByte()
            capk.hashInd = readCapk.hashInd.toByte()
            capk.arithInd = readCapk.arithInd.toByte()
            capk.modul = str2Bcd(readCapk.module!!)
            capk.modulLen = capk.modul.size.toShort()
            capk.exponent = str2Bcd(readCapk.exponent!!)
            capk.exponentLen = capk.exponent.size.toByte()
            capk.expDate = str2Bcd(readCapk.expDate)
            capk.checkSum = str2Bcd(readCapk.checkSum)

            return capk
        }
    }
}
