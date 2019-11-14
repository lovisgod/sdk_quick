package com.interswitchng.smartpos.shared.services.kimono.models

import org.simpleframework.xml.Element


 class TermInfoModel( _mid:String,_ttype:String,_tid:String, _uid:String,
                      _mloc:String, _batt:String, _tim:String,_csid:String,
                      _pstat: String,
                      _lang :String,
                      _poscondcode:String,
                      _posgeocode:String,
                      _currencycode:String
                      ) {

    @Element(name = "mid")
     var mid = _mid

    @Element(name = "ttype")
     val ttype = _ttype

    @Element(name = "tid")
     val tid = _tid

    @Element(name = "uid")
     val uid = _uid

    @Element(name = "mloc")
     val mloc = _mloc

    @Element(name = "batt")
     val batt = _batt


    @Element(name = "tim")
     val tim = _tim

    @Element(name = "csid")
     val csid = _csid



    @Element(name = "pstat")
     val pstat = _pstat

    @Element(name = "lang")
     val lang = _lang

    @Element(name = "poscondcode")
     val poscondcode = _poscondcode


    @Element(name = "posgeocode")
     val posgeocode = _posgeocode


    @Element(name = "currencycode")
     val currencycode = _currencycode

}