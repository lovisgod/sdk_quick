package com.interswitchng.smartpos.shared.services.kimono.models

import com.interswitchng.smartpos.shared.Constants
import org.simpleframework.xml.Element
import org.simpleframework.xml.Root

@Root(name = "kmsg", strict = false)
internal class NewKeyRequest {
    @Element(name = "cmd", required = false)
    var cmd: String? = ""

    @Element(name = "termId", required = false)
    var terminalId: String = " "

    @Element(name = "pkmod", required = false)
    var pkmod: String = ""

    @Element(name = "pkex", required = false)
    var pkex: String = ""

    @Element(name = "pkv", required = false)
    var pkv: String = ""

    @Element(name = "keyset_id", required = false)
    var keyset_id: String = ""

    @Element(name = "den_er", required = false)
    var den_er: String = ""


    companion object {
        fun create(terminalIdString: String) = NewKeyRequest().apply {

            return NewKeyRequest().apply {
                cmd = "key"
                terminalId = terminalIdString
                pkmod = Constants.PKMOD
                pkex = Constants.PKEX
                pkv = "1"
                keyset_id = "000002"
                den_er = "1"
            }
        }

        /*fun keyRequestString(){

        }*/
    }

}

