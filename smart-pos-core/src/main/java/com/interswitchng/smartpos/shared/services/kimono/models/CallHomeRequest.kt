package com.interswitchng.smartpos.shared.services.kimono.models


import com.interswitchng.smartpos.IswPos
import com.interswitchng.smartpos.shared.models.core.Environment
import com.interswitchng.smartpos.shared.models.core.TerminalInfo
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.request.TransactionInfo
import com.interswitchng.smartpos.shared.services.iso8583.utils.DateUtils
import org.simpleframework.xml.Element
import org.simpleframework.xml.Root
import java.util.*

@Root(name = "kmsg" , strict = false)
internal  class CallHomeRequest() {
    @Element(name = "terminfo", required = false)
     var termInfo:TermInfoModel ? =null

    @Element(name = "app" , required = false)
    var app = "callhome"


    companion object {
        fun create(deviceName: String, terminalInfo: TerminalInfo, uidString:String)= CallHomeRequest().apply {

            return CallHomeRequest().apply {
                  termInfo= TermInfoModel.create(deviceName, terminalInfo,uidString)

            }
        }
    }

}


internal  class TermInfoModel {

    @Element(name = "mid", required = false)
    var mid = ""

    @Element(name = "ttype", required = false)
    var ttype = ""

    @Element(name = "tid", required = false)
    var tid = ""

    @Element(name = "uid", required = false)
    var uid = ""

    @Element(name = "mloc", required = false)
    var mloc = ""

    @Element(name = "batt", required = false)
    var batt = ""


    @Element(name = "tim", required = false)
    var tim = ""

    @Element(name = "csid", required = false)
    var csid = ""



    @Element(name = "pstat", required = false)
    var pstat = ""

    @Element(name = "lang", required = false)
    var lang = ""

    @Element(name = "poscondcode", required = false)
    var poscondcode = ""


    @Element(name = "posgeocode", required = false)
    var posgeocode = ""


    @Element(name = "currencycode", required = false)
    var currencycode = ""


    companion object {
        fun create(deviceName:String,terminalInfo: TerminalInfo, uidString:String)= TermInfoModel().apply {
            val battery = "-1"
            val date = DateUtils.universalDateFormat.format(Date())
            return TermInfoModel() .apply {
                batt = battery
                currencycode = terminalInfo.currencyCode
                lang = "EN"
                mid = terminalInfo.merchantId
                mloc = terminalInfo.merchantNameAndLocation
                poscondcode = "00"
//                posEntryMode = "051"
                tid = terminalInfo.terminalId
                uid = "00000${uidString}"
                ttype = deviceName.toUpperCase()
//                cardAcceptorId = terminalInfo.merchantId
                pstat="1"
                tim=date //time e.g 2019/04/004 16:44:00
                csid = "00"
                posgeocode = "00234000000000566"
            }
        }
    }

}


