package com.interswitchng.smartpos.shared.services.kimono.models


import org.simpleframework.xml.Element
import org.simpleframework.xml.Root

@Root(name = "kmsg")
class CallHomeModel(_termInfo: TermInfoModel) {
    @Element(name = "terminfo")
     val termInfo = _termInfo

    @Element(name = "app")
     val app = "callhome"


}



