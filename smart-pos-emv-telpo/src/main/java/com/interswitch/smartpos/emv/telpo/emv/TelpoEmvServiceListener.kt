package com.interswitch.smartpos.emv.telpo.emv

import com.telpo.emv.*

abstract class TelpoEmvServiceListener : EmvServiceListener() {

    override fun onInputAmount(p0: EmvAmountData?): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onOnlineProcess(p0: EmvOnlineData?): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onRequireDatetime(p0: ByteArray?): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun OnCheckException_qvsdc(p0: Int, p1: String?): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onMir_Hint(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onFinishReadAppData(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun OnCheckException(p0: String?): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onReferProc(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onInputPin(p0: EmvPinData?): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onMir_DataExchange(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onRequireTagValue(p0: Int, p1: Int, p2: ByteArray?): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onVerifyCert(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onSelectApp(p0: Array<out EmvCandidateApp>?): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onMir_FinishReadAppData(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onSelectAppFail(p0: Int): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}