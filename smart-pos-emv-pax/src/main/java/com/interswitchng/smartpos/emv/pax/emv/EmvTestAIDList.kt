package com.interswitchng.smartpos.emv.pax.emv

import com.pax.jemv.clcommon.EMV_APPLIST
import java.util.*

object EmvTestAIDList {

    private val PART_MATCH = 0
    private val FULL_MATCH = 1


    private val EMV = EmvTestAID(
            0,
            "",
            "A0000000999090",
            PART_MATCH, 0, 0,
            0, 1, 1, 1)

    private val VISA_VSDC = EmvTestAID(
            0,
            "VISA CREDIT",
            "A0000000031010",
            PART_MATCH, 0, 0, 0, 1, 1, 1)

    private val VISA_ELECTRON = EmvTestAID(
            0,
            "VISA ELECTRON",
            "A0000000032010",
            PART_MATCH, 0, 0, 0, 1, 1, 1)

    private val VISA_ELECTRON2 = EmvTestAID(
            0,
            "VISA ELECTRON2",
            "A0000000033010",
            PART_MATCH, 0, 0, 0, 1, 1, 1)


    private val MASTER_MCHIP = EmvTestAID(
            0,
            "MCHIP",
            "A0000000041010",
            PART_MATCH, 0, 0, 0, 1, 1, 1
    )

    private val MASTER_MAESTRO = EmvTestAID(
            0,
            "MAESTRO",
            "A0000000043060",
            PART_MATCH, 0, 0, 0, 1, 1, 1
    )

    private val MASTER_MAESTRO_US = EmvTestAID(
            0,
            "MAESTRO",
            "A0000000042203",
            PART_MATCH, 0, 0, 0, 1, 1, 1
    )

    private val MASTER_CIRRUS = EmvTestAID(
            0,
            "CIRRUS",
            "A0000000046000",
            PART_MATCH, 0, 0, 0, 1, 1, 1
    )

    private val MCC_4 = EmvTestAID(
            0,
            "",
            "A0000000046010",
            PART_MATCH, 0, 0, 0, 1, 1, 1
    )

    private val MCC_5 = EmvTestAID(
            0,
            "",
            "A0000000101030",
            PART_MATCH, 0, 0, 0, 1, 1, 1
    )

    private val VERVE = EmvTestAID(
            0, "Verve",
            "A0000003710001",
            PART_MATCH,0, 0, 0, 0, 1, 1)


    fun genApplists(): List<EMV_APPLIST> {
        val applists = ArrayList<EMV_APPLIST>()
        applists.add(EMV.toAPPListItem())
        applists.add(VERVE.toAPPListItem())
        applists.add(VISA_VSDC.toAPPListItem())
        applists.add(VISA_ELECTRON.toAPPListItem())
        applists.add(VISA_ELECTRON2.toAPPListItem())
        applists.add(MASTER_MCHIP.toAPPListItem())
        applists.add(MASTER_MAESTRO.toAPPListItem())
        applists.add(MASTER_MAESTRO_US.toAPPListItem())
        applists.add(MASTER_CIRRUS.toAPPListItem())
        applists.add(MCC_4.toAPPListItem())
        applists.add(MCC_5.toAPPListItem())

        return applists
    }


}

