package com.interswitchng.smartpos.shared.models.kimono.request


import org.simpleframework.xml.Element
import org.simpleframework.xml.Root

@Root(name = "kmsg")
class CallHomeModel(_termInfo:TermInfoModel) {
    @Element(name = "terminfo")
     val termInfo = _termInfo

    @Element(name = "app")
     val app = "callhome"


}



