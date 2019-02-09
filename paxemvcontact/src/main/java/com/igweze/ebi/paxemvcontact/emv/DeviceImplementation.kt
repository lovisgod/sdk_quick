package com.igweze.ebi.paxemvcontact.emv

import com.pax.jemv.device.IDevice
import com.pax.jemv.device.model.ApduRespL2
import com.pax.jemv.device.model.ApduSendL2
import com.pax.jemv.device.model.RsaPinKeyL2

class DeviceImplementation: IDevice {
    override fun des(p0: ByteArray?, p1: ByteArray?, p2: ByteArray?, p3: Int) {

    }

    override fun rsaRecover(p0: ByteArray?, p1: Int, p2: ByteArray?, p3: Int, p4: ByteArray?, p5: ByteArray?): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setPinInputParam(p0: ByteArray?, p1: Long): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun fReadData(p0: Int, p1: ByteArray?, p2: Int): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun readSN(p0: ByteArray?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setControlParam(p0: ByteArray?): Byte {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setIccSlot(p0: Byte) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getRand(p0: ByteArray?, p1: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getTickCount(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun hash(p0: ByteArray?, p1: Int, p2: ByteArray?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun iccReset(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun fInitiate(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun iccSetTxnIF(p0: Int): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setDebug(p0: Byte, p1: Byte) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun timerCheck(p0: Byte): Short {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun iccCommand(p0: ApduSendL2?, p1: ApduRespL2?): Byte {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun sm2Verify(p0: Byte, p1: ByteArray?, p2: Int, p3: ByteArray?, p4: Int): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getTime(p0: ByteArray?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setCancelKey(p0: Byte): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun timerSet(p0: ByteArray?, p1: Short) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun delayMs(p0: Short) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun sm3(p0: ByteArray?, p1: Int, p2: ByteArray?): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun fRemove(p0: Int): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun iccGetTxnIF(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun aes(p0: ByteArray?, p1: ByteArray?, p2: ByteArray?, p3: Int, p4: Int): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun pedVerifyCipherPin(p0: RsaPinKeyL2?, p1: ByteArray?, p2: Byte): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun fWriteData(p0: Int, p1: ByteArray?, p2: Int): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun pedVerifyPlainPin(p0: ByteArray?, p1: Byte): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}